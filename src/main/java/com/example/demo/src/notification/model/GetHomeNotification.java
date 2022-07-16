package com.example.demo.src.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetHomeNotification {
    private String date;
    private List<GetNotification> notifications;
}
