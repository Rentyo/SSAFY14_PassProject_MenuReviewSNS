package com.ssafy.aiSns.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.aiSns.dto.AiExtractResult;
import com.ssafy.aiSns.mapper.AISnsMapper;
import com.ssafy.sns.dto.SnsBoardResp;

@Service
public class AISnsSesrviceImpl implements AISnsService {

    @Autowired
    private AISnsMapper aiSnsIdMapper;

    @Value("${gms.key}")
    private String gmsKey;

    private final ObjectMapper om = new ObjectMapper();

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://gms.ssafy.io/gmsapi/api.openai.com/v1")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    // ✅ AI가 JSON으로만 뽑게 강제
    // ✅ AI가 JSON으로만 뽑게 강제
    private static final String DEV_PROMPT = """
            너는 '식당 SNS 리뷰 검색'을 위한 세밀한 조건 추출기다.
            사용자의 자연어 질의를 분석하여 DB 검색을 위한 정확한 키워드를 JSON으로 추출해라.

            반드시 아래 JSON 스키마로만 출력해라(설명/문장 금지).

            {
              "sidoName": string|null,      // "서울", "경기", "대전" 등 시/도 명 (예: "서울시" -> "서울", "대전광역시" -> "대전")
              "gunguName": string|null,     // "강남구", "서구", "유성구" 등 구/군 명 (예: "강남" -> "강남구")
              "restaurantName": string|null,// 특정 식당 이름이 언급된 경우만 추출
              "categories": string[],       // 아래 ENUM 중 하나 이상 선택 (복수 선택 가능)
              "menuNames": string[],        // 구체적인 메뉴 이름 (예: "파스타", "짬뽕", "김치찌개")
              "tagNames": string[],         // 맛, 분위기, 목적 등 해시태그로 쓰일법한 단어 (예: "매운맛", "데이트", "가성비", "노포")
              "features": string[]          // 본문에 포함될법한 특징적 단어 (tagNames와 중복되어도 됨, 뉘앙스/형용사 위주)
            }

            [ENUM - categories]
            KOREAN, CHINESE, JAPANESE, WESTERN, SNACK, CHICKEN, MEAT, SEAFOOD, CAFE, PUB, BAKERY, FASTFOOD, ASIAN, CURRY

            [추출 가이드]
            1. 지역명은 표준 행정구역명으로 변환하여 추출해라. (전국 대상이면 null)
            2. 'features'와 'tagNames'는 검색 확률을 높이기 위해, 중요한 형용사나 키워드는 양쪽에 모두 포함시켜라.
               (예: "매콤한 짬뽕" -> menuNames=["짬뽕"], tagNames=["매콤", "매운맛"], features=["매콤", "얼큰"])
            3. 'menuNames'는 식재료나 요리명 그 자체만 추출해라.
            4. 값이 없으면 null 또는 빈 배열 [] 로 둬라.

            유효한 JSON만 출력해라.
            """;

    private static final String REVIEW_PROMPT_TEMPLATE = """
            너는 맛집 추천 AI다.
            사용자 검색어: "%s"

            추천된 게시물들:
            %s

            각 게시물에 대해 사용자 검색어와 연관지어 '이 메뉴/식당을 추천하는 이유'를 한 줄 평(20~40자, 친절한 해요체, 이모지 1개 포함)으로 작성해라.
            반드시 아래 JSON 배열 형식을 지켜라:
            [
              { "boardId": 게시물ID(숫자), "review": "한줄평내용" }
            ]
            """;

    public List<SnsBoardResp> getTop5BoardIds(String query) throws Exception {
        AiExtractResult cond = extractByAi(query);
        System.out.println(cond);
        if (cond.getCategories() == null)
            cond.setCategories(new ArrayList<>());
        if (cond.getMenuNames() == null)
            cond.setMenuNames(new ArrayList<>());
        if (cond.getTagNames() == null)
            cond.setTagNames(new ArrayList<>());
        if (cond.getFeatures() == null)
            cond.setFeatures(new ArrayList<>());

        if (cond.getSidoName() != null && !cond.getSidoName().isBlank()) {
            Long sidoNo = aiSnsIdMapper.findSidoNoByName(cond.getSidoName());
            cond.setSidoNo(sidoNo);
        }

        if (cond.getSidoNo() != null && cond.getGunguName() != null && !cond.getGunguName().isBlank()) {
            Long gunguCode = aiSnsIdMapper.findGunguCodeByName(cond.getSidoNo(), cond.getGunguName());
            cond.setGunguCode(gunguCode);
        }

        List<Long> boardIds = aiSnsIdMapper.findTop5BoardIdsByCond(cond);

        if (boardIds == null || boardIds.isEmpty())
            return List.of();

        List<SnsBoardResp> boards = aiSnsIdMapper.selectBoardsByIds(boardIds);

        for (SnsBoardResp board : boards) {
            board.setImages(aiSnsIdMapper.selectImagesByBoardId(board.getBoardId()));
        }

        injectAiReviews(query, boards);

        return boards;
    }

    private void injectAiReviews(String query, List<SnsBoardResp> boards) {
        try {
            StringBuilder sb = new StringBuilder();
            for (SnsBoardResp b : boards) {
                String c = b.getContent() == null ? "" : b.getContent().replaceAll("[\\r\\n]+", " ");
                if (c.length() > 80)
                    c = c.substring(0, 80) + "...";
                sb.append(String.format("- ID:%d | %s\n", b.getBoardId(), c));
            }

            String prompt = String.format(REVIEW_PROMPT_TEMPLATE, query, sb.toString());

            Map<String, Object> body = Map.of(
                    "model", "gpt-4.1",
                    "temperature", 0.7,
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)));

            JsonNode json = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + gmsKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (json == null)
                return;

            String content = json.get("choices").get(0).get("message").get("content").asText();
            if (content.contains("```")) {
                content = content.replaceAll("```json", "").replaceAll("```", "").trim();
            }

            JsonNode arr = om.readTree(content);
            if (arr.isArray()) {
                for (JsonNode node : arr) {
                    Long id = node.get("boardId").asLong();
                    String review = node.get("review").asText();

                    boards.stream()
                            .filter(b -> b.getBoardId().equals(id))
                            .findFirst()
                            .ifPresent(b -> b.setOneLineReview(review));
                }
            }
        } catch (Exception e) {
            System.err.println("AI Review Generation Failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure no nulls
            for (SnsBoardResp b : boards) {
                if (b.getOneLineReview() == null || b.getOneLineReview().isBlank()) {
                    b.setOneLineReview("AI가 추천하는 최고의 맛집입니다! 👍");
                }
            }
        }
    }

    private AiExtractResult extractByAi(String userQuery) {
        Map<String, Object> body = Map.of(
                "model", "gpt-4.1",
                "temperature", 0,
                "messages", List.of(
                        Map.of("role", "developer", "content", DEV_PROMPT),
                        Map.of("role", "user", "content", userQuery)));

        try {
            JsonNode json = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + gmsKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            String content = json.get("choices").get(0).get("message").get("content").asText();
            return om.readValue(content, AiExtractResult.class);
        } catch (Exception e) {
            System.out.println(e);
            return new AiExtractResult(null, null, null,
                    List.of(), List.of(), List.of(), List.of(),
                    null, null);
        }
    }

}
