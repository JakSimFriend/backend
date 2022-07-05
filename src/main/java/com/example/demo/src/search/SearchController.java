package com.example.demo.src.search;

import com.example.demo.src.search.SearchProvider;
import com.example.demo.src.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.search.model.*;
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
@RequestMapping("/searches")
public class SearchController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final SearchProvider searchProvider;
    @Autowired
    private final SearchService searchService;
    @Autowired
    private final JwtService jwtService;

    public SearchController(SearchProvider searchProvider, SearchService searchService, JwtService jwtService) {
        this.searchProvider = searchProvider;
        this.searchService = searchService;
        this.jwtService = jwtService;
    }

    /**
     * 카테고리별 검색 API
     * [GET] /searches/:categoryIdx/:userIdx
     * @return BaseResponse<List<GetChallengeSearch>>
     */
    //Query String
    @ResponseBody
    @GetMapping("/{categoryIdx}/{userIdx}")
    public BaseResponse<List<GetChallengeSearch>> getChallengeSearch(@PathVariable("categoryIdx") int categoryIdx, @PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(categoryIdx < 0 || categoryIdx > 8) return new BaseResponse<>(POST_CHALLENGES_INVALID_CATEGORY);

            if(categoryIdx == 0){
                List<GetChallengeSearch> getChallengeSearch = searchProvider.getChallengeSearch();
                if(getChallengeSearch.get(0).getRecruitments().size() == 0 && getChallengeSearch.get(0).getEnds().size() == 0){return new BaseResponse<>(NOT_EXIST_SEARCH);}
                return new BaseResponse<>(getChallengeSearch);
            }

            List<GetChallengeSearch> getCategorySearch = searchProvider.getCategorySearch(categoryIdx);
            if(getCategorySearch.get(0).getRecruitments().size() == 0 && getCategorySearch.get(0).getEnds().size() == 0){return new BaseResponse<>(NOT_EXIST_SEARCH);}
            return new BaseResponse<>(getCategorySearch);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
