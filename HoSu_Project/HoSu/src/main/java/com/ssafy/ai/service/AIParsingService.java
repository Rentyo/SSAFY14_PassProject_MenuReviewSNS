package com.ssafy.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.ai.dto.ParsedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AIParsingService {

  @Value("${gms.key}")
  private String apiKey;

  private static final String SSAFY_API_URL = "https://gms.ssafy.io/gmsapi/api.openai.com/v1/chat/completions";

  private static final String SYSTEM_PROMPT = """
      당신은 한국 식당 검색 어시스턴트입니다.
      사용자의 자연어 질문을 분석하여 JSON 형식으로만 응답하세요.

      응답 형식:
      {
        "sidoNo": "3",
        "gunguName": "동구",
        "category": "WESTERN",
        "tagName": "피자",
        "sortBy": "RATING"
      }

      === 시도 번호 ===
      서울:1, 인천:2, 대전:3, 대구:4, 광주:5, 부산:6, 울산:7, 세종:8
      경기:31, 강원:32, 충북:33, 충남:34, 전북:35, 전남:36, 경북:37, 경남:38, 제주:39

      === 군구 이름 ===
      서울: 강남구, 강동구, 강북구, 강서구, 관악구, 광진구, 구로구, 금천구, 노원구, 도봉구, 동대문구, 동작구, 마포구, 서대문구, 서초구, 성동구, 성북구, 송파구, 양천구, 영등포구, 용산구, 은평구, 종로구, 중구, 중랑구
      대전: 대덕구, 동구, 서구, 유성구, 중구
      부산: 강서구, 금정구, 남구, 동구, 동래구, 부산진구, 북구, 사상구, 사하구, 서구, 수영구, 연제구, 영도구, 중구, 해운대구, 기장군

      === 카테고리 ===
      KOREAN(한식), JAPANESE(일식), CHINESE(중식), WESTERN(양식), CHICKEN(치킨), SNACK(분식), CAFE(카페), MEAT(고기), SEAFOOD(해산물), PUB(술집), BAKERY(빵), FASTFOOD(패스트푸드), ASIAN(아시안), CURRY(커리)

      === 메뉴 태그 ===
      한식: 찌개, 제육, 불고기, 비빔밥, 알탕, 국밥
      중식: 짜장면, 짬뽕, 탕수육, 중식요리
      일식: 라멘, 가츠동, 초밥, 우동
      양식: 피자, 파스타, 리조또, 스테이크, 샐러드
      분식: 떡볶이, 순대, 분식튀김, 라면
      치킨: 치킨, 치즈볼
      고기: 삼겹살, 소고기, 갈비, 곱창
      해산물: 회덮밥, 사시미, 해물요리
      카페: 커피, 라떼, 모카, 바닐라라떼
      술집: 감자튀김, 소시지, 나초
      빵: 크루아상, 바게트, 소금빵, 피자빵
      패스트푸드: 버거, 탄산음료
      아시안: 팟타이, 나시고랭, 월남쌈, 마라
      커리: 카레

      === 정렬 기준 ===
      LATEST(최신순), RATING(평점높은순), REVIEW(리뷰많은순), VIEW(조회수많은순)

      === 추출 규칙 ===
      1. 지역만 언급: sidoNo만 추출
      2. 지역+군구 언급: sidoNo + gunguName 추출
      3. 카테고리 언급: category 추출
      4. 구체적 메뉴 언급: category + tagName 추출
      5. 정렬 키워드 감지:
         - "평점", "별점", "맛있는" → RATING
         - "리뷰", "후기", "인기" → REVIEW
         - "최신", "새로운", "신규" → LATEST
         - "유명한", "많이본", "핫한" → VIEW
         - 언급 없으면 생략

      === 예시 ===
      "대전 맛집" → {"sidoNo":"3"}
      "대전 동구 맛집" → {"sidoNo":"3","gunguName":"동구"}
      "대전 동구 피자집" → {"sidoNo":"3","gunguName":"동구","category":"WESTERN","tagName":"피자"}
      "평점 높은 치킨집" → {"category":"CHICKEN","sortBy":"RATING"}
      "강남 인기 많은 일식당" → {"sidoNo":"1","gunguName":"강남구","category":"JAPANESE","sortBy":"REVIEW"}
      "최신 카페" → {"category":"CAFE","sortBy":"LATEST"}

      추출 불가능한 정보는 생략하세요. 반드시 JSON만 응답하세요.
      """;

  public ParsedQuery parseNaturalLanguage(String userQuery) {
    try {
      RestTemplate restTemplate = new RestTemplate();

      // 요청 헤더 설정
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
      headers.set("Authorization", "Bearer " + apiKey);
      headers.set("Accept", "application/json");

      // 요청 바디 구성
      String requestBodyJson = String.format("""
          {
            "model": "gpt-4.1-nano",
            "messages": [
              {
                "role": "system",
                "content": %s
              },
              {
                "role": "user",
                "content": %s
              }
            ],
            "max_tokens": 200,
            "temperature": 0.3
          }
          """,
          new ObjectMapper().writeValueAsString(SYSTEM_PROMPT),
          new ObjectMapper().writeValueAsString(userQuery));

      HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

      log.info("Sending AI request for query: {}", userQuery);

      // API 호출
      ResponseEntity<String> response = restTemplate.exchange(
          SSAFY_API_URL,
          HttpMethod.POST,
          entity,
          String.class);

      log.info("AI API Response Status: {}", response.getStatusCode());

      // 응답 파싱
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> responseBody = mapper.readValue(response.getBody(), Map.class);

      if (responseBody.containsKey("choices")) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        if (!choices.isEmpty()) {
          Map<String, Object> firstChoice = choices.get(0);
          Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
          String content = (String) message.get("content");

          log.info("AI Response: {}", content);

          // JSON에서 중괄호만 추출
          String jsonContent = content.trim();
          if (jsonContent.contains("{")) {
            int start = jsonContent.indexOf("{");
            int end = jsonContent.lastIndexOf("}") + 1;
            jsonContent = jsonContent.substring(start, end);
          }

          ParsedQuery parsed = mapper.readValue(jsonContent, ParsedQuery.class);

          // 파싱 실패 시 폴백
          if (parsed.getSidoNo() == null && parsed.getCategory() == null &&
              parsed.getGunguName() == null && parsed.getTagName() == null) {
            parsed.setKeyword(userQuery);
          }

          log.info("Parsed result: {}", parsed);
          return parsed;
        }
      }

      // 응답이 없으면 폴백
      return ParsedQuery.builder().keyword(userQuery).build();

    } catch (Exception e) {
      log.error("AI 파싱 실패: {}", e.getMessage(), e);
      // 에러 시 폴백: 키워드 검색
      return ParsedQuery.builder()
          .keyword(userQuery)
          .build();
    }
  }
}
