package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostCertificationRes {
    private int certificationIdx;
    private int challengeIdx;
    private int userIdx;
    private String nickName;
    private int beforePercent;
    private int afterPercent;
}
