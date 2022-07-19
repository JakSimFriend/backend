package com.example.demo.src.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChallengeDetail {
    private int challengeIdx;
    private String title;
    private String content;
    private int pee;
    private String date;
    private String certification;
    private String deadline;
    private int accept;
    private int waiting;
    private String tier;
    private int myPoint;
    private int existStatus;
}
