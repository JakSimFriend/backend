package com.example.demo.src.notification;

import com.example.demo.config.BaseException;
import com.example.demo.src.notification.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

//Provider : Read의 비즈니스 로직 처리
@Service
public class NotificationProvider {

    private final NotificationDao notificationDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public NotificationProvider(NotificationDao notificationDao, JwtService jwtService) {
        this.notificationDao = notificationDao;
        this.jwtService = jwtService;
    }

    public List<GetHomeNotification> getHomeNotification(int userIdx) throws BaseException {
        try {
            List<GetHomeNotification> getHomeNotification = notificationDao.getHomeNotification(userIdx);
            return getHomeNotification;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
