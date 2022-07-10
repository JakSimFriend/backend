package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetMyChallengeApplication {
    private int recruitmentCount;
    private List<GetRecruitment> recruitments;
    private int applyingCount;
    private List<GetApplying> applyings;
}
