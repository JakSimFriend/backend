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

@RestController
@RequestMapping("/notifications")
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
            return new BaseResponse<>(getHomeNotification);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
