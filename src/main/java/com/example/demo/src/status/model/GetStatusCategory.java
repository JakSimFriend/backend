package com.example.demo.src.status.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStatusCategory {
    private int categoryIdx;
    private String categoryPhoto;
    private String categoryName;
    private int categoryEx;
}
