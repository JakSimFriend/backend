package com.example.demo.src.status.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetStatus {
    private int userIdx;
    private int achievement;
    private int experience;
    private List<GetStatusCategory> categories;
}
