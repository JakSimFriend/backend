package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProgressMember {
    private int userIdx;
    private String profile;
    private String nickName;
    private String promise;
    private int percent;
    private String certification;
}
