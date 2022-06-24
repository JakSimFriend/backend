package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
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
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 카카오 로그인 API
     * [POST] /users/kakao-signin
     * @return BaseResponse<PostUserSignInRes>
     */
    @ResponseBody
    @PostMapping("/kakao-login")
    public BaseResponse<PostLoginRes> postKakaoLogIn() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String accessToken = request.getHeader("KAKAO-ACCESS-TOKEN");

        if (accessToken == null || accessToken.length() == 0) {
            return new BaseResponse<>(EMPTY_ACCESS_TOKEN);
        }

        try {
            PostLoginRes postLoginRes = userService.createKakaoSignIn(accessToken);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 닉네임 중복 확인 API
     * [POST] /users/nickname/check
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/nickname/check")
    public BaseResponse<String> checkNickName(@RequestBody PostUserNickName postUserNickName) {
        try {
            if(postUserNickName.getNickName() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
            }
            if(postUserNickName.getNickName().length() < 1 || postUserNickName.getNickName().length() > 8){
                return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
            }

            int userIdx = postUserNickName.getUserIdx();
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.checkNickName(postUserNickName);
            String result = "중복확인 완료되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
