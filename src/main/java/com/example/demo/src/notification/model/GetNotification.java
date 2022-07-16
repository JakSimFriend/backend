package com.example.demo.src.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetNotification {
    private int alertIdx;
    private String image;
    private String alert;
    private String time;
}
