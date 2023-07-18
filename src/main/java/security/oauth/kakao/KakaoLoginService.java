package security.oauth.kakao;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import global.exception.BusinessLogicException;
import global.exception.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user.dto.UserDto;
import user.entity.User;
import user.repositoory.UserRepository;
import user.service.UserService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
@Slf4j
@Transactional
public class KakaoLoginService {

    private final UserService userService;

    public KakaoLoginService(UserService userService){
        this.userService = userService;
    }

    public KakaoOauth getKakaoAccessToken(String code){
        KakaoOauth.KakaoOauthBuilder kakaoOauth = new KakaoOauth.KakaoOauthBuilder();

        String accessToken = "";
        String refreshToken = "";
        Integer atkExpire = -1;
        Integer rtkExpire = -1;
        String requestURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // POST 요청에서 필요한 팔라미터를 OutputStream을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            String sb = "grant_type=authorization_code" +
                    "&client_id=0cde673f66f6e813ce1e69bfa9e6159b" + // REST_API_KEY
                    "&redirect_uri=http://localhost:8080/login/oauth2/code/kakao" + // REDIRECT_URI
                    "&code=" + code;

            bw.write(sb);
            bw.flush();

            // http response 메세지에서 status code를 받아온다.
            // 유효하지 않을 경우 -1 반환
            int responseCode = connection.getResponseCode();
            log.info("Response Code : {}", responseCode);

            if (responseCode == 400 || responseCode == 401){
                throw new BusinessLogicException(ExceptionCode.INVALID_KAKAO_CODE);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String s = "";
            StringBuilder builder = new StringBuilder();

            while ((s = br.readLine()) != null) builder.append(s);

            JsonElement element = JsonParser.parseString(builder.toString());

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
            atkExpire = element.getAsJsonObject().get("expires_in").getAsInt();
            rtkExpire = element.getAsJsonObject().get("refresh_token_expires_in").getAsInt();

            br.close();
            bw.close();

            kakaoOauth.accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .ATKExpiresIn(atkExpire)
                    .RTKExpiresIn(rtkExpire);
        } catch (IOException e){
            log.info("Throw Exception In getKakaoAccessToken : {}", e.getMessage());
        }

        return kakaoOauth.build();
    }

    public HashMap<String, String> getKakaoUserInfo(String accessToken){
        HashMap<String, String> kakaoUserInfo = new HashMap<>();
        String postURL = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(postURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = connection.getResponseCode();
            log.info("Response Code In getKakaoUserInfo : {}", responseCode);

            if (responseCode == 400 || responseCode == 401){
                throw new BusinessLogicException(ExceptionCode.INVALID_KAKAO_CODE);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String s = "";
            StringBuilder builder = new StringBuilder();

            while ((s = br.readLine()) != null) builder.append(s);

            JsonElement element = JsonParser.parseString(builder.toString());
            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String username = properties.getAsJsonObject().get("username").getAsString();
            String email = kakaoAccount.getAsJsonObject().get("email").getAsString();
            String image = kakaoAccount.getAsJsonObject().get("profile").getAsJsonObject().get("profile_image_url").getAsString();
            System.out.println("프로필 이미지" + image);

            kakaoUserInfo.put("username", username);
            kakaoUserInfo.put("email", email);
            kakaoUserInfo.put("image", image);
        } catch (IOException e){
            log.info("Throw Exception In getKakaoUserInfo : {}", e.getMessage());
        }

        return kakaoUserInfo;
    }

    public void kakaoLogoutOnly(String kakaoAccessToken){
        String requestUrl = "https://kapi.kakao.com/v1/user/logout";

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + kakaoAccessToken);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String s = "";
            while ((s = br.readLine()) != null) result += s;
            log.info("logout 메서드에서 나온 결과 값 : {}", result);

            br.close();
        } catch (IOException e){
            throw new RuntimeException();
        }
    }

    public void socialSignup(UserDto.OauthPost post){

        User user = User
                .builder()
                .email(post.getEmail())
                .username(post.getUsername())
                .provider("kakao")
                .image(post.getImage())
                .build();

        System.out.println("signup으로 넘겨줬을 때 뜨는 email 값 : " + user.getEmail());

        userService.createSocialUser(user);
    }

    public void kakaoUnLink(String accessToken){
        String postURL = "https://kapi.kakao.com/v1/user/unlink";

        try {
            URL url = new URL(postURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            int resCode = connection.getResponseCode();
            log.info("Response Code : {}", resCode);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
