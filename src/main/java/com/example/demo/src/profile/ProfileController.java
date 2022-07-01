package com.example.demo.src.profile;

import com.example.demo.src.profile.ProfileProvider;
import com.example.demo.src.profile.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.profile.model.*;
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
@RequestMapping("/profiles")
public class ProfileController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ProfileProvider profileProvider;
    @Autowired
    private final ProfileService profileService;
    @Autowired
    private final JwtService jwtService;

    public ProfileController(ProfileProvider profileProvider, ProfileService profileService, JwtService jwtService) {
        this.profileProvider = profileProvider;
        this.profileService = profileService;
        this.jwtService = jwtService;
    }

    /**
     * 다짐하기 수정 API
     * [POST] /profiles/promise
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/promise")
    public BaseResponse<String> modifyPromise(@RequestBody PostPromise postPromise) {
        try {
            if (postPromise.getPromise() == null) {
                return new BaseResponse<>(POST_PROFILES_EMPTY_PROMISE);
            }
            if (postPromise.getPromise().length() < 1 || postPromise.getPromise().length() > 10) {
                return new BaseResponse<>(POST_PROFILES_INVALID_PROMISE);
            }

            int userIdx = postPromise.getUserIdx();
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            profileService.modifyPromise(postPromise);
            String result = "작심 다짐하기 변경에 성공하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 프로필 수정 화면 조회 API
     * [GET] /profiles/:idx/edit
     *
     * @return BaseResponse<GetProfileEdit>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{idx}/edit")
    public BaseResponse<GetProfileEdit> getProfileEdit(@PathVariable("idx") int userIdx) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetProfileEdit getProfileEdit = profileProvider.getProfileEdit(userIdx);
            return new BaseResponse<>(getProfileEdit);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

}
