package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.request.UserCreationRequest;
import fpt.aptech.server_be.dto.request.UserUpdateRequest;
import fpt.aptech.server_be.dto.response.PageResponse;
import fpt.aptech.server_be.dto.response.UserResponse;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.enums.Role;
import fpt.aptech.server_be.exception.AppException;
import fpt.aptech.server_be.exception.ErrorCode;
import fpt.aptech.server_be.mapper.UserMapper;
import fpt.aptech.server_be.repositories.RoleRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;


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
}
