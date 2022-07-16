package com.example.demo.src.setting;

import com.example.demo.src.setting.SettingProvider;
import com.example.demo.src.setting.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
}
