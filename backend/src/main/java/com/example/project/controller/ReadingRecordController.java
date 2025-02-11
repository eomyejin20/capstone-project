package com.example.project.controller;

import com.example.project.dto.BookDto;
import com.example.project.dto.ReadingRecordDto; // ReadingRecordDto 임포트
import com.example.project.service.AladinService;
import com.example.project.entity.ReadingRecord;
import com.example.project.service.ReadingRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reading-records")
public class ReadingRecordController {

    private final ReadingRecordService readingRecordService;
    private final AladinService aladinService;

    public ReadingRecordController(ReadingRecordService readingRecordService, AladinService aladinService) {
        this.readingRecordService = readingRecordService;
        this.aladinService = aladinService;
    }

    // 책 검색 API 추가
    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> searchBooks(@RequestParam String keyword) {
        List<BookDto> books = aladinService.searchBooks(keyword); // 알라딘 API 호출
        return ResponseEntity.ok(books); // 결과 반환
    }

    // 독서 기록 생성 API
    @PostMapping
    public ResponseEntity<ReadingRecord> createRecord(@RequestBody ReadingRecordDto dto) {
        // ReadingRecordDto에서 필요한 값을 가져와서 ReadingRecordService에 전달
        Long userId = dto.getUserId();
        Long bookId = dto.getBookId();
        int readPages = dto.getReadPages();

        // ReadingRecordService의 addReadingRecord 메서드 호출
        return ResponseEntity.ok(readingRecordService.addReadingRecord(userId, bookId, readPages));
    }
}