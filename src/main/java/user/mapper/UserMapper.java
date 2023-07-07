package user.mapper;

import user.dto.UserDto;
import user.entity.User;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UserMapper {

    User userPostToMember(UserDto.Post post);

    User userPatchToMember(UserDto.Patch patch);

    UserDto.Response userToUserResponseDto(User user);

    List<UserDto.Response> userToUsersResponseDto(List<User> users);
}
