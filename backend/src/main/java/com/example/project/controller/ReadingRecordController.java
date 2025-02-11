package com.example.project.controller;

import com.example.project.dto.ReadingRecordDto;
import com.example.project.entity.ReadingRecord;
import com.example.project.service.ReadingRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reading-records")
public class ReadingRecordController {
    private final ReadingRecordService readingRecordService;

    public ReadingRecordController(ReadingRecordService readingRecordService) {
        this.readingRecordService = readingRecordService;
    }

    @PostMapping
    public ResponseEntity<ReadingRecord> createRecord(@RequestBody ReadingRecordDto dto) {
        // DTO에서 필요한 값만 추출하여 addReadingRecord() 호출
        ReadingRecord record = readingRecordService.addReadingRecord(
            dto.getUserId(),
            dto.getBookId(),
            dto.getReadPages()
        );
        return ResponseEntity.ok(record);
    }
}