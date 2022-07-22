package com.example.demo.src.status.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStatusDetail {
    private int categoryIdx;
    private String categoryName;
    private String categoryPhoto;
    private int challengeIdx;
    private String title;
    private String endDate;
    private int experience;
    private int total;
}
