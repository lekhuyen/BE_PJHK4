package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.*;
import fpt.aptech.server_be.dto.response.PageResponse;
import fpt.aptech.server_be.dto.response.UserCitizenResponse;
import fpt.aptech.server_be.dto.response.UserResponse;
import fpt.aptech.server_be.entities.Address;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.service.OCRService;
import fpt.aptech.server_be.service.UserService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
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

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok("OTP has been sent to your email");
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = userService.verifyOTP(email, otp);
        return isValid ? ResponseEntity.ok("OTP is valid") : ResponseEntity.badRequest().body("Invalid OTP");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
        userService.resetPassword(email, otp, newPassword);
        return ResponseEntity.ok("Password has been reset successfully");
    }



    //truong
    @PostMapping("/add-address")
    public ResponseEntity<?> addAddress(@RequestBody AddressRequest request) {
        try {
            System.out.println("📢 Nhận request thêm địa chỉ: " + request);

            if (request.getAddress().trim().isEmpty() || request.getZip().trim().isEmpty() || request.getPhone().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Address, ZIP hoặc Phone không được để trống\"}");
            }

            boolean success = userService.addAddress(request.getUserId(), request.getAddress(), request.getZip(), request.getPhone());

            if (success) {
                return ResponseEntity.ok().body("{\"message\": \"Address added successfully\"}");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Failed to add address\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal Server Error\"}");
        }
    }


    @GetMapping("/address/{userId}")
    public ResponseEntity<?> getUserAddresses(@PathVariable String userId) {
        try {
            List<Address> addresses = userService.getUserAddresses(userId);
            if (addresses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"No addresses found\"}");
            }
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal Server Error\"}");
        }
    }

    @PostMapping("/verify-citizen")
    ApiResponse<Boolean> verifyCitizen(@RequestBody UserCitizenRequest request) {

        return ApiResponse.<Boolean>builder()
                .message("Verify successfully")
               .result(userService.citizen(request))
                .build();
    }

    @GetMapping("/user-citizen/{userId}")
    ApiResponse<UserCitizenResponse> getUserCitizenByUser(@PathVariable("userId") String userId) {

        return ApiResponse.<UserCitizenResponse>builder()
                .message("Get successfully")
                .result(userService.getUserCitizenByUserId(userId))
                .build();
    }
}
