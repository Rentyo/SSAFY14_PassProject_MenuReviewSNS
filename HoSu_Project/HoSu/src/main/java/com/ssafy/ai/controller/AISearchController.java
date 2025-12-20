package com.ssafy.ai.controller;

import com.ssafy.ai.dto.AISearchRequest;
import com.ssafy.ai.dto.ParsedQuery;
import com.ssafy.ai.service.AIParsingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Search", description = "AI 자연어 검색 API")
public class AISearchController {

    private final AIParsingService aiParsingService;

    @PostMapping("/parse-query")
    @Operation(summary = "자연어 검색 파싱", description = "사용자의 자연어 입력을 검색 조건으로 변환합니다.")
    public ResponseEntity<ParsedQuery> parseNaturalLanguage(@RequestBody AISearchRequest request) {
        log.info("AI 검색 요청: {}", request.getQuery());

        try {
            ParsedQuery parsed = aiParsingService.parseNaturalLanguage(request.getQuery());
            log.info("파싱 결과: {}", parsed);
            return ResponseEntity.ok(parsed);
        } catch (Exception e) {
            log.error("AI 파싱 중 오류 발생", e);
            // 에러 시에도 키워드 검색으로 폴백
            ParsedQuery fallback = ParsedQuery.builder()
                    .keyword(request.getQuery())
                    .build();
            return ResponseEntity.ok(fallback);
        }
    }
}
