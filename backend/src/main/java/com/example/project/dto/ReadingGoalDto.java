package com.example.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadingGoalDto {
    private Long userId;
    private String title;
    private String type;
    private int targetValue;
    private String period;
    private String startDate;
    private String endDate;
}