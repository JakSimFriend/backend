package com.example.demo.src.challenge;

import com.example.demo.src.challenge.ChallengeProvider;
import com.example.demo.src.challenge.ChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.challenge.model.*;
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
@RequestMapping("/challenges")
public class ChallengeController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ChallengeProvider challengeProvider;
    @Autowired
    private final ChallengeService challengeService;
    @Autowired
    private final JwtService jwtService;

    public ChallengeController(ChallengeProvider challengeProvider, ChallengeService challengeService, JwtService jwtService) {
        this.challengeProvider = challengeProvider;
        this.challengeService = challengeService;
        this.jwtService = jwtService;
    }

    /**
     * 챌린지 개설 API
     * [POST] /challenges
     * @return BaseResponse<String>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<Integer> createChallenge(@RequestBody PostChallenge postChallenge) {
        try {

            int userIdx = postChallenge.getUserIdx();
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(postChallenge.getTitle() == null) return new BaseResponse<>(POST_CHALLENGES_EMPTY_TITLE);
            if(postChallenge.getTitle().length() < 1 || postChallenge.getTitle().length() > 10) return new BaseResponse<>(POST_CHALLENGES_INVALID_TITLE);
            if(postChallenge.getContent() == null) return new BaseResponse<>(POST_CHALLENGES_EMPTY_CONTENT);
            if(postChallenge.getContent().length() < 1 || postChallenge.getContent().length() > 300) return new BaseResponse<>(POST_CHALLENGES_INVALID_CONTENT);
            if(postChallenge.getStartDate() == null) return new BaseResponse<>(POST_CHALLENGES_EMPTY_START);
            if(postChallenge.getCycle() == 0) return new BaseResponse<>(POST_CHALLENGES_EMPTY_CYCLE);
            if(!(postChallenge.getCycle() == 1 || postChallenge.getCycle() == 7 || postChallenge.getCycle() == 14)) return new BaseResponse<>(POST_CHALLENGES_INVALID_CYCLE);
            if(postChallenge.getCount() == 0) return new BaseResponse<>(POST_CHALLENGES_EMPTY_COUNT);
            if(postChallenge.getCount() < 1 || postChallenge.getCount() > 7) return new BaseResponse<>(POST_CHALLENGES_INVALID_COUNT);
            if(postChallenge.getDeadline() == null) return new BaseResponse<>(POST_CHALLENGES_EMPTY_DEADLINE);
            if(postChallenge.getCategoryIdx() == 0) return new BaseResponse<>(POST_CHALLENGES_EMPTY_CATEGORY);
            if(postChallenge.getCategoryIdx() < 1 || postChallenge.getCategoryIdx() > 8) return new BaseResponse<>(POST_CHALLENGES_INVALID_CATEGORY);
            if(postChallenge.getUserIdx() == 0) return  new BaseResponse<>(POST_CHALLENGES_EMPTY_USER);

            String tag;
            if(postChallenge.getTags() != null){
                for(int i = 0; i < postChallenge.getTags().size(); i++){
                    tag = postChallenge.getTags().get(i);
                    if(tag.length() < 1 || tag.length() > 4) return new BaseResponse<>(POST_CHALLENGES_INVALID_TAG);
                }
            }

            int result = challengeService.createChallenge(postChallenge);
            if(result == 0) {
                return new BaseResponse<>(POST_FAIL_CHALLENGE);
            }
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 챌린지 삭제 API
     * [PATCH] /challenges/:idx/:userIdx/delete
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{idx}/{userIdx}/delete")
    public BaseResponse<String> deleteChallenge(@PathVariable("idx") int challengeIdx, @PathVariable("userIdx") int userIdx){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            challengeService.deleteChallenge(challengeIdx, userIdx);
            String result = "성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
