package com.example.demo.src.profile;

import com.example.demo.config.BaseException;
import com.example.demo.src.profile.ProfileDao;
import com.example.demo.src.profile.ProfileProvider;
import com.example.demo.src.profile.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class ProfileService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProfileDao profileDao;
    private final ProfileProvider profileProvider;
    private final JwtService jwtService;


    @Autowired
    public ProfileService(ProfileDao profileDao, ProfileProvider profileProvider, JwtService jwtService) {
        this.profileDao = profileDao;
        this.profileProvider = profileProvider;
        this.jwtService = jwtService;
    }

    public void modifyPromise(PostPromise postPromise) throws BaseException {
        try {
            int result = profileDao.modifyPromise(postPromise);
            if (result == 0) {
                throw new BaseException(POST_FAIL_PROMISE);
            }
        } catch (Exception exception) {
            throw new BaseException(POST_FAIL_PROMISE);
        }
    }
}
