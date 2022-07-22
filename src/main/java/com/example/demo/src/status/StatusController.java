package com.example.demo.src.status;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.status.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/status")
public class StatusController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final StatusProvider statusProvider;
    @Autowired
    private final StatusService statusService;
    @Autowired
    private final JwtService jwtService;

    public StatusController(StatusProvider statusProvider, StatusService statusService, JwtService jwtService) {
        this.statusProvider = statusProvider;
        this.statusService = statusService;
        this.jwtService = jwtService;
    }

    /**
     * 현황 조회 API
     * [GET] /status/:userIdx
     * @return BaseResponse<GetStatus>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<List<GetStatus>> getStatus(@PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetStatus> getStatus = statusProvider.getStatus(userIdx);
            if(getStatus.get(0).getUserIdx() == 0) throw new BaseException(NOTHING_STATUS);
            return new BaseResponse<>(getStatus);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 현황 상세 조회 API
     * [GET] /status/:userIdx/detail
     * @return BaseResponse<GetStatus>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}/detail")
    public BaseResponse<List<GetStatusDetail>> getStatusDetail(@PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetStatusDetail> getStatusDetail = statusProvider.getStatusDetail(userIdx);
            if(getStatusDetail.size() == 0) throw new BaseException(NOTHING_STATUS);
            return new BaseResponse<>(getStatusDetail);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
