package com.example.demo.src.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetChallengeHome {
    private String categoryName;
    private int challengeIdx;
    private String title;
    private String startDate;
    private String certification;
    private int waiting;
    private List<String> tags;
}
