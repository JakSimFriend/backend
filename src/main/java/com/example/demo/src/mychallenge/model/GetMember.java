package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMember {
    private int userIdx;
    private String profile;
    private String nickName;
    private int percent;
}
