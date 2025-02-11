package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int targetBooks;  // 목표 책 개수
    private int completedBooks; // 읽은 책 개수
    private LocalDate startDate;
    private LocalDate endDate;

    public void updateProgress() {
        if (completedBooks >= targetBooks) {
            this.endDate = LocalDate.now(); // 목표 달성 시 완료 날짜 저장
        }
    }
}