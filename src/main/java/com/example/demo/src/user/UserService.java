package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.simple.JSONObject;

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
    public PostLoginRes createKakaoSignIn(String accessToken/* String deviceToken*/) throws BaseException {
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

//            int check = userDao.checkToken(userIdx);
//            if(check == 0) {
//                int result = userDao.postDeviceToken(userIdx, deviceToken);
//                if(result == 0) throw new BaseException(SAVE_FAIL_DEVICE);
//            }

//            int result = userDao.updateDeviceToken(userIdx, deviceToken);
//            if(result == 0) throw new BaseException(SAVE_FAIL_DEVICE);

            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);

        } else {
            int userIdx = userDao.postEmail(email);
           // int result = userDao.postDeviceToken(userIdx, deviceToken);
           // if(result == 0) throw new BaseException(SAVE_FAIL_DEVICE);
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        }
    }

    /**
     * 구글 로그인 API
     *
     * @param accessToken
     * @return PostLogInRes
     * @throws BaseException
     */
    public PostLoginRes createGoogleSignIn(String accessToken /*String deviceToken*/) throws BaseException {
        final String RequestUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        JSONObject jsonObject;

        String header = "Bearer " + accessToken; // Bearer 다음에 공백 추가
        String apiURL = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

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

        String email;
        try {
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(body);
            email = jsonObject.get("email").toString();
        } catch (Exception e) {
            throw new BaseException(FAILED_TO_PARSE);
        }

        if (userDao.checkEmail(email) == 1) {
            GetSocial getSocial = userDao.getIdx(email);
            int userIdx = getSocial.getUserIdx();

//            int check = userDao.checkToken(userIdx);
//            if(check == 0) {
//                int result = userDao.postDeviceToken(userIdx, deviceToken);
//                if(result == 0) throw new BaseException(SAVE_FAIL_DEVICE);
//            }

//            int result = userDao.updateDeviceToken(userIdx, deviceToken);
//            if(result == 0) throw new BaseException(SAVE_FAIL_DEVICE);

            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);

        } else {
            int userIdx = userDao.postEmail(email);
           // int result = userDao.postDeviceToken(userIdx, deviceToken);
           // if(result == 0) throw new BaseException(SAVE_FAIL_DEVICE);
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        }
    }

    /**
     * 애플 로그인 API (첫 로그인시 디비에 저장)
     * @param email, deviceToken
     * @return PostLoginRes
     * @throws BaseException
     */
    public PostLoginRes createAppleSignUp(String email, String deviceToken) throws BaseException {
        try {
            int userIdx = userDao.postEmail(email);
            int result = userDao.postDeviceToken(userIdx, deviceToken);
            if(result == 0) throw new BaseException(SAVE_FAIL_DEVICE);
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_APPLE_SIGN_UP);
        }
    }


    /**
     * 애플 로그인 API
     * @param email, deviceToken
     * @return PostLoginRes
     * @throws BaseException
     */
    public PostLoginRes createAppleSignIn(String email, String deviceToken) throws BaseException {
        if (userDao.checkEmail(email) == 1) {
            GetSocial getSocial = userDao.getIdx(email);
            int userIdx = getSocial.getUserIdx();

            int check = userDao.checkToken(userIdx);
            if(check == 0) {
                int result = userDao.postDeviceToken(userIdx, deviceToken);
                if(result == 0) throw new BaseException(SAVE_FAIL_DEVICE);
            }

            int result = userDao.updateDeviceToken(userIdx, deviceToken);
            if(result == 0) throw new BaseException(SAVE_FAIL_DEVICE);

            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);

        } else {
            int userIdx = userDao.postEmail(email);
            int result = userDao.postDeviceToken(userIdx, deviceToken);
            if(result == 0) throw new BaseException(SAVE_FAIL_DEVICE);
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

    public void createBirth(PostBirth postBirth) throws BaseException {
        try {
            int result = userDao.createBirth(postBirth);
            if (result == 0) {
                throw new BaseException(POST_FAIL_BIRTH);
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

    public void deleteUser(int userIdx) throws BaseException {
        int check = userDao.checkUser(userIdx);
        if (check == 0) throw new BaseException(NOT_EXIST_USER);

        try {
            int result = userDao.deleteUser(userIdx);
            userDao.deleteDeviceToken(userIdx);
            if (result == 0) throw new BaseException(DELETE_FAIL_USER);
        } catch (Exception exception) {
            throw new BaseException(DELETE_FAIL_USER);
        }
    }

    public void deleteDeviceToken(int userIdx) throws BaseException {
        int check = userDao.checkUser(userIdx);
        if (check == 0) throw new BaseException(NOT_EXIST_USER);

        int checkToken = userDao.checkToken(userIdx);
        if(checkToken == 0) throw new BaseException(NOT_EXIST_TOKEN);

        try {
            int result = userDao.deleteDeviceToken(userIdx);
            if (result == 0) throw new BaseException(LOGOUT_FAIL_USER);
        } catch (Exception exception) {
            throw new BaseException(LOGOUT_FAIL_USER);
        }
    }
}



