package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.UserCreationRequest;
import fpt.aptech.server_be.dto.request.UserUpdateRequest;
import fpt.aptech.server_be.dto.response.PermissionResponse;
import fpt.aptech.server_be.dto.response.RoleResponse;
import fpt.aptech.server_be.dto.response.UserCitizenResponse;
import fpt.aptech.server_be.dto.response.UserResponse;
import fpt.aptech.server_be.entities.Role;
import fpt.aptech.server_be.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public class UserMapper {
    public static User toUser(UserCreationRequest request){
        User user = new User();

        user.setName(request.getName());
        user.setPassword(request.getPassword());
        user.setDob(request.getDob());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setMoney(request.getMoney());

        return user;
    }
    public static UserResponse toUserResponse(User user){

        if(user == null){
            return null;
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setActive(user.getIsActive() != null ? user.getIsActive() : false);
        userResponse.setDob(user.getDob());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setCiNumber(user.getCiNumber());
        userResponse.setAddress(user.getAddress());
        userResponse.setIsVerify(user.getIsVerify() != null ? user.getIsVerify() : false);
        userResponse.setMoney(user.getMoney() != null ? user.getMoney() : 0.0);


        UserCitizenResponse userCitizenResponse = new UserCitizenResponse();
        if(user.getCitizen() != null){
            userCitizenResponse.setId(user.getCitizen().getId());
            userCitizenResponse.setAddress(user.getCitizen().getAddress());
            userCitizenResponse.setCiCode(user.getCitizen().getCiCode());
            userCitizenResponse.setFullName(user.getCitizen().getFullName());
            userCitizenResponse.setBirthDate(user.getCitizen().getBirthDate());
            userCitizenResponse.setStartDate(user.getCitizen().getStartDate());

            userResponse.setCitizen(userCitizenResponse);
        }

        userResponse.setRoles(convertRoles(user.getRoles()) != null ? convertRoles(user.getRoles()) : new HashSet<>());
        return userResponse;
    }

    private static Set<RoleResponse> convertRoles(Set<Role> roles) {
        if (roles == null) {
            return Collections.emptySet(); // Trả về một Set rỗng thay vì `null`
        }
        return roles.stream()
                .map(role -> new RoleResponse(
                        role.getName(),
                        role.getDescription(),
                        role.getPermissions()
                                .stream()
                                .map(permission ->
                                        new PermissionResponse(
                                                permission.getName(),
                                                permission.getDescription()))
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toSet());
    }

}
