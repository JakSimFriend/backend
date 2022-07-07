package com.example.demo.src.mychallenge;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
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
}
