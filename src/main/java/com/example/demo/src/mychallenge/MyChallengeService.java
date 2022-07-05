package com.example.demo.src.mychallenge;

import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Service Create, Update, Delete 의 로직 처리
@Service
public class MyChallengeService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MyChallengeDao myChallengeDao;
    private final MyChallengeProvider myChallengeProvider;
    private final JwtService jwtService;

    @Autowired
    public MyChallengeService(MyChallengeDao myChallengeDao, MyChallengeProvider myChallengeProvider, JwtService jwtService) {
        this.myChallengeDao = myChallengeDao;
        this.myChallengeProvider = myChallengeProvider;
        this.jwtService = jwtService;
    }
}
