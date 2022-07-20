package com.example.demo.src.mychallenge;

import com.example.demo.config.BaseException;
import com.example.demo.src.challenge.model.PostChallenge;
import com.example.demo.src.mychallenge.model.PostCertificationRes;
import com.example.demo.src.mychallenge.model.PostReward;
import com.example.demo.src.s3.AwsS3Service;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class MyChallengeService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MyChallengeDao myChallengeDao;
    private final MyChallengeProvider myChallengeProvider;
    private final JwtService jwtService;
    private final AwsS3Service awsS3Service;

    @Autowired
    public MyChallengeService(MyChallengeDao myChallengeDao, MyChallengeProvider myChallengeProvider, JwtService jwtService, AwsS3Service awsS3Service) {
        this.myChallengeDao = myChallengeDao;
        this.myChallengeProvider = myChallengeProvider;
        this.jwtService = jwtService;
        this.awsS3Service = awsS3Service;
    }

    @Transactional
    public PostCertificationRes createCertification(int challengeIdx, int userIdx, MultipartFile multipartFile) throws BaseException {

        int checkChallenge = myChallengeDao.checkChallenge(challengeIdx);
        if(checkChallenge == 0) throw new BaseException(NOT_EXIST_CHALLENGE);

        int checkProceeding = myChallengeDao.checkProceeding(challengeIdx);
        if(checkProceeding == 0) throw new BaseException(NOT_PROCEEDING_CHALLENGE);

        int checkMember = myChallengeDao.checkMember(challengeIdx, userIdx);
        if(checkMember == 0) throw new BaseException(NOT_EXIST_MEMBER);

        int checkCertification = myChallengeDao.checkCertification(challengeIdx, userIdx);
        if(checkCertification == 1) throw new BaseException(EXIST_CERTIFICATION);

        int checkDeadline = myChallengeDao.checkDeadline(challengeIdx);
        if(checkDeadline == 1) throw new BaseException(DEADLINE_END);

        try {
            String nickName = myChallengeDao.getNickName(userIdx);

            int exist = myChallengeDao.existCertification(challengeIdx, userIdx);
            if(exist == 0){
                String imageUrl = awsS3Service.uploadProfile(multipartFile);
                int certificationIdx = myChallengeDao.certification(challengeIdx, userIdx, imageUrl);
                int afterPercent = myChallengeDao.getPercent(challengeIdx, userIdx);
                return new PostCertificationRes(certificationIdx, challengeIdx, userIdx, nickName, 0, afterPercent);
            } else{
                int beforePercent = myChallengeDao.getPercent(challengeIdx, userIdx);
                String imageUrl = awsS3Service.uploadProfile(multipartFile);
                int certificationIdx = myChallengeDao.certification(challengeIdx, userIdx, imageUrl);
                int afterPercent = myChallengeDao.getPercent(challengeIdx, userIdx);
                return new PostCertificationRes(certificationIdx, challengeIdx, userIdx, nickName, beforePercent, afterPercent);
            }

        } catch (Exception exception) {
            throw new BaseException(CERTIFICATION_FAIL);
        }
    }

    public int postReward(PostReward postReward) throws BaseException {
        int challengeIdx = postReward.getChallengeIdx();
        int userIdx = postReward.getUserIdx();

        int challenge = myChallengeDao.checkChallenge(challengeIdx);
        if(challenge == 0) throw new BaseException(NOT_EXIST_CHALLENGE);

        int member = myChallengeDao.checkMember(challengeIdx, userIdx);
        if(member == 0) throw new BaseException(NOT_EXIST_MEMBER);

        int end = myChallengeDao.getEnd(challengeIdx);
        if(end == 0) throw new BaseException(NOT_END_CHALLENGE);

        int achievement = myChallengeDao.checkAchievement(challengeIdx, userIdx);
        if(achievement == 1) throw new BaseException(EXIST_REWARD);

        int experience = myChallengeDao.checkExperience(challengeIdx, userIdx);
        if(experience == 1) throw new BaseException(EXIST_REWARD);

        try {
            int result = myChallengeDao.postReward(postReward);
            return result;
        } catch (Exception exception) {
            throw new BaseException(POST_FAIL_REWARD);
        }
    }

}
