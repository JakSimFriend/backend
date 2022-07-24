package com.example.demo.src.notification;

import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class NotificationService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NotificationDao notificationDao;
    private final NotificationProvider notificationProvider;
    private final JwtService jwtService;


    @Autowired
    public NotificationService(NotificationDao notificationDao, NotificationProvider notificationProvider, JwtService jwtService) {
        this.notificationDao = notificationDao;
        this.notificationProvider = notificationProvider;
        this.jwtService = jwtService;
    }

    public void deleteNotification(int alertIdx, int userIdx) throws BaseException {
        int check = notificationDao.checkNotification(alertIdx);
        if(check == 0) throw new BaseException(NOT_EXIST_NOTIFICATION);

        int user = notificationDao.checkUser(alertIdx, userIdx);
        if(user == 0) throw new BaseException(NO_ACCESS_USER);

        try{
            int result = notificationDao.deleteNotification(alertIdx, userIdx);
            if(result == 0) throw new BaseException(DELETE_FAIL_NOTIFICATION);
        } catch(Exception exception){
            throw new BaseException(DELETE_FAIL_NOTIFICATION);
        }
    }

    public void deleteNotificationAll(int userIdx) throws BaseException {
        try{
            int result = notificationDao.deleteNotificationAll(userIdx);
            if(result == 0) throw new BaseException(DELETE_FAIL_ALL);
        } catch(Exception exception){
            throw new BaseException(DELETE_FAIL_ALL);
        }
    }

    public void deleteAlert(int alertIdx, int userIdx) throws BaseException {
        int check = notificationDao.checkAlert(alertIdx);
        if(check == 0) throw new BaseException(NOT_EXIST_NOTIFICATION);

        try{
            int result = notificationDao.deleteAlert(alertIdx, userIdx);
            if(result == 0) throw new BaseException(DELETE_FAIL_NOTIFICATION);
        } catch(Exception exception){
            throw new BaseException(DELETE_FAIL_NOTIFICATION);
        }
    }

}
