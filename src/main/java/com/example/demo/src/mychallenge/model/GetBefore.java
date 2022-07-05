package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetBefore {
    private int challengeIdx;
    private String categoryName;
    private String title;
    private String remainingDay;
    private String certification;
    private int accept;
    private List<String> tags;
}
