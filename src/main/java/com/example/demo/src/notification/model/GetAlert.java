package com.example.demo.src.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetAlert {
    private int alertIdx;
    private String image;
    private String alert;
    private int certification;
    private String certificationPhoto;
    private String time;
    private int reportStatus;
}
