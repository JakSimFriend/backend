package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetApplying {
    private int acceptStatus;
    private int challengeIdx;
    private String title;
    private List<String> tags;
    private String remainingDay;
    private String certification;
    private int memberCount;
    private int needCount;
}
