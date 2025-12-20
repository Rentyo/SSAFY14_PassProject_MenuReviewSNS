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
    private static final String DEV_PROMPT = """
	너는 '식당 SNS 리뷰 검색'을 위한 조건 추출기다.
	반드시 아래 JSON 스키마로만 출력해라(설명/문장 금지).
	
	{
	  "sidoName": string|null,
	  "gunguName": string|null,
	  "restaurantName": string|null,
	  "categories": string[],
	  "menuNames": string[],
	  "tagNames": string[],
	  "features": string[]
	}
	
	categories는 아래 ENUM만 허용:
	KOREAN, CHINESE, JAPANESE, WESTERN, SNACK, CHICKEN, MEAT, SEAFOOD, CAFE, PUB, BAKERY, FASTFOOD, ASIAN, CURRY
	
	sidoName과 gunguName은 되도록 짧게(예: "대전", "서구", "서울") 출력해도 된다.
	값이 없으면 null 또는 빈 배열로 둬라.
	유효한 JSON만 출력해라.
	""";

    public List<SnsBoardResp> getTop5BoardIds(String query) throws Exception {
        AiExtractResult cond = extractByAi(query);

        // null-safe
        if (cond.getCategories() == null) cond.setCategories(new ArrayList<>());
        if (cond.getMenuNames() == null) cond.setMenuNames(new ArrayList<>());
        if (cond.getTagNames() == null) cond.setTagNames(new ArrayList<>());
        if (cond.getFeatures() == null) cond.setFeatures(new ArrayList<>());

        // ✅ 1) sidoName -> sidoNo 변환
        if (cond.getSidoName() != null && !cond.getSidoName().isBlank()) {
            Long sidoNo = aiSnsIdMapper.findSidoNoByName(cond.getSidoName());
            cond.setSidoNo(sidoNo);
        }

        // ✅ 2) gunguName -> gunguCode 변환 (sidoNo가 있을 때만 정확)
        if (cond.getSidoNo() != null && cond.getGunguName() != null && !cond.getGunguName().isBlank()) {
            Long gunguCode = aiSnsIdMapper.findGunguCodeByName(cond.getSidoNo(), cond.getGunguName());
            cond.setGunguCode(gunguCode);
        }
        

        // ✅ 3) 스코어링 쿼리로 상위 5개 board_id
        List<Long> boardIds = aiSnsIdMapper.findTop5BoardIdsByCond(cond);
        
        if (boardIds == null || boardIds.isEmpty()) return List.of();

        // 1) boardIds에 해당하는 게시물들 가져오기 (순서 유지 권장)
        List<SnsBoardResp> boards = aiSnsIdMapper.selectBoardsByIds(boardIds);

        // 2) 각 게시물에 images 붙이기 (기존 방식 그대로)
        for (SnsBoardResp board : boards) {
            board.setImages(aiSnsIdMapper.selectImagesByBoardId(board.getBoardId()));
        }

        return boards;
    }

    private AiExtractResult extractByAi(String userQuery) {
        Map<String, Object> body = Map.of(
                "model", "gpt-4.1-nano",
                "temperature", 0,
                "messages", List.of(
                        Map.of("role", "developer", "content", DEV_PROMPT),
                        Map.of("role", "user", "content", userQuery)
                )
        );

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
            return new AiExtractResult(null, null, null,
                    List.of(), List.of(), List.of(), List.of(),
                    null, null);
        }
    }
    
   
	
	
}
