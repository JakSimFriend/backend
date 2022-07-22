package com.example.demo.src.fcm;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.challenge.model.PostChallenge;
import com.example.demo.src.fcm.model.PostReaction;
import com.example.demo.src.mychallenge.MyChallengeProvider;
import com.example.demo.src.mychallenge.MyChallengeService;
import com.example.demo.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.POST_FAIL_CHALLENGE;

@RestController
@RequestMapping("/fcm")
public class FirebaseCloudMessageController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    @Autowired
    private final JwtService jwtService;

    public FirebaseCloudMessageController(FirebaseCloudMessageService firebaseCloudMessageService, JwtService jwtService) {
        this.firebaseCloudMessageService = firebaseCloudMessageService;
        this.jwtService = jwtService;
    }



    @PostMapping("/api")
    public ResponseEntity pushMessage(@RequestBody RequestDTO requestDTO) throws IOException {
        System.out.println(requestDTO.getTargetToken() + " "
                +requestDTO.getTitle() + " " + requestDTO.getBody() + " " + requestDTO.getImage());

        firebaseCloudMessageService.sendMessageTo(
                requestDTO.getTargetToken(),
                requestDTO.getTitle(),
                requestDTO.getBody(),
                requestDTO.getImage()
        );
        return ResponseEntity.ok().build();
    }

    /**
     * 리액션 API
     * [POST] /fcm/reaction
     * @return BaseResponse<String>
     */
    // Body
    @ResponseBody
    @PostMapping("/reaction")
    public ResponseEntity createReaction(@RequestBody PostReaction postReaction) {
        try {

            int userIdx = postReaction.getSenderIdx();
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return ResponseEntity.ok().build();
            }

            int result = firebaseCloudMessageService.createReaction(postReaction);
            return ResponseEntity.ok().build();

        } catch (BaseException exception) {
            return ResponseEntity.ok().build();
        }
    }
}
