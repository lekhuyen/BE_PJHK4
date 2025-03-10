package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.request.UserCitizenRequest;
import fpt.aptech.server_be.dto.request.UserCreationRequest;
import fpt.aptech.server_be.dto.request.UserUpdateRequest;
import fpt.aptech.server_be.dto.response.PageResponse;
import fpt.aptech.server_be.dto.response.UserCitizenResponse;
import fpt.aptech.server_be.dto.response.UserResponse;
import fpt.aptech.server_be.entities.Address;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.entities.UserCitizen;
import fpt.aptech.server_be.enums.Role;
import fpt.aptech.server_be.exception.AppException;
import fpt.aptech.server_be.exception.ErrorCode;
import fpt.aptech.server_be.mapper.UserMapper;
import fpt.aptech.server_be.repositories.RoleRepository;
import fpt.aptech.server_be.repositories.UserCitizenRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    UserCitizenRepository userCitizenRepository;

    @Autowired
    private JavaMailSender mailSender;

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();



    public User createUser(UserCreationRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = UserMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

//        HashSet<String> roles = new HashSet<>();
//        roles.add(Role.USER.name());

//        user.setRoles(roles);

       return userRepository.save(user);
    }



//    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('APPROVE_POST')")
    public PageResponse<UserResponse> getAllUsers(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "updatedAt");
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        Page<User> users = userRepository.findAll(pageRequest);
//        users.sort(Comparator.comparing(User::getUpdatedAt).reversed());
        return PageResponse.<UserResponse> builder()
                .currentPage(page)
                .pageSize(users.getSize())
                .totalPages(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .data(users.getContent().stream().map(UserMapper::toUserResponse).collect(Collectors.toList()))
                .build();
    }

    //user chi lay dc thong tin cua chinh minh
//    @PostAuthorize("returnObject.email == authentication.name")
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toUserResponse(user);
    }

    public UserResponse getMyInfo() {
       var context = SecurityContextHolder.getContext();
      String email = context.getAuthentication().getName();

       User user = userRepository.findByEmail(email).orElseThrow(()
               -> new AppException(ErrorCode.USER_NOT_EXISTED));

       return UserMapper.toUserResponse(user);
    }

    public boolean updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setFirstName(request.getFirstName());
//        user.setLastName(request.getLastName());
        user.setDob(request.getDob());
        user.setEmail(request.getEmail());

        var roles = roleRepository.findAllById(request.getRoles());

        user.setRoles(new HashSet<>(roles));

        User userUpdated = userRepository.save(user);

        return userUpdated != null;
    }

    public boolean deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if(!Objects.isNull(user)){
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    public boolean updateStatus(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if(!Objects.isNull(user)) {
            if(user.getIsActive() != null) {
                boolean updateStatus = !user.getIsActive();
                user.setIsActive(updateStatus);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

//    lay OTP
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if(!Objects.isNull(user)) {
            String otp = generateOTP();

            otpStorage.put(email, otp);

            sendOTP(email, otp);
        }

    }

    public boolean verifyOTP(String email, String otp) {
        return otpStorage.containsKey(email) && otpStorage.get(email).equals(otp);
    }

    public void resetPassword(String email, String otp, String newPassword) {
        if (!verifyOTP(email, otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpStorage.remove(email);
    }

    public String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    public void sendOTP(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Password Reset OTP");
            helper.setText("Your OTP for password reset is: " + otp, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }


    //trường
    //trường
    public boolean addAddress(String userId, String address, String zip, String phone) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            System.out.println("🚨 Lỗi: Không tìm thấy userId: " + userId);
            return false;
        }
        if (address.trim().isEmpty() || zip.trim().isEmpty() || phone.trim().isEmpty()) {
            System.out.println("🚨 Lỗi: Address, ZIP hoặc Phone không được để trống!");
            return false;
        }
        Address newAddress = new Address();
        newAddress.setUser(user);
        newAddress.setAddress(address);
        newAddress.setZip(zip);
        newAddress.setPhone(phone);

        user.getAddresses().add(newAddress);
        userRepository.save(user);
        return true;
    }
    public List<Address> getUserAddresses(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            System.out.println("🚨 Lỗi: Không tìm thấy userId: " + userId);
            return new ArrayList<>();
        }
        if (user.getAddresses() == null) {
            System.out.println("🚨 Lỗi: Danh sách địa chỉ của user rỗng!");
            return new ArrayList<>();
        }
        System.out.println("📢 Đang trả về danh sách địa chỉ: " + user.getAddresses().size());
        return user.getAddresses();
    }
    public boolean deleteAddress(String userId, int id) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            System.out.println("🚨 Lỗi: Không tìm thấy userId: " + userId);
            return false;
        }

        List<Address> addresses = user.getAddresses();
        Address addressToDelete = addresses.stream()
                .filter(address -> address.getId() == (id))  // ✅ So sánh với Long
                .findFirst()
                .orElse(null);

        if (addressToDelete == null) {
            System.out.println("🚨 Lỗi: Không tìm thấy địa chỉ với ID: " + id);
            return false;
        }

        addresses.remove(addressToDelete);
        userRepository.save(user);
        System.out.println("✅ Đã xóa địa chỉ thành công!");
        return true;
    }



    public Boolean citizen(UserCitizenRequest request){
        UserCitizen existCICode = userCitizenRepository.findByAndCiCode(request.getCiCode());
        if(existCICode != null){
            throw new AppException(ErrorCode.SAME_CITIZEN);
        }
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if(user != null){
            UserCitizen userCitizen  = new UserCitizen();
            userCitizen.setCiCode(request.getCiCode());
            userCitizen.setFullName(request.getFullName());
            userCitizen.setAddress(request.getAddress());
            userCitizen.setStartDate(request.getStartDate());
            userCitizen.setBirthDate(request.getBirthDate());
            userCitizen.setUser(user);

            user.setIsVerify(true);
            userCitizenRepository.save(userCitizen);
            userRepository.save(user);


            return true;
        }
        return false;
    }

    public UserCitizenResponse getUserCitizenByUserId(String userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if(user != null){
            UserCitizen userCitizen = userCitizenRepository.findByAndOrderByUser(user);
            UserCitizenResponse userCitizenResponse = new UserCitizenResponse();
            userCitizenResponse.setId(userCitizen.getId());
            userCitizenResponse.setCiCode(userCitizen.getCiCode());
            userCitizenResponse.setFullName(userCitizen.getFullName());
            userCitizenResponse.setAddress(userCitizen.getAddress());
            userCitizenResponse.setStartDate(userCitizen.getStartDate());
            userCitizenResponse.setBirthDate(userCitizen.getBirthDate());
            userCitizenResponse.setUserId(userId);
            return userCitizenResponse;
        }
        return null;
    }
}
