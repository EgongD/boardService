package user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import security.auth.PrincipalDetails;
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

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserService userService,
                          UserMapper userMapper,
                          UserRepository userRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder){

        this.userService = userService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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

    @GetMapping({"", "/"})
    public String index(){

        return "controller";
    }

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication,
                                          @AuthenticationPrincipal UserDetails userDetails){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        return "세션 정보 확인";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOauthLogin(Authentication authentication,
                                               @AuthenticationPrincipal OAuth2User oauth){
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        return "OAuth 세션 정보 확인";
    }

    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){

        return "user";
    }

    @GetMapping("/loginForm")
    public String loginForm(){

        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){

        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user){
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);

        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info(){

        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data(){

        return "데이터 정보";
    }
}
