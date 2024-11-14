package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.request.PermissionRequest;
import fpt.aptech.server_be.dto.response.PermissionResponse;
import fpt.aptech.server_be.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/permissions")
public class PermissionController {

    PermissionService permissionService;
    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.findAll())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable("permission") String permission) {
        permissionService.delete(permission);

        return ApiResponse.<Void>builder().build();
    }
}
