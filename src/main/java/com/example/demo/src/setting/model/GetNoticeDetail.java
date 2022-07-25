package com.example.demo.src.setting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetNoticeDetail {
    private int noticeIdx;
    private String title;
    private String date;
    private String content;
}
