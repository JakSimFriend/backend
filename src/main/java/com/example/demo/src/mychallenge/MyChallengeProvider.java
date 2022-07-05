package com.example.demo.src.mychallenge;

import com.example.demo.config.BaseException;
import com.example.demo.src.mychallenge.model.GetMyChallengeProgress;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

//Provider : Read의 비즈니스 로직 처리
@Service
public class MyChallengeProvider {
    private final MyChallengeDao myChallengeDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public MyChallengeProvider(MyChallengeDao myChallengeDao, JwtService jwtService) {
        this.myChallengeDao = myChallengeDao;
        this.jwtService = jwtService;
    }

    public List<GetMyChallengeProgress> getMyChallengeProgress(int userIdx) throws BaseException {
        try{
            List<GetMyChallengeProgress> getMyChallengeProgress = myChallengeDao.getMyChallengeProgress(userIdx);
            return getMyChallengeProgress;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
