package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.request.UserCreationRequest;
import fpt.aptech.server_be.dto.request.UserUpdateRequest;
import fpt.aptech.server_be.dto.response.PageResponse;
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
    ApiResponse<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "3") int size
    ) {
       var authentication = SecurityContextHolder.getContext().getAuthentication();

       //log thong tin user
       log.info("user name: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority ->
                log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(0)
                .result(userService.getAllUsers(page, size))
                .build();
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
    public ApiResponse<Boolean> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);

        return ApiResponse.<Boolean>builder()
                .code(0)
                .message("User deleted")
                .build();
    }

    @PutMapping("/status/{id}")
    public ApiResponse<Boolean> updateUserStatus(@PathVariable String id) {
        boolean isUpdate =  userService.updateStatus(id);
        if(isUpdate) {
            return ApiResponse.<Boolean> builder()
                    .code(0)
                    .message("Update user active successfully")
                    .build();
        }
        return ApiResponse.<Boolean> builder()
                .code(1)
                .message("Update user active failed")
                .build();
    }

}
