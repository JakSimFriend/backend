package com.example.demo.src.status;

import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Service Create, Update, Delete 의 로직 처리
@Service
public class StatusService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StatusDao statusDao;
    private final StatusProvider statusProvider;
    private final JwtService jwtService;


    @Autowired
    public StatusService(StatusDao statusDao, StatusProvider statusProvider, JwtService jwtService) {
        this.statusDao = statusDao;
        this.statusProvider = statusProvider;
        this.jwtService = jwtService;
    }
}
