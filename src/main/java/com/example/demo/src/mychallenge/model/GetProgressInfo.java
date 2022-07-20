package com.example.demo.src.mychallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetProgressInfo {
    private int challengeIdx;
    private String title;
    private Date startDate;
    private Date endDate;
    private List<GetDateList> dateLists;
    private int certificationStatus;
    private int remainingDay;
    private int remainingCount;
    private String date;
    private int limited;
    private String certificationInfo;
    private String deadline;
    private int memberCount;
    private List<GetProgressMember> members;
}
