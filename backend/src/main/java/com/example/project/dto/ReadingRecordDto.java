package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ReadingRecordDto {
    private Long id;          // 기록 ID
    private Long userId;      // 유저 ID
    private Long bookId;      // 책 ID
    private int readPages;    // 읽은 페이지 수
    private int totalPages;   // 전체 페이지 수
    private double progress;  // 진행률 (%)
    private LocalDate startDate; // 읽기 시작한 날짜
    private LocalDate endDate;   // 다 읽은 날짜 (100% 시)
}