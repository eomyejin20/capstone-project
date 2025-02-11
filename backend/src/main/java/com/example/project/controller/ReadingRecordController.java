package com.example.project.controller;

import com.example.project.dto.ReadingRecordDto;
import com.example.project.service.ReadingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reading-records")
@RequiredArgsConstructor
public class ReadingRecordController {

    private final ReadingRecordService readingRecordService;

    @PostMapping("/add")
    public ResponseEntity<ReadingRecordDto> addReadingRecord(@RequestParam Long userId,
                                                             @RequestParam Long bookId,
                                                             @RequestParam int readPages) {
        ReadingRecordDto recordDto = readingRecordService.addReadingRecord(userId, bookId, readPages);
        return ResponseEntity.ok(recordDto);
    }
}