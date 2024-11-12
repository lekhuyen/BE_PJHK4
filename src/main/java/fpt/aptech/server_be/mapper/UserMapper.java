package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.UserCreationRequest;
import fpt.aptech.server_be.dto.request.UserUpdateRequest;
import fpt.aptech.server_be.dto.response.UserResponse;
import fpt.aptech.server_be.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public class UserMapper {
    public static User toUser(UserCreationRequest request){
        User user = new User();

        user.setName(request.getName());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());
        user.setEmail(request.getEmail());

        return user;
    }
    public static UserResponse toUserResponse(User user){

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setDob(user.getDob());
        userResponse.setEmail(user.getEmail());
        userResponse.setRoles(user.getRoles());
        return userResponse;
    }

}
