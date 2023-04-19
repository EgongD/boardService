package global.kakao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/member")
public class KakaoController {

    @Autowired
    KakaoService kakaoService;

    @GetMapping("/do")
    public String loginPage(){

        return "kakaoCI/login";
    }

    @GetMapping("/kakao")
    public String getCI(@RequestParam String code, Model model) throws IOException{

        System.out.println("code = " + code);

        String access_token = kakaoService.getToken(code);
        Map<String, Object> userInfo = kakaoService.getUserInfo(access_token);
        model.addAttribute("code", code);
        model.addAttribute("access_token", access_token);
        model.addAttribute("userInfo", userInfo);

        return "index";
    }
}
