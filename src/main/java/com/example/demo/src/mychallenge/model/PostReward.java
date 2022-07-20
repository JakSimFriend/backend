package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostReward {
    private int challengeIdx;
    private int userIdx;
    private int achievement;
    private int point;
    private int experience;
}
