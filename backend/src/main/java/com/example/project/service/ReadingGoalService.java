package com.example.project.service;

import com.example.project.entity.ReadingGoal;
import com.example.project.repository.ReadingGoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadingGoalService {
    private final ReadingGoalRepository readingGoalRepository;

    public ReadingGoalService(ReadingGoalRepository readingGoalRepository) {
        this.readingGoalRepository = readingGoalRepository;
    }

    public ReadingGoal saveGoal(ReadingGoal goal) {
        return readingGoalRepository.save(goal);
    }

    public List<ReadingGoal> getUserGoals(Long userId) {
        return readingGoalRepository.findByUserId(userId);
    }
}