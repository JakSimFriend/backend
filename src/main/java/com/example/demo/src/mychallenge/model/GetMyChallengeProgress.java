package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetMyChallengeProgress {
    private int proceedingCount;
    private List<GetProceeding> proceedings;
    private int beforeCount;
    private List<GetBefore> befores;
}
