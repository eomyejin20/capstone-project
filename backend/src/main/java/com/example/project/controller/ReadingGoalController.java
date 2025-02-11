package com.example.project.controller;

import com.example.project.dto.ReadingGoalDto;
import com.example.project.entity.ReadingGoal;
import com.example.project.service.ReadingGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reading-goals")
@RequiredArgsConstructor
public class ReadingGoalController {

    private final ReadingGoalService readingGoalService;

    // 목표 생성
    @PostMapping
    public ResponseEntity<ReadingGoalDto> createGoal(@RequestBody ReadingGoalDto goalDto) {
        ReadingGoal goal = readingGoalService.saveGoal(goalDto.toEntity());
        return ResponseEntity.ok(ReadingGoalDto.fromEntity(goal));
    }

    // 특정 유저의 목표 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<ReadingGoalDto>> getUserGoals(@PathVariable Long userId) {
        List<ReadingGoalDto> goals = readingGoalService.getUserGoals(userId)
                .stream()
                .map(ReadingGoalDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(goals);
    }
}