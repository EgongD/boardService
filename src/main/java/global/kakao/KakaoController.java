package global.kakao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;


@Controller
public class KakaoController {

    @Autowired
    private KakaoService kakaoService;

    @RequestMapping(value = "/")
    public String index(){

        return "index";
    }

    @RequestMapping(value = "/login")
    public String login(@RequestParam("code") String code, HttpSession session) {

        String access_token = kakaoService.getAccessToken(code);

        HashMap<String, Object> userInfo = kakaoService.getUserInfo(access_token);

        if (userInfo.get("email") != null){

            session.setAttribute("userId", userInfo.get("email"));
            session.setAttribute("access_token", access_token);
        }

        System.out.println("controller access_token : " + access_token);
        return "index";
    }
}
