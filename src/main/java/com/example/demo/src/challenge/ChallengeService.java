package com.example.demo.src.challenge;

import com.example.demo.config.BaseException;
import com.example.demo.src.challenge.ChallengeDao;
import com.example.demo.src.challenge.ChallengeProvider;
import com.example.demo.src.challenge.model.*;
import com.example.demo.src.fcm.FcmMessage;
import com.example.demo.src.fcm.RequestDTO;
import com.example.demo.src.fcm.model.PostReaction;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.google.gson.JsonParseException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import java.util.List;
import java.util.Map;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class ChallengeService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ChallengeDao challengeDao;
    private final ChallengeProvider challengeProvider;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/jaksimfriend/messages:send";

    @Autowired
    public ChallengeService(ChallengeDao challengeDao, ChallengeProvider challengeProvider, JwtService jwtService, ObjectMapper objectMapper) {
        this.challengeDao = challengeDao;
        this.challengeProvider = challengeProvider;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    public int createChallenge(PostChallenge postChallenge) throws BaseException {
        int point = challengeDao.getPoint(postChallenge.getUserIdx());
        if(point < 1000) throw new BaseException(NOT_EXIST_POINT);

        try {
            int result = challengeDao.createChallenge(postChallenge);
            return result;
        } catch (Exception exception) {
            throw new BaseException(POST_FAIL_CHALLENGE);
        }
    }

    public void deleteChallenge(int challengeIdx, int userIdx) throws BaseException {
        int check = challengeDao.checkChallenge(challengeIdx);
        if(check == 0) throw new BaseException(NOT_EXIST_CHALLENGE);

        int proceeding = challengeDao.checkProceeding(challengeIdx);
        if(proceeding == 1) throw new BaseException(PROCEEDING_CHALLENGE);

        try{
            int result = challengeDao.deleteChallenge(challengeIdx, userIdx);
            if(result == 0) throw new BaseException(DELETE_FAIL_CHALLENGE);
        } catch(Exception exception){
            throw new BaseException(DELETE_FAIL_CHALLENGE);
        }
    }

    public int joinChallenge(PostChallengeJoin postChallengeJoin) throws BaseException {
        int check = challengeDao.checkChallenge(postChallengeJoin.getChallengeIdx());
        if(check == 0) throw new BaseException(NOT_EXIST_CHALLENGE);

        int join = challengeDao.checkJoin(postChallengeJoin.getChallengeIdx(), postChallengeJoin.getUserIdx());
        if(join == 1) throw new BaseException(EXIST_JOIN);

        int proceeding = challengeDao.checkProceeding(postChallengeJoin.getChallengeIdx());
        if(proceeding == 1) throw new BaseException(PROCEEDING_CHALLENGE_JOIN);

        ClosingCondition closingCondition = challengeDao.closingCondition(postChallengeJoin.getChallengeIdx());

        if(closingCondition.getStartDate() < 1 || closingCondition.getPeople() > 5)
            throw new BaseException(CLOSED_CHALLENGE);

        int point = challengeDao.getPoint(postChallengeJoin.getUserIdx());
        if(point < 1000) throw new BaseException(NOT_EXIST_POINT);

        try{
            String sender = challengeDao.getNickName(postChallengeJoin.getUserIdx());
            int receiverIdx = challengeDao.getFounder(postChallengeJoin.getChallengeIdx());
            String receiver = challengeDao.getNickName(receiverIdx);
            String token = challengeDao.getToken(16);
            String challengeTitle = challengeDao.getTitle(postChallengeJoin.getChallengeIdx());
            System.out.println(token);

            String title = "참여 신청이 왔어요!";
            String image = "https://jaksim-bucket.s3.ap-northeast-2.amazonaws.com/c6238aa8-6855-413d-b1ee-f3b0b8d8e50a.png";
            String body = challengeTitle + " 도전작심에 참여 신청이 들어왔어요!";

            RequestDTO requestDTO = new RequestDTO(token, title, body, image);
            System.out.println(requestDTO + "객체");
            System.out.println(token + " " + title + " " + body + " " + image);
            sendMessageTo(requestDTO.getTargetToken(), requestDTO.getTitle(), requestDTO.getBody(), requestDTO.getImage());

            int alert = challengeDao.createAlert(body, receiverIdx, image);
            int result = challengeDao.joinChallenge(postChallengeJoin);
            if(result == 0) throw new BaseException(POST_FAIL_JOIN);
            return result;
        } catch(Exception exception){
            throw new BaseException(POST_FAIL_JOIN);
        }
    }

    public void deleteWaiting(int waitingIdx, int userIdx) throws BaseException {
        try{
            int check = challengeDao.checkWaiting(waitingIdx);
            if(check == 0) throw new BaseException(NOT_EXIST_JOIN);
        } catch(Exception exception){
            throw new BaseException(NOT_EXIST_JOIN);
        }
        try{
            int result = challengeDao.deleteWaiting(waitingIdx, userIdx);
            if(result == 0) throw new BaseException(DELETE_FAIL_JOIN);
        } catch(Exception exception){
            throw new BaseException(DELETE_FAIL_JOIN);
        }
    }

    public void refuseWaiting(int waitingIdx, int founderIdx) throws BaseException {

        int check = challengeDao.checkWaiting(waitingIdx);
        if(check == 0) throw new BaseException(NOT_EXIST_JOIN);

        int refuse = challengeDao.checkRefuse(waitingIdx);
        if(refuse == 1) throw new BaseException(EXIST_REFUSE);

        try{
            int userIdx = challengeDao.getUser(waitingIdx);
            int challengeIdx = challengeDao.getChallenge(waitingIdx);
            String token = challengeDao.getToken(16);
            String challengeTitle = challengeDao.getTitle(challengeIdx);
            System.out.println(token);

            String title = "승인 여부 알림이 도착했어요!";
            String image = "https://jaksim-bucket.s3.ap-northeast-2.amazonaws.com/c6238aa8-6855-413d-b1ee-f3b0b8d8e50a.png";
            String body = challengeTitle + " 도전작심 참여 신청이 거절되었어요";

            RequestDTO requestDTO = new RequestDTO(token, title, body, image);
            System.out.println(requestDTO + "객체");
            System.out.println(token + " " + title + " " + body + " " + image);
            sendMessageTo(requestDTO.getTargetToken(), requestDTO.getTitle(), requestDTO.getBody(), requestDTO.getImage());

            int alert = challengeDao.createAlert(body, userIdx, image);
            int result = challengeDao.refuseWaiting(waitingIdx, founderIdx);
            if(result == 0) throw new BaseException(REFUSE_FAIL_JOIN);
        } catch(Exception exception){
            throw new BaseException(REFUSE_FAIL_JOIN);
        }
    }

    public void acceptWaiting(int waitingIdx, int founderIdx) throws BaseException {
            int check = challengeDao.checkWaiting(waitingIdx);
            if(check == 0) throw new BaseException(NOT_EXIST_JOIN);

            int founder = challengeDao.checkFounder(waitingIdx, founderIdx);
            if(founder == 0) throw new BaseException(NOT_EXIST_FOUNDER);

            int accept = challengeDao.checkAccept(waitingIdx);
            if(accept == 1) throw new BaseException(EXIST_ACCEPT);

            int refuse = challengeDao.checkRefuse(waitingIdx);
            if(refuse == 1) throw new BaseException(EXIST_REFUSE);

        try{
            int userIdx = challengeDao.getUser(waitingIdx);
            int challengeIdx = challengeDao.getChallenge(waitingIdx);
            String token = challengeDao.getToken(16);
            String challengeTitle = challengeDao.getTitle(challengeIdx);
            System.out.println(token);

            String title = "승인 여부 알림이 도착했어요!";
            String image = "https://jaksim-bucket.s3.ap-northeast-2.amazonaws.com/c6238aa8-6855-413d-b1ee-f3b0b8d8e50a.png";
            String body = challengeTitle + " 도전작심 참여 신청이 승인되었어요!";

            RequestDTO requestDTO = new RequestDTO(token, title, body, image);
            System.out.println(requestDTO + "객체");
            System.out.println(token + " " + title + " " + body + " " + image);
            sendMessageTo(requestDTO.getTargetToken(), requestDTO.getTitle(), requestDTO.getBody(), requestDTO.getImage());

            int alert = challengeDao.createAlert(body, userIdx, image);

            int result = challengeDao.acceptWaiting(waitingIdx, founderIdx);
            if(result == 0) throw new BaseException(ACCEPT_FAIL_jOIN);
        } catch(Exception exception){
            throw new BaseException(ACCEPT_FAIL_jOIN);
        }
    }

    public void sendMessageTo(String targetToken, String title, String body, String image) throws IOException {
        String message = makeMessage(targetToken, title, body, image);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }

    private String makeMessage(String targetToken, String title, String body, String image) throws JsonParseException, JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(image)
                                .build()
                        ).build()).validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "jaksimfriend-firebase-adminsdk-pbdqf-076653fb04.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }


}
