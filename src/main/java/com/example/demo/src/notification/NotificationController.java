package com.example.demo.src.notification;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.notification.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;
import static com.example.demo.config.BaseResponseStatus.NOTHING_NOTIFICATION;

@RestController
@RequestMapping("/alerts")
public class NotificationController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final NotificationProvider notificationProvider;
    @Autowired
    private final NotificationService notificationService;
    @Autowired
    private final JwtService jwtService;

    public NotificationController(NotificationProvider notificationProvider, NotificationService notificationService, JwtService jwtService) {
        this.notificationProvider = notificationProvider;
        this.notificationService = notificationService;
        this.jwtService = jwtService;
    }

    /**
     * 홈 알림 조회 API
     * [GET] /notifications/:userIdx
     * @return BaseResponse<GetHomeNotification>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<List<GetHomeNotification>> getHomeNotification(@PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetHomeNotification> getHomeNotification = notificationProvider.getHomeNotification(userIdx);
            if(getHomeNotification.size() == 0) throw new BaseException(NOTHING_NOTIFICATION);
            return new BaseResponse<>(getHomeNotification);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     *  특정 홈 알림 삭제 API
     * [PATCH] /alerts/:idx/:userIdx/delete
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{idx}/{userIdx}/delete")
    public BaseResponse<String> deleteNotification(@PathVariable("idx") int alertIdx, @PathVariable("userIdx") int userIdx){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            notificationService.deleteNotification(alertIdx, userIdx);
            String result = "성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     *  홈 알림 전체 삭제 API
     * [PATCH] /alerts/:userIdx/delete-all
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/delete-all")
    public BaseResponse<String> deleteNotificationAll(@PathVariable("userIdx") int userIdx){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            notificationService.deleteNotificationAll(userIdx);
            String result = "성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 챌린지 내부 알림 조회 API
     * [GET] /alerts/:challengeIdx/:userIdx
     * @return BaseResponse<GetChallangeAlert>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{challengeIdx}/{userIdx}")
    public BaseResponse<List<GetChallengeAlert>> getChallengeAlert(@PathVariable("challengeIdx") int challengeIdx, @PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetChallengeAlert> getChallengeAlert = notificationProvider.getChallengeAlert(challengeIdx, userIdx);
            if(getChallengeAlert.size() == 0) throw new BaseException(NOTHING_NOTIFICATION);
            return new BaseResponse<>(getChallengeAlert);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     *  챌린지 내부 알림 삭제 API
     * [PATCH] /alerts/:idx/delete/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{idx}/delete/{userIdx}")
    public BaseResponse<String> deleteAlert(@PathVariable("idx") int alertIdx, @PathVariable("userIdx") int userIdx){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            notificationService.deleteAlert(alertIdx, userIdx);
            String result = "성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
