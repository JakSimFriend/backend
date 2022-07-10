package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetWaiting {
    private int userIdx;
    private int achievement;
    private String nickName;
    private String profile;
    private String promise;
}
