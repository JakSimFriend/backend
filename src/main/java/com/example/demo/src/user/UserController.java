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

import static com.example.demo.config.BaseResponseStatus.*;

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

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 카카오 로그인 API
     * [POST] /users/kakao-signin
     *
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
     * 구글 로그인 API
     * [POST] /users/kakao-login
     *
     * @return BaseResponse<PostUserSignInRes>
     */
    @ResponseBody
    @PostMapping("/google-login")
    public BaseResponse<PostLoginRes> postGoogleLogIn() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String accessToken = request.getHeader("GOOGLE-ACCESS-TOKEN");

        if (accessToken == null || accessToken.length() == 0) {
            return new BaseResponse<>(EMPTY_ACCESS_TOKEN);
        }

        try {
            PostLoginRes postLoginRes = userService.createGoogleSignIn(accessToken);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 닉네임 중복 확인 API
     * [POST] /users/nickname/check
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/nickname/check")
    public BaseResponse<String> checkNickName(@RequestBody PostUserNickName postUserNickName) {
        try {
            if (postUserNickName.getNickName() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
            }
            if (postUserNickName.getNickName().length() < 1 || postUserNickName.getNickName().length() > 8) {
                return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
            }

            int userIdx = postUserNickName.getUserIdx();
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.checkNickName(postUserNickName);
            String result = "중복확인 완료되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 추천인 확인 API
     * [POST] /users/check
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/check")
    public BaseResponse<String> userCheck(@RequestBody PostUserNickName postUserNickName) {
        try {
            if (postUserNickName.getNickName() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
            }
            if (postUserNickName.getNickName().length() < 1 || postUserNickName.getNickName().length() > 8) {
                return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
            }

            int userIdx = postUserNickName.getUserIdx();
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.userCheck(postUserNickName);
            String result = "존재하는 유저입니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 닉네임 설정 API
     * [POST] /users/nickname
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/nickname")
    public BaseResponse<String> createNickName(@RequestBody PostUserInfo postUserInfo) {
        try {

            if (postUserInfo.getNickName() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
            }
            if (postUserInfo.getNickName().length() < 1 || postUserInfo.getNickName().length() > 8) {
                return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
            }

            int userIdx = postUserInfo.getUserIdx();
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if (userIdx == postUserInfo.getRecommenderIdx()) {
                return new BaseResponse<>(POST_USERS_INVALID_RECOMMENDER);
            }

            String result;
            if (postUserInfo.getRecommenderIdx() == 0) {
                userService.createNickName(postUserInfo);
                result = "닉네임 설정에 성공하였습니다.";
                return new BaseResponse<>(result);
            }

            userService.createNickName(postUserInfo);
            userService.createRecommender(postUserInfo);

            result = "닉네임 설정 및 추천인 등록에 성공하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 탈퇴 API
     * [PATCH] /users/:idx/delete
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{idx}/delete")
    public BaseResponse<String> deleteUser(@PathVariable("idx") int userIdx){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.deleteUser(userIdx);
            String result = "성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
