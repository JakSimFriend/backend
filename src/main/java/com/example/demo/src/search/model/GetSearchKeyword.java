package com.example.demo.src.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetSearchKeyword {
    private List<Integer> challengeIdxs;
    private List<GetRecruitment> recruitments;
    private List<GetEnd> ends;
}
