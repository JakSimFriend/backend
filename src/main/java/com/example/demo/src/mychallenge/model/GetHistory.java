package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
public class GetHistory {
    private int challengeIdx;
    private String title;
    private String categoryName;
    private Date endDate;
    private int percent;
    private int rewardStatus;
}
