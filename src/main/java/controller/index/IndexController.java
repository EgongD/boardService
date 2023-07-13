package controller.index;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import security.auth.PrincipalDetails;
import user.entity.User;
import user.repositoory.UserRepository;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
