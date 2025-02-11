package com.example.project.service;

import com.example.project.dto.ReadingRecordDto;
import com.example.project.entity.Book;
import com.example.project.entity.ReadingRecord;
import com.example.project.entity.User;
import com.example.project.repository.BookRepository;
import com.example.project.repository.ReadingRecordRepository;
import com.example.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReadingRecordService {

    private final ReadingRecordRepository readingRecordRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public ReadingRecordDto addReadingRecord(Long userId, Long bookId, int readPages) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("책을 찾을 수 없음"));

        ReadingRecord record = readingRecordRepository.findByUserIdAndBookId(userId, bookId)
                .orElse(new ReadingRecord());

        if (record.getStartDate() == null) {
            record.setStartDate(LocalDate.now());
        }

        record.setUser(user);
        record.setBook(book);
        record.setReadPages(readPages);
        record.updateProgress(); // 진행률 자동 계산

        readingRecordRepository.save(record);

        return new ReadingRecordDto(
                record.getId(),
                userId,
                bookId,
                readPages,
                book.getTotalPages(),
                record.getProgress(), // 서비스에서 진행률 계산 후 DTO에 넣음
                record.getStartDate(),
                record.getEndDate()
        );
    }
}