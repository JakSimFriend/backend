package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetProceeding {
    private int challengeIdx;
    private String title;
    private int certification;
    private List<GetMember> members;
}
