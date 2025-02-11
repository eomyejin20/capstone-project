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

    private int targetReviews; // 목표 감상문 개수
    private int completedReviews; // 작성한 감상문 개수

    private int targetMinutes; // 목표 독서 시간 (분)
    private int completedMinutes; // 실제 독서 시간 (분)

    private LocalDate startDate;
    private LocalDate endDate;

    // 목표 진행률 업데이트
    public void updateProgress() {
        boolean isBooksCompleted = (targetBooks > 0) && (completedBooks >= targetBooks);
        boolean isReviewsCompleted = (targetReviews > 0) && (completedReviews >= targetReviews);
        boolean isMinutesCompleted = (targetMinutes > 0) && (completedMinutes >= targetMinutes);

        // 세 가지 목표 중 하나라도 설정되어 있고, 모두 달성되었으면 목표 완료 처리
        if ((targetBooks > 0 || targetReviews > 0 || targetMinutes > 0) 
                && isBooksCompleted && isReviewsCompleted && isMinutesCompleted) {
            this.endDate = LocalDate.now();
        }
    }
}