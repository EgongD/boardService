package global.kakao;

import com.google.gson.JsonElement;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoService {

    public String getAccessToken(String code) {

        String host = "https://kauth.kakao.com/oauth/token";
        String access_token = "";
        String refresh_token = "";


        try {

            URL url = new URL(host);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //  POST 요청을 위해 기본값이 false인 setDoOutput을 ture로 설정
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            //  POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=be898f7a7177c25770b5213027ca4088");
            sb.append("&redirect_url=http://localhost:8080/login/oauth2/kakao");
            sb.append("&code=" + code);

            bw.write(sb.toString());
            bw.flush();

            //  결과 코드가 200이면 성공
            int responseCode = connection.getResponseCode();
            System.out.println("responseCode = " + responseCode);

            //  요청을 통해 얻은 JSON 타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null){

                result += line;
            }

            System.out.println("result = " + result);

            //  GSON 라이브러리에 포함된 클래스로 JSON 파싱 객체 생성
            JSONParser parser = new JSONParser();
            JsonElement elem = (JsonElement) parser.parse(result);

            access_token = elem.getAsJsonObject().get("access_token").toString();
            refresh_token = elem.getAsJsonObject().get("refresh_token").toString();
            System.out.println("refresh_token = " + refresh_token);
            System.out.println("access_token = " + access_token);

            br.close();
            bw.close();
        } catch (IOException e){

            e.printStackTrace();
        } catch (ParseException e){

            e.printStackTrace();
        }

        return access_token;
    }

    public HashMap<String, Object> getUserInfo(String access_token) {

        //  요청하는 클라이언트마다 가진 정보가 다를 수 있으므로 HashMap 타입으로 선언
        String host = "https://kapi.kakao.com/v2/user/me";
        HashMap<String, Object> result = new HashMap<>();

        try {

            URL url = new URL(host);

            //  요청에 필요한 Header에 포함될 내용
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Barer " + access_token);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("responseCode = " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String res = "";

            while ((line = br.readLine()) != null){

                res += line;
            }

            System.out.println("res = " + res);

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(res);
            JSONObject kakao_account = (JSONObject) obj.get("kakao_account");
            JSONObject properties = (JSONObject) obj.get("properties");

            String id = obj.get("id").toString();
            String nickName = properties.get("nickname").toString();
            String age_range = kakao_account.get("age_range").toString();

            result.put("id", id);
            result.put("nickname", nickName);
            result.put("age_range", age_range);

            br.close();
        } catch (IOException | ParseException e){

            e.printStackTrace();
        }

        return result;
    }

ㅌ
}
