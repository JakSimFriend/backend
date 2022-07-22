package com.example.demo.src.status;

import com.example.demo.config.BaseException;
import com.example.demo.src.status.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class StatusProvider {
    private final StatusDao statusDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public StatusProvider(StatusDao statusDao, JwtService jwtService) {
        this.statusDao = statusDao;
        this.jwtService = jwtService;
    }

    public List<GetStatus> getStatus(int userIdx) throws BaseException {
        try {
            List<GetStatus> getStatus = statusDao.getStatus(userIdx);
            return getStatus;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
