package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetMyChallengeHistory {
    private int year;
    private int count;
    private List<GetHistory> histories;
}
