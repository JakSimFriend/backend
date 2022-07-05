package com.example.demo.src.mychallenge;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.mychallenge.model.GetMyChallengeProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
