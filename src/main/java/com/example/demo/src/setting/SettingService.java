package com.example.demo.src.setting;

import com.example.demo.config.BaseException;
import com.example.demo.src.setting.SettingDao;
import com.example.demo.src.setting.SettingProvider;
import com.example.demo.src.setting.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class SettingService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SettingDao settingDao;
    private final SettingProvider settingProvider;
    private final JwtService jwtService;

    @Autowired
    public SettingService(SettingDao settingDao, SettingProvider settingProvider, JwtService jwtService) {
        this.settingDao = settingDao;
        this.settingProvider = settingProvider;
        this.jwtService = jwtService;
    }

    public void settingAlert(int userIdx) throws BaseException {
        int alert = settingDao.getAlert(userIdx);
        if(alert == 1) throw new BaseException(EXITS_SETTING);

        try {
            int result = settingDao.settingAlert(userIdx);
            if (result == 0) {throw new BaseException(SETTING_FAIL_ALERT);}
        } catch (Exception exception) {
            throw new BaseException(SETTING_FAIL_ALERT);
        }
    }

    public void cancelAlert(int userIdx) throws BaseException {
        int alert = settingDao.getAlert(userIdx);
        if(alert == 0) throw new BaseException(EXITS_CANCEL);

        try {
            int result = settingDao.cancelAlert(userIdx);
            if (result == 0) {throw new BaseException(SETTING_FAIL_ALERT_CANCEL);}
        } catch (Exception exception) {
            throw new BaseException(SETTING_FAIL_ALERT_CANCEL);
        }
    }

    public int postReport(PostReport postReport) throws BaseException {
        int challengeIdx = postReport.getChallengeIdx();
        int userIdx = postReport.getUserIdx();

        int challenge = settingDao.checkChallenge(challengeIdx);
        if(challenge == 0) throw new BaseException(NOT_EXIST_CHALLENGE);

        int member = settingDao.checkMember(challengeIdx, userIdx);
        if(member == 0) throw new BaseException(NOT_EXIST_MEMBER);

        int certification = settingDao.checkCertification(postReport.getCertificationIdx());
        if(certification == 0) throw new BaseException(NOT_EXIST_CERTIFICATION);

        int report = settingDao.checkReport(postReport.getCertificationIdx(), userIdx);
        if(report == 1) throw new BaseException(EXIST_REPORT);

        try {
            int result = settingDao.postReport(postReport);
            return result;
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_REPORT);
        }
    }

}
