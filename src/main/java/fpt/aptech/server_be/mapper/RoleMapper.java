package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.PermissionRequest;
import fpt.aptech.server_be.dto.request.RoleRequest;
import fpt.aptech.server_be.dto.response.PermissionResponse;
import fpt.aptech.server_be.dto.response.RoleResponse;
import fpt.aptech.server_be.entities.Permission;
import fpt.aptech.server_be.entities.Role;
import org.mapstruct.Mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public class RoleMapper {
    public static Role toRole(RoleRequest request){
        Role role = new Role();

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        return role;
    }
    public static RoleResponse toRoleResponse(Role request){

        Set<PermissionResponse> permissionResponses = request.getPermissions().stream()
                .map(permission -> new PermissionResponse(
                        permission.getName(),
                        permission.getDescription()
                )).collect(Collectors.toSet());

        return new RoleResponse(
                request.getName(),
                request.getDescription(),
                permissionResponses
        );
    }

}
