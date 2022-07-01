package com.example.demo.src.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostChallenge {
    private String title;
    private String content;
    private Date startDate;
    private int cycle;
    private int count;
    private Time deadline;
    private int categoryIdx;
    private int userIdx;
    private List<String> tags;
}
