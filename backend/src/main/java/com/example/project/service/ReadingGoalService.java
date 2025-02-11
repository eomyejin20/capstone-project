package com.example.project.service;

import com.example.project.entity.ReadingGoal;
import com.example.project.repository.ReadingGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReadingGoalService {
    private final ReadingGoalRepository readingGoalRepository;

    public ReadingGoalService(ReadingGoalRepository readingGoalRepository) {
        this.readingGoalRepository = readingGoalRepository;
    }

    // 목표 저장 (신규 생성 및 수정)
    public ReadingGoal saveGoal(ReadingGoal goal) {
        if (goal.getStartDate() == null) {
            goal.setStartDate(LocalDate.now());
        }
        return readingGoalRepository.save(goal);
    }

    // 특정 유저의 목표 리스트 조회
    public List<ReadingGoal> getUserGoals(Long userId) {
        return readingGoalRepository.findByUserId(userId);
    }

    // 목표 진행률 업데이트
    @Transactional
    public void updateReadingProgress(Long goalId, int readBooks, int writtenReviews, int readingMinutes) {
        ReadingGoal goal = readingGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("목표를 찾을 수 없음"));

        goal.setCompletedBooks(goal.getCompletedBooks() + readBooks);
        goal.setCompletedReviews(goal.getCompletedReviews() + writtenReviews);
        goal.setCompletedMinutes(goal.getCompletedMinutes() + readingMinutes);

        // 목표 달성 여부 확인
        goal.updateProgress();

        readingGoalRepository.save(goal);
    }
}