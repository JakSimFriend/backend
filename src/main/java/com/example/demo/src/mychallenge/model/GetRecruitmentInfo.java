package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetRecruitmentInfo {
    private int challengeIdx;
    private String title;
    private Date startDate;
    private int remainingDay;
    private String date;
    private int limited;
    private String certification;
    private String deadline;
    private int waiting;
    private List<GetWaiting> waitings;
    private int memberCount;
    private List<GetMemberInfo> members;
}
