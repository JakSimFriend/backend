package com.example.demo.src.profile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetProfile {
    private int userIdx;
    private String profile;
    private String nickName;
    private String promise;
    private int point;
    private List<GetPoints> points;
}
