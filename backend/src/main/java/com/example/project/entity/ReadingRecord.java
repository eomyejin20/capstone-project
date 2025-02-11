package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reading_records")
public class ReadingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private int readPages;  // 읽은 페이지 수
    private double progress; // 진행률 (%)
    private LocalDate startDate; // 읽기 시작한 날짜
    private LocalDate endDate; // 다 읽은 날짜

    @PrePersist
    public void prePersist() {
        this.startDate = LocalDate.now();  // 처음 생성될 때 시작 날짜 자동 설정
    }

    public void updateProgress() {
        if (book != null && book.getTotalPages() > 0) {
            this.progress = Math.min(((double) this.readPages / book.getTotalPages()) * 100, 100); // 100% 초과 방지

            // 진행률이 100%이면 endDate 저장
            if (this.progress == 100) {
                this.endDate = LocalDate.now();
            }
        } else {
            this.progress = 0;
        }
    }
}