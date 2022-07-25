package com.example.demo.src.setting;

import com.example.demo.src.setting.SettingProvider;
import com.example.demo.src.setting.SettingService;
import com.example.demo.src.setting.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/settings")
public class SettingController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final SettingProvider settingProvider;
    @Autowired
    private final SettingService settingService;
    @Autowired
    private final JwtService jwtService;

    public SettingController(SettingProvider settingProvider, SettingService settingService, JwtService jwtService) {
        this.settingProvider = settingProvider;
        this.settingService = settingService;
        this.jwtService = jwtService;
    }

    /**
     *  미달성 챌린지 알림 설정 API
     * [PATCH] /settings/:userIdx/alert
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/alert")
    public BaseResponse<String> settingAlert(@PathVariable("userIdx") int userIdx){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            settingService.settingAlert(userIdx);
            String result = "성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     *  미달성 챌린지 알림 설정 API
     * [PATCH] /settings/:userIdx/alert-cancel
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/alert-cancel")
    public BaseResponse<String> cancelAlert(@PathVariable("userIdx") int userIdx){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            settingService.cancelAlert(userIdx);
            String result = "성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 신고하기 API
     * [POST] /settings/report
     * @return BaseResponse<Integer>
     */
    @ResponseBody
    @PostMapping("/report")
    public BaseResponse<Integer> postReport(@RequestBody PostReport postReport) {
        try {
            if(postReport.getUserIdx() == 0) return new BaseResponse<>(POST_REPORT_EMPTY_USER);
            if(postReport.getChallengeIdx() == 0) return new BaseResponse<>(POST_REPORT_EMPTY_CHALLENGE);
            if(postReport.getCertificationIdx() == 0) return new BaseResponse<>(POST_REPORT_EMPTY_CERTIFICATION);

            int userIdx = postReport.getUserIdx();
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            int result = settingService.postReport(postReport);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 문의하기 API
     * [POST] /settings/Inquire
     * @return BaseResponse<Integer>
     */
    @ResponseBody
    @PostMapping("/Inquire")
    public BaseResponse<Integer> postInquire(@RequestBody PostInquire postInquire) {
        try {
            if(postInquire.getUserIdx() == 0) return new BaseResponse<>(USERS_EMPTY_USER_ID);
            if(postInquire.getTitle() == null) return new BaseResponse<>(POST_INQUIRE_EMPTY_TITLE);
            if(postInquire.getContent() == null) return new BaseResponse<>(POST_INQUIRE_EMPTY_CONTENT);
            if(postInquire.getTitle().length() < 1 || postInquire.getTitle().length() > 50) return new BaseResponse<>(POST_INQUIRE_INVALID_TITLE);
            if(postInquire.getContent().length() < 1 || postInquire.getContent().length() > 400) return new BaseResponse<>(POST_INQUIRE_INVALID_CONTENT);

            int userIdx = postInquire.getUserIdx();
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            int result = settingService.postInquire(postInquire);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공지사항 조회 API
     * [GET] /settings/notice/:userIdx
     * @return BaseResponse<GetNotice>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/notice/{userIdx}")
    public BaseResponse<List<GetNotice>> getNotice(@PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetNotice> getNotice = settingProvider.getNotice();
            return new BaseResponse<>(getNotice);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공지사항 상세 조회 API
     * [GET] /settings/:noticeIdx/:userIdx
     * @return BaseResponse<GetNoticeDetail>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{noticeIdx}/{userIdx}")
    public BaseResponse<GetNoticeDetail> getNoticeDetail(@PathVariable("noticeIdx") int noticeIdx, @PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetNoticeDetail getNoticeDetail = settingProvider.getNoticeDetail(noticeIdx);
            return new BaseResponse<>(getNoticeDetail);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
