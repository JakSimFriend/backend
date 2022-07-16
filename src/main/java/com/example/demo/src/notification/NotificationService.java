package com.example.demo.src.notification;

import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
