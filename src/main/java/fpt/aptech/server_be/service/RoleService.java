package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.request.RoleRequest;
import fpt.aptech.server_be.dto.response.RoleResponse;
import fpt.aptech.server_be.mapper.RoleMapper;
import fpt.aptech.server_be.repositories.PermissionRepository;
import fpt.aptech.server_be.repositories.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    public RoleResponse create(RoleRequest request) {
        var role = RoleMapper.toRole(request);

       var permissions = permissionRepository.findAllById(request.getPermissions());

       role.setPermissions(new HashSet<>(permissions));

      role = roleRepository.save(role);

      return RoleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(RoleMapper::toRoleResponse)
                .toList();
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
