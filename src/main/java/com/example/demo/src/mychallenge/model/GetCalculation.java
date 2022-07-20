package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCalculation {
    private int challengeIdx;
    private String title;
    private String nowTime;
    private int achievement;
    private int totalCash;
    private int refundCash;
    private int experience;
    private int individual;
    private int friend;
    private int bonus;
    private int exitStatus;
}
