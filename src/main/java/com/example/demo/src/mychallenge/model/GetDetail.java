package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetDetail {
    private int challengeIdx;
    private String title;
    private String content;
    private String date;
    private String certification;
    private String deadline;
    private int accept;
    private int waiting;
    private String tier;
}
