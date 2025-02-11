package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reading_goals")
public class ReadingGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String title;
    private String type;  // "책 권수", "감상문 개수", "독서 시간"
    private int targetValue;
    private String period;  // "월간", "연간"
    private String startDate;
    private String endDate;
}