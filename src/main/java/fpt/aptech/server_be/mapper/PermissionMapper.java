package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.PermissionRequest;
import fpt.aptech.server_be.dto.response.PermissionResponse;
import fpt.aptech.server_be.entities.Permission;
import org.mapstruct.Mapper;

@Mapper
public class PermissionMapper {
    public static Permission toPermission(PermissionRequest request){
        Permission permission = new Permission();

        permission.setName(request.getName());
        permission.setDescription(request.getDescription());

        return permission;
    }
    public static PermissionResponse toPermissionResponse(Permission request){

        PermissionResponse permissionResponse = new PermissionResponse();
        permissionResponse.setName(request.getName());
        permissionResponse.setDescription(request.getDescription());

        return permissionResponse;
    }

}
