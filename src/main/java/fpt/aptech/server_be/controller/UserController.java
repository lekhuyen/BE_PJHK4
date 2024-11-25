package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.request.UserCreationRequest;
import fpt.aptech.server_be.dto.request.UserUpdateRequest;
import fpt.aptech.server_be.dto.response.UserResponse;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.service.OCRService;
import fpt.aptech.server_be.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;
    OCRService ocrService;

    @PostMapping("/addImage/{userId}")
//    ResponseEntity<String>
    public void getImageToString(@PathVariable String userId, @RequestParam MultipartFile file) throws TesseractException, IOException {
//        return new ResponseEntity<>(ocrService.getImageString(file), HttpStatus.OK);
        ocrService.getImageString(userId,file);
    }

    @PostMapping
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));

       return ApiResponse.<User>builder()
               .code(apiResponse.getCode())
               .message("Register successfully")
//               .result(apiResponse.getResult())
               .build();
    }

    @GetMapping
    List<UserResponse> getAllUsers() {
       var authentication = SecurityContextHolder.getContext().getAuthentication();

       //log thong tin user
       log.info("user name: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority ->
                log.info(grantedAuthority.getAuthority()));

        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    UserResponse getUserById(@PathVariable String userId) {

        return userService.getUserById(userId);
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {

        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    boolean updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);

        return "User deleted";
    }

}
