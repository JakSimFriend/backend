package com.example.demo.src.mychallenge;

import com.example.demo.config.BaseException;
import com.example.demo.src.challenge.model.PostChallenge;
import com.example.demo.src.fcm.FcmMessage;
import com.example.demo.src.fcm.RequestDTO;
import com.example.demo.src.mychallenge.model.GetToken;
import com.example.demo.src.mychallenge.model.PostCertificationRes;
import com.example.demo.src.mychallenge.model.PostReward;
import com.example.demo.src.s3.AwsS3Service;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class MyChallengeService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/jaksimfriend/messages:send";
    private final MyChallengeDao myChallengeDao;
    private final MyChallengeProvider myChallengeProvider;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final AwsS3Service awsS3Service;

    @Autowired
    public MyChallengeService(MyChallengeDao myChallengeDao, MyChallengeProvider myChallengeProvider, JwtService jwtService, AwsS3Service awsS3Service, ObjectMapper objectMapper) {
        this.myChallengeDao = myChallengeDao;
        this.myChallengeProvider = myChallengeProvider;
        this.jwtService = jwtService;
        this.awsS3Service = awsS3Service;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PostCertificationRes createCertification(int challengeIdx, int userIdx, MultipartFile multipartFile) throws BaseException {

        int checkChallenge = myChallengeDao.checkChallenge(challengeIdx);
        if(checkChallenge == 0) throw new BaseException(NOT_EXIST_CHALLENGE);

        int checkProceeding = myChallengeDao.checkProceeding(challengeIdx);
        if(checkProceeding == 0) throw new BaseException(NOT_PROCEEDING_CHALLENGE);

        int checkMember = myChallengeDao.checkMember(challengeIdx, userIdx);
        if(checkMember == 0) throw new BaseException(NOT_EXIST_MEMBER);

        int checkCertification = myChallengeDao.checkCertification(challengeIdx, userIdx);
        if(checkCertification == 1) throw new BaseException(EXIST_CERTIFICATION);

        int checkDeadline = myChallengeDao.checkDeadline(challengeIdx);
        if(checkDeadline == 1) throw new BaseException(DEADLINE_END);

        try {
            String nickName = myChallengeDao.getNickName(userIdx);
            List<GetToken> getToken = myChallengeDao.getMember(challengeIdx, userIdx);
            System.out.println(getToken.get(0));

            String title = "친구가 도전작심 인증을 했어요!";
            String image = "https://jaksim-bucket.s3.ap-northeast-2.amazonaws.com/e26e96d2-b73a-4dc8-9f62-07c7bc83c125.png";
            String body = nickName + "님이 도전작심을 인증하셨어요!";

            int exist = myChallengeDao.existCertification(challengeIdx, userIdx);
            if(exist == 0){
                String imageUrl = awsS3Service.uploadProfile(multipartFile);
                int certificationIdx = myChallengeDao.certification(challengeIdx, userIdx, imageUrl);
                int afterPercent = myChallengeDao.getPercent(challengeIdx, userIdx);

                for(int i = 0; i < getToken.size(); i++){
                    RequestDTO requestDTO = new RequestDTO(getToken.get(i).getToken(), title, body, image);
                    System.out.println(requestDTO + "객체");
                    System.out.println(getToken.get(i).getToken() + " " +title + " " + body + " " + image);
                    sendMessageTo(requestDTO.getTargetToken(), requestDTO.getTitle(), requestDTO.getBody(), requestDTO.getImage());
                    int alert = myChallengeDao.createAlert(body, image, certificationIdx, getToken.get(i).getUserIdx(), challengeIdx);
                }
                return new PostCertificationRes(certificationIdx, challengeIdx, userIdx, nickName, 0, afterPercent);
            } else{
                int beforePercent = myChallengeDao.getPercent(challengeIdx, userIdx);
                String imageUrl = awsS3Service.uploadProfile(multipartFile);
                int certificationIdx = myChallengeDao.certification(challengeIdx, userIdx, imageUrl);
                int afterPercent = myChallengeDao.getPercent(challengeIdx, userIdx);
                for(int i = 0; i < getToken.size(); i++){
                    RequestDTO requestDTO = new RequestDTO(getToken.get(i).getToken(), title, body, image);
                    System.out.println(requestDTO + "객체");
                    System.out.println(getToken.get(i).getToken() + " " +title + " " + body + " " + image);
                    sendMessageTo(requestDTO.getTargetToken(), requestDTO.getTitle(), requestDTO.getBody(), requestDTO.getImage());
                    int alert = myChallengeDao.createAlert(body, image, certificationIdx, getToken.get(i).getUserIdx(), challengeIdx);
                }
                return new PostCertificationRes(certificationIdx, challengeIdx, userIdx, nickName, beforePercent, afterPercent);
            }

        } catch (Exception exception) {
            throw new BaseException(CERTIFICATION_FAIL);
        }
    }

    public int postReward(PostReward postReward) throws BaseException {
        int challengeIdx = postReward.getChallengeIdx();
        int userIdx = postReward.getUserIdx();

        int challenge = myChallengeDao.checkChallenge(challengeIdx);
        if(challenge == 0) throw new BaseException(NOT_EXIST_CHALLENGE);

        int member = myChallengeDao.checkMember(challengeIdx, userIdx);
        if(member == 0) throw new BaseException(NOT_EXIST_MEMBER);

        int end = myChallengeDao.getEnd(challengeIdx);
        if(end == 0) throw new BaseException(NOT_END_CHALLENGE);

        int achievement = myChallengeDao.checkAchievement(challengeIdx, userIdx);
        if(achievement == 1) throw new BaseException(EXIST_REWARD);

        int experience = myChallengeDao.checkExperience(challengeIdx, userIdx);
        if(experience == 1) throw new BaseException(EXIST_REWARD);

        try {
            int result = myChallengeDao.postReward(postReward);
            return result;
        } catch (Exception exception) {
            throw new BaseException(POST_FAIL_REWARD);
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
