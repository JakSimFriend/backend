package com.example.demo.src.profile;

import com.example.demo.config.BaseException;
import com.example.demo.src.profile.model.*;
import com.example.demo.src.s3.AwsS3Service;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class ProfileService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProfileDao profileDao;
    private final ProfileProvider profileProvider;
    private final JwtService jwtService;
    private final AwsS3Service awsS3Service;


    @Autowired
    public ProfileService(ProfileDao profileDao, ProfileProvider profileProvider, JwtService jwtService, AwsS3Service awsS3Service) {
        this.profileDao = profileDao;
        this.profileProvider = profileProvider;
        this.jwtService = jwtService;
        this.awsS3Service = awsS3Service;
    }

    public void modifyPromise(PostPromise postPromise) throws BaseException {
        try {
            int result = profileDao.modifyPromise(postPromise);
            if (result == 0) {
                throw new BaseException(POST_FAIL_PROMISE);
            }
        } catch (Exception exception) {
            throw new BaseException(POST_FAIL_PROMISE);
        }
    }

    public void modifyProfileImage(int userIdx, MultipartFile multipartFile) throws BaseException {
        try {
            String imageUrl = awsS3Service.uploadProfile(multipartFile);
            int result = profileDao.modifyProfileImage(userIdx, imageUrl);
            if (result == 0) {
                throw new BaseException(POST_FAIL_PROFILE);
            }
        } catch (Exception exception) {
            throw new BaseException(POST_FAIL_PROFILE);
        }
    }

    public void getReward(int userIdx) throws BaseException {
        int check = profileDao.checkUser(userIdx);
        if(check == 0) throw new BaseException(NOT_EXIST_USER);

        try {
            int result = profileDao.getReward(userIdx);
            if (result == 0) {throw new BaseException(GET_FAIL_REWARD);}
        } catch (Exception exception) {
            throw new BaseException(GET_FAIL_REWARD);
        }
    }
}
