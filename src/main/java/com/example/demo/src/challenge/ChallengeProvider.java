package com.example.demo.src.challenge;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.challenge.ChallengeDao;
import com.example.demo.src.challenge.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class ChallengeProvider {

    private final ChallengeDao challengeDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ChallengeProvider(ChallengeDao challengeDao, JwtService jwtService) {
        this.challengeDao = challengeDao;
        this.jwtService = jwtService;
    }

    public List<GetChallengeHome> getChallengeHome(int categoryIdx) throws BaseException{
        try{
            List<GetChallengeHome> getChallengeHome = challengeDao.getChallengeHome(categoryIdx);
            return getChallengeHome;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
