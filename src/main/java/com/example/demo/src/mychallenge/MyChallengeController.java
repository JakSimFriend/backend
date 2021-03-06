package com.example.demo.src.mychallenge;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.challenge.model.PostChallengeJoin;
import com.example.demo.src.mychallenge.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/my-challenges")
public class MyChallengeController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final MyChallengeProvider myChallengeProvider;
    @Autowired
    private final MyChallengeService myChallengeService;
    @Autowired
    private final JwtService jwtService;

    public MyChallengeController(MyChallengeProvider myChallengeProvider, MyChallengeService myChallengeService, JwtService jwtService) {
        this.myChallengeProvider = myChallengeProvider;
        this.myChallengeService = myChallengeService;
        this.jwtService = jwtService;
    }

    /**
     *  진행 화면 조회 API
     * [GET] my-challenges/:userIdx/progress
     * @return BaseResponse<List<GetMyChallengeProgress>>
     */
    //Query String
    @ResponseBody
    @GetMapping("/{userIdx}/progress")
    public BaseResponse<List<GetMyChallengeProgress>> getMyChallengeProgress(@PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetMyChallengeProgress> getMyChallengeProgress = myChallengeProvider.getMyChallengeProgress(userIdx);
            if(getMyChallengeProgress.get(0).getProceedings().size() == 0 && getMyChallengeProgress.get(0).getBefores().size() == 0){return new BaseResponse<>(NOT_EXIST_SEARCH);}
            return new BaseResponse<>(getMyChallengeProgress);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 챌린지 인증하기 API
     * [POST] /my-challenges/:idx/:userIdx/certification
     * @return BaseResponse<PostCertificationRes>
     */
    @ResponseBody
    @PostMapping("/{idx}/{userIdx}/certification")
    public BaseResponse<PostCertificationRes> createCertification(@PathVariable("idx") int challengeIdx, @PathVariable("userIdx") int userIdx, @RequestParam("images") @RequestPart MultipartFile multipartFile) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            PostCertificationRes postCertificationRes = myChallengeService.createCertification(challengeIdx, userIdx, multipartFile);
            return new BaseResponse<>(postCertificationRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     *  진행중 정보 조회 API
     * [GET] /my-challenges/:idx/:userIdx/progress-info
     * @return BaseResponse<List<GetProgressInfo>>
     */
    //Query String
    @ResponseBody
    @GetMapping("/{idx}/{userIdx}/progress-info")
    public BaseResponse<List<GetProgressInfo>> getProgressInfo(@PathVariable("idx") int challengeIdx, @PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetProgressInfo> getProgressInfo = myChallengeProvider.getProgressInfo(challengeIdx, userIdx);
            return new BaseResponse<>(getProgressInfo);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     *  시작전 정보 조회 API
     * [GET] /my-challenges/:idx/userIdx/before-info
     * @return BaseResponse<List<GetProgressInfo>>
     */
    //Query String
    @ResponseBody
    @GetMapping("/{idx}/{userIdx}/before-info")
    public BaseResponse<List<GetBeforeInfo>> getBeforeInfo(@PathVariable("idx") int challengeIdx, @PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetBeforeInfo> getBeforeInfo = myChallengeProvider.getBeforeInfo(challengeIdx, userIdx);
            return new BaseResponse<>(getBeforeInfo);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     *  진행 화면 조회 API
     * [GET] /my-challenges/:userIdx/application
     * @return BaseResponse<List<GetMyChallengeApplication>>
     */
    //Query String
    @ResponseBody
    @GetMapping("/{userIdx}/application")
    public BaseResponse<List<GetMyChallengeApplication>> getMyChallengeApplication(@PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetMyChallengeApplication> getMyChallengeApplication = myChallengeProvider.getMyChallengeApplication(userIdx);
            if(getMyChallengeApplication.get(0).getRecruitments().size() == 0 && getMyChallengeApplication.get(0).getApplyings().size() == 0){return new BaseResponse<>(NOT_EXIST_SEARCH);}
            return new BaseResponse<>(getMyChallengeApplication);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     *  모집중 정보 조회 API
     * [GET] /my-challenges/:idx/:userIdx/recruitment-info
     * @return BaseResponse<List<GetRecruitmentInfo>>
     */
    @ResponseBody
    @GetMapping("/{idx}/{userIdx}/recruitment-info")
    public BaseResponse<List<GetRecruitmentInfo>> getRecruitmentInfo(@PathVariable("idx") int challengeIdx, @PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetRecruitmentInfo> getRecruitmentInfo = myChallengeProvider.getRecruitmentInfo(challengeIdx, userIdx);
            return new BaseResponse<>(getRecruitmentInfo);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     *  내 챌린지 기록 조회 API
     * [GET] /my-challenges/:userIdx/record
     * @return BaseResponse<List<GetRecruitmentInfo>>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/record")
    public BaseResponse<List<GetMyChallengeHistory>> getMyChallengeHistory(@PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetMyChallengeHistory> getMyChallengeHistory = myChallengeProvider.getMyChallengeHistory(userIdx);
            return new BaseResponse<>(getMyChallengeHistory);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 챌린지 정보 조회 API
     * [GET] /my-challenges/:idx/:userIdx/detail
     *
     * @return BaseResponse<GetDetail>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{idx}/{userIdx}/detail")
    public BaseResponse<GetDetail> getDetail(@PathVariable("idx") int challengeIdx, @PathVariable("userIdx") int userIdx) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetDetail getDetail = myChallengeProvider.getDetail(challengeIdx);
            if(getDetail == null){return new BaseResponse<>(NOT_EXIST_DETAIL);}
            return new BaseResponse<>(getDetail);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 진행중 정산 조회 API
     * [GET] /my-challenges/:idx/:useridx/progress-calculation
     *
     * @return BaseResponse<GetCalculation>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{idx}/{userIdx}/progress-calculation")
    public BaseResponse<GetCalculation> getCalculation(@PathVariable("idx") int challengeIdx, @PathVariable("userIdx") int userIdx) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetCalculation getCalculation = myChallengeProvider.getCalculation(challengeIdx, userIdx);
            if(getCalculation == null){return new BaseResponse<>(NOT_EXIST_DETAIL);}
            return new BaseResponse<>(getCalculation);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 보상받기 API
     * [POST] /my-challenges/reward
     * @return BaseResponse<PostReward>
     */
    // Body
    @ResponseBody
    @PostMapping("/reward")
    public BaseResponse<String> joinChallenge(@RequestBody PostReward postReward) {
        try {
            int userIdx = postReward.getUserIdx();
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(postReward.getUserIdx() == 0) return new BaseResponse<>(POST_REWARDS_EMPTY_USERIDX);
            if(postReward.getChallengeIdx() == 0) return new BaseResponse<>(POST_REWARDS_EMPTY_CHALLENGEIDX);
            if(postReward.getPoint() == 0) return new BaseResponse<>(POST_REWARDS_EMPTY_POINT);
            if(postReward.getExperience() == 0) return new BaseResponse<>(POST_REWARDS_EMPTY_EXPERIENCE);
            if(postReward.getAchievement() == 0) return new BaseResponse<>(POST_REWARDS_EMPTY_ACHIEVEMENT);

            if(postReward.getPoint() < 0 || postReward.getPoint() > 6000) return new BaseResponse<>(POST_REWARDS_INVALID_POINT);
            if(postReward.getExperience() < 0 || postReward.getExperience() > 2200) return new BaseResponse<>(POST_REWARDS_INVALID_EXPERIENCE);
            if(postReward.getAchievement() < 0 || postReward.getAchievement() > 100) return new BaseResponse<>(POST_REWARDS_INVALID_ACHIEVEMENT);

            String message = "";
            int result = myChallengeService.postReward(postReward);
            if(result == 1) message = "성공";

            return new BaseResponse<>(message);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
