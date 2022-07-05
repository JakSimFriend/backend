package com.example.demo.src.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetEnd {
    private int challengeIdx;
    private String title;
    private String categoryName;
    private String endStatus;
    private String certification;
    private int accept;
    private List<String> tags;
}
