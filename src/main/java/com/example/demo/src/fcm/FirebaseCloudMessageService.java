package com.example.demo.src.fcm;


import com.example.demo.config.BaseException;
import com.example.demo.src.challenge.ChallengeDao;
import com.example.demo.src.challenge.ChallengeProvider;
import com.example.demo.src.fcm.model.PostReaction;
import com.example.demo.src.mychallenge.model.PostReward;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.google.gson.JsonParseException;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/jaksimfriend/messages:send";
    private final ObjectMapper objectMapper;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FirebaseCloudMessageDao firebaseCloudMessageDao;
    private final JwtService jwtService;

    @Autowired
    public FirebaseCloudMessageService(FirebaseCloudMessageDao firebaseCloudMessageDao, JwtService jwtService, ObjectMapper objectMapper) {
        this.firebaseCloudMessageDao = firebaseCloudMessageDao;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
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


    public int createReaction(PostReaction postReaction) throws BaseException {
        try {
            String sender = firebaseCloudMessageDao.getNickName(postReaction.getSenderIdx());
            String receiver = firebaseCloudMessageDao.getNickName(postReaction.getReceiverIdx());
            String reaction = firebaseCloudMessageDao.getReaction(postReaction.getReactionIdx());
            String token = firebaseCloudMessageDao.getToken(postReaction.getReceiverIdx());
            System.out.println(token);

            String title = "리액션이 도착했어요!";
            String image = "https://jaksim-bucket.s3.ap-northeast-2.amazonaws.com/6b792eec-f500-4eb7-90b2-90d132c8b8a2.png";
            String body = sender + "님이 " + receiver + "님께 " + reaction + " 리액션을 남기셨어요";

            RequestDTO requestDTO = new RequestDTO(token, title, body, image);
            System.out.println(requestDTO + "객체");
            System.out.println(token + " "
                    +title + " " + body + " " + image);
            sendMessageTo(requestDTO.getTargetToken(), requestDTO.getTitle(), requestDTO.getBody(), requestDTO.getImage());

            int alert = firebaseCloudMessageDao.createAlert(body, postReaction.getReceiverIdx(), postReaction.getChallengeIdx());
            int result = firebaseCloudMessageDao.createReaction(postReaction);
            return result;
        } catch (Exception exception) {
            throw new BaseException(POST_FAIL_REWARD);
        }
    }

}









