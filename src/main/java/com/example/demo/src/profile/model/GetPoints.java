package com.example.demo.src.profile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPoints {
    private String categoryName;
    private String image;
    private String createAt;
    private int point;
    private int balance;
}
