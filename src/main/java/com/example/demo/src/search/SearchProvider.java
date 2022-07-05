package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.search.SearchDao;
import com.example.demo.src.search.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class SearchProvider {
    private final SearchDao searchDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SearchProvider(SearchDao searchDao, JwtService jwtService) {
        this.searchDao = searchDao;
        this.jwtService = jwtService;
    }

    public List<GetChallengeSearch> getChallengeSearch() throws BaseException{
        try{
            List<GetChallengeSearch> getChallengeSearch = searchDao.getChallengeSearch();
            return getChallengeSearch;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetChallengeSearch> getCategorySearch(int categoryIdx) throws BaseException{
        try{
            List<GetChallengeSearch> getCategorySearch = searchDao.getCategorySearch(categoryIdx);
            return getCategorySearch;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
