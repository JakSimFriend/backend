package com.example.demo.src.challenge;

import com.example.demo.config.BaseException;
import com.example.demo.src.challenge.ChallengeDao;
import com.example.demo.src.challenge.ChallengeProvider;
import com.example.demo.src.challenge.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Map;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class ChallengeService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ChallengeDao challengeDao;
    private final ChallengeProvider challengeProvider;
    private final JwtService jwtService;


    @Autowired
    public ChallengeService(ChallengeDao challengeDao, ChallengeProvider challengeProvider, JwtService jwtService) {
        this.challengeDao = challengeDao;
        this.challengeProvider = challengeProvider;
        this.jwtService = jwtService;
    }

    public int createChallenge(PostChallenge postChallenge) throws BaseException {
        try {
            int result = challengeDao.createChallenge(postChallenge);
            return result;
        } catch (Exception exception) {
            throw new BaseException(POST_FAIL_CHALLENGE);
        }
    }

    public void deleteChallenge(int challengeIdx, int userIdx) throws BaseException {
        try{
            int check = challengeDao.checkChallenge(challengeIdx);
            if(check == 0) throw new BaseException(NOT_EXIST_CHALLENGE);
        } catch(Exception exception){
            throw new BaseException(NOT_EXIST_CHALLENGE);
        }
        try{
            int result = challengeDao.deleteChallenge(challengeIdx, userIdx);
            if(result == 0) throw new BaseException(DELETE_FAIL_CHALLENGE);
        } catch(Exception exception){
            throw new BaseException(DELETE_FAIL_CHALLENGE);
        }
    }
}
