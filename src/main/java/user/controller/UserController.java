package user.controller;

import user.dto.UserDto;
import user.entity.User;
import user.mapper.UserMapper;
import user.repositoory.UserRepository;
import user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    public UserController(UserService userService,
                          UserMapper userMapper,
                          UserRepository userRepository){

        this.userService = userService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody UserDto.Post post){

        User user = userService.createMember(userMapper.userPostToMember(post));

        UserDto.Response memberPostResponse = userMapper.userToUserResponseDto(user);

        return new ResponseEntity<>(memberPostResponse, HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity patchMember(@Valid @RequestBody UserDto.Patch patch){

        User user = userService.updateMember(userMapper.userPatchToMember(patch));

        UserDto.Response memberPatchResponse = userMapper.userToUserResponseDto(user);

        return new ResponseEntity<>(memberPatchResponse, HttpStatus.OK);
    }

    @GetMapping("/{user-id}")
    public ResponseEntity getMember(@Positive @PathVariable("user-id") Long userId){

        User user = userService.findUser(userId);

        UserDto.Response memberGetResponse= userMapper.userToUserResponseDto(user);

        return new ResponseEntity<>(memberGetResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity deleteMember(@PathVariable("user-id") @Positive Long userId){

        userService.deleteMember(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
