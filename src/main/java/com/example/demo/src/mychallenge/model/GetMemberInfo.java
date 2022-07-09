package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMemberInfo {
    private int userIdx;
    private String nickName;
    private String profile;
    private String promise;
}
