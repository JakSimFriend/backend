package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    /**
     * 카카오 로그인 API
     *
     * @param accessToken
     * @return PostLogInRes
     * @throws BaseException
     */
    public PostLoginRes createKakaoSignIn(String accessToken) throws BaseException {
        JSONObject jsonObject;

        String header = "Bearer " + accessToken; // Bearer 다음에 공백 추가
        String apiURL = "https://kapi.kakao.com/v2/user/me";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", header);

        HttpURLConnection con;
        try {
            URL url = new URL(apiURL);
            con = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new BaseException(WRONG_URL);
        } catch (IOException e) {
            throw new BaseException(FAILED_TO_CONNECT);
        }

        String body;
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> rqheader : requestHeaders.entrySet()) {
                con.setRequestProperty(rqheader.getKey(), rqheader.getValue());
            }

            int responseCode = con.getResponseCode();
            InputStreamReader streamReader;
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                streamReader = new InputStreamReader(con.getInputStream());
            } else { // 에러 발생
                streamReader = new InputStreamReader(con.getErrorStream());
            }

            BufferedReader lineReader = new BufferedReader(streamReader);
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            body = responseBody.toString();
        } catch (IOException e) {
            throw new BaseException(FAILED_TO_READ_RESPONSE);
        } finally {
            con.disconnect();
        }

        if (body.length() == 0) {
            throw new BaseException(FAILED_TO_READ_RESPONSE);
        }
        System.out.println(body);

        String socialId;
        String response;
        try {
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(body);
            socialId = "kakao_" + jsonObject.get("id").toString();
            response = jsonObject.get("kakao_account").toString();
        } catch (Exception e) {
            throw new BaseException(FAILED_TO_PARSE);
        }

        String email = null;
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject responObj = (JSONObject) jsonParser.parse(response);
            if (responObj.get("email") != null) {
                email = responObj.get("email").toString();
                System.out.println(email);
            }
        } catch (Exception e) {
            throw new BaseException(FAILED_TO_PARSE);
        }

        if (userDao.checkEmail(email) == 1) {
            GetSocial getSocial = userDao.getIdx(email);
            int userIdx = getSocial.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        } else {
            int userIdx = userDao.postEmail(email);
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        }
    }

    public void checkNickName(PostUserNickName postUserNickName) throws BaseException {
        String nickName = postUserNickName.getNickName();
        if (userDao.checkNickName(nickName) == 1) {
            throw new BaseException(DUPLICATED_NICKNAME);
        }
    }

    public void userCheck(PostUserNickName postUserNickName) throws BaseException {
        try {
            String nickName = postUserNickName.getNickName();
            if (userDao.getUserIdx(nickName) == 0) {
                throw new BaseException(NOT_EXIST_USER);
            }

        } catch (Exception exception) {
            throw new BaseException(NOT_EXIST_USER);
        }
    }

    public void createNickName(PostUserInfo postUserInfo) throws BaseException {
        try {
            int result = userDao.createNickName(postUserInfo);
            if (result == 0) {
                throw new BaseException(POST_FAIL_NICKNAME);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void createRecommender(PostUserInfo postUserInfo) throws BaseException {
        try {
            int result = userDao.createRecommender(postUserInfo);
            if (result == 0) {
                throw new BaseException(POST_FAIL_RECOMMENDER);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}



