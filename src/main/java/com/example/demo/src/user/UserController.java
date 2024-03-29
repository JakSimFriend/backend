package com.example.demo.src.user;

import com.example.demo.src.profile.model.GetProfile;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.http.ParseException;
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
import javax.servlet.http.HttpSession;

import java.util.List;

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
     * 유저 idx 조회 API
     * [GET] /users/idx
     * @return BaseResponse<Integer>
     */
    // Header
    @ResponseBody
    @GetMapping("/idx")
    public BaseResponse<Integer> getIdx() {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            return new BaseResponse<>(userIdxByJwt);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
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
        String deviceToken = request.getHeader("DEVICE-TOKEN");

        if (accessToken == null || accessToken.length() == 0) {
            return new BaseResponse<>(EMPTY_ACCESS_TOKEN);
        }

        if (deviceToken == null || deviceToken.length() == 0) {
            return new BaseResponse<>(EMPTY_DEVICE_TOKEN);
        }

        try {
            PostLoginRes postLoginRes = userService.createKakaoSignIn(accessToken, deviceToken);
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
        String deviceToken = request.getHeader("DEVICE-TOKEN");

        if (accessToken == null || accessToken.length() == 0) {
            return new BaseResponse<>(EMPTY_ACCESS_TOKEN);
        }

        if (deviceToken == null || deviceToken.length() == 0) {
            return new BaseResponse<>(EMPTY_DEVICE_TOKEN);
        }

        try {
            PostLoginRes postLoginRes = userService.createGoogleSignIn(accessToken, deviceToken);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 애플 로그인 API
     * [POST] /users/apple-signin
     * @RequestBody postAppleSignInReq
     * @return BaseResponse<PostUserSignInRes>
     */
    @ResponseBody
    @PostMapping("/users/apple-signin")
    public BaseResponse<PostLoginRes> postAppleSignIn(@RequestBody PostAppleSignInReq postAppleSignInReq) throws BaseException, ParseException, java.text.ParseException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String idToken = request.getHeader("APPLE-ID-TOKEN");
        String deviceToken = request.getHeader("DEVICE-TOKEN");
        String email = postAppleSignInReq.getEmail();

        if (deviceToken == null || deviceToken.length() == 0) {
            return new BaseResponse<>(EMPTY_DEVICE_TOKEN);
        }

        if (idToken == null || idToken.length() == 0) {
            return new BaseResponse<>(EMPTY_ID_TOKEN);
        }

        // idToken 디코딩하여 sub 뽑기
        SignedJWT signedJWT = SignedJWT.parse(idToken);
        ReadOnlyJWTClaimsSet payload = signedJWT.getJWTClaimsSet();
        String socialId = "apple_"+payload.getSubject();

        try {
            PostLoginRes postLoginRes = userService.createAppleSignIn(email, deviceToken);
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
     * 생일 등록 API
     * [POST] /users/birth
     *
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/birth")
    public BaseResponse<String> createBirth(@RequestBody PostBirth postBirth) {
        try {

            if (postBirth.getBirth() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_BIRTH);
            }

            int userIdx = postBirth.getUserIdx();
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.createBirth(postBirth);
            String result = "생일 등록에 성공하였습니다.";
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

    /**
     * 로그아웃 API
     * [PATCH] /users/:idx/logout
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{idx}/logout")
    public BaseResponse<String> logout(@PathVariable("idx") int userIdx){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.deleteDeviceToken(userIdx);
            String result = "성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
