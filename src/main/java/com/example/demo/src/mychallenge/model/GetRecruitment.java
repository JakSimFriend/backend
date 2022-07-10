package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetRecruitment {
    private int challengeIdx;
    private String title;
    private String remainingDay;
    private int memberCount;
    private int waiting;
}
