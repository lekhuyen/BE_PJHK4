package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.request.UserCreationRequest;
import fpt.aptech.server_be.dto.request.UserUpdateRequest;
import fpt.aptech.server_be.dto.response.UserResponse;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.exception.AppException;
import fpt.aptech.server_be.exception.ErrorCode;
import fpt.aptech.server_be.mapper.UserMapper;
import fpt.aptech.server_be.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;

    public User createUser(UserCreationRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = UserMapper.toUser(request);

       return userRepository.save(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::toUserResponse).collect(Collectors.toList());
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toUserResponse(user);
    }

    public boolean updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());
        user.setEmail(request.getEmail());

        User userUpdated = userRepository.save(user);

        return userUpdated != null;
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

}
