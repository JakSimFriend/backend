package com.example.demo.src.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetChallengeAlert {
    private String date;
    private List<GetAlert> alerts;
}
