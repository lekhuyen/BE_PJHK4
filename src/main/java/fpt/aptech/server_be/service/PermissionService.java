package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.request.PermissionRequest;
import fpt.aptech.server_be.dto.response.PermissionResponse;
import fpt.aptech.server_be.entities.Permission;
import fpt.aptech.server_be.mapper.PermissionMapper;
import fpt.aptech.server_be.repositories.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;

   public PermissionResponse create(PermissionRequest request) {
        Permission permission = PermissionMapper.toPermission(request);
       permission = permissionRepository.save(permission);

       return PermissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> findAll() {
        var permissions = permissionRepository.findAll();
       return permissions.stream().map(PermissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }

}
