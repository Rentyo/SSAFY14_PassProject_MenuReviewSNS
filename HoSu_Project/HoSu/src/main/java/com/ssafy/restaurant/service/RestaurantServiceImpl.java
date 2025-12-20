package com.ssafy.restaurant.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.restaurant.dto.CheckRestaurantResp;
import com.ssafy.restaurant.dto.Notice;
import com.ssafy.restaurant.dto.Restaurant;
import com.ssafy.restaurant.dto.RestaurantDetailById;
import com.ssafy.restaurant.dto.RestaurantOpenRequest;
import com.ssafy.restaurant.dto.UpdateNoticeReq;
import com.ssafy.restaurant.dto.insertNoticeReq;
import com.ssafy.restaurant.mapper.RestaurantMapper;
import com.ssafy.search.dto.Gungu;
import com.ssafy.search.dto.Sido;
import com.ssafy.search.service.SearchService;
import com.ssafy.sns.dto.SnsBoardInRestaurant;

@Service("RestaurantServiceImpl")
public class RestaurantServiceImpl implements RestaurantService {
	@Autowired
	private RestaurantMapper rMapper;
	
	private final SearchService service;
	
	private final List<CheckRestaurantResp> restaurants = new ArrayList<>();
	private final WebClient webClient;
    private final String kakaoBaseUrl;
    private final String apiKey;

    public RestaurantServiceImpl(
            @Value("${kakao.api.url}") String kakaoBaseUrl,
            @Value("${kakao.api.key}") String apiKey,
            @Qualifier("searchServiceImpl") SearchService service) {
    	this.service = service;
        this.kakaoBaseUrl = kakaoBaseUrl;
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(kakaoBaseUrl)
                .defaultHeader("Authorization", apiKey)
                .build();	
    }

    public List<CheckRestaurantResp> search(String keyword) {
    	if (keyword == null || keyword.isBlank()) {
            return restaurants;
        }
        
        try {
            String response = webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("query", keyword).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();	
            
            // JSON 파싱을 위한 ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            JsonNode documents = root.path("documents");
            
            List<CheckRestaurantResp> results = new ArrayList<>();
            
            for (JsonNode doc : documents) {
                String addressName = doc.path("address_name").asText();
                String roadAddressName = doc.path("road_address_name").asText();
                double lat = doc.path("y").asDouble();
                double lng = doc.path("x").asDouble();
                String placeName = doc.path("place_name").asText();
                String phone = doc.path("phone").asText();
                
                // road_address_name에서 시도와 군구 추출
                String[] addressParts = roadAddressName.split(" ");
                
                String sidoNo = null;
                String sidoName = null;
                String gunguCode = null;
                String gunguName = null;
                
                if (addressParts.length >= 2) {
                    // 첫 번째 단어로 시도 정보 조회
                    Sido sido = service.getSidoToName(addressParts[0]);
                    if (sido != null) {
                        sidoNo = sido.getSidoNo(); // Sido 객체의 필드명에 맞게 수정 필요
                        sidoName = sido.getSidoName();
                    }
                    
                    // 두 번째 단어로 군구 정보 조회
                    Gungu gungu = service.getGunguToName(sido.getSidoNo(),addressParts[1]);
                    if (gungu != null) {
                        gunguCode = gungu.getGunguCode(); // Gungu 객체의 필드명에 맞게 수정 필요
                        gunguName = gungu.getGunguName();
                    }
                }
                
                CheckRestaurantResp resp = new CheckRestaurantResp(
                    addressName,
                    sidoNo,
                    sidoName,
                    gunguCode,
                    gunguName,
                    phone,
                    lat,
                    lng,
                    placeName
                );
                
                results.add(resp);
            }
            
            return results;
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

	@Override
	@Transactional
	public void insertRestaurant(Restaurant rest) throws Exception {
		// 식당 등록
		rMapper.insertRestaurant(rest);
		// 식당 등록했다면 사람의 닉네임을 한번 바꿔보자
		rMapper.updateNickName(rest.getUserNo(), rest.getName());
		// 식당 등록됐다면 식당 이미지를 사람의 프로필 사진으로 바꾸기
		rMapper.updateImage(rest.getUserNo());
		
	}

	@Override
	public void updateRestaurant(Restaurant rest) throws Exception {
		rMapper.updateRestaurant(rest);
	}

	@Override
	public void deleteRestaurant(Restaurant rest) throws Exception {
		rMapper.deleteRestaurant(rest);
	}

	@Override
	public void insertOpenRequest(RestaurantOpenRequest req) throws Exception {
		rMapper.insertOpenRequest(req);	
	}
	@Override
	public RestaurantDetailById selectRestaurantById(long restaurantId) throws Exception {
		Restaurant restaurant = rMapper.selectRestaurantById(restaurantId);
		List<SnsBoardInRestaurant> list = rMapper.selectSnsBoardInRestaurantsById(restaurantId);	
		return new RestaurantDetailById(restaurant.getId(),restaurant.getName(),restaurant.getAddress(),
				restaurant.getCategory(),restaurant.getTotalRating(), restaurant.getLat(),
				restaurant.getLng(), restaurant.getPhone(), restaurant.getMainImage(),
				restaurant.getTotalContents(), restaurant.getViewCount(),restaurant.getReviewCounts(), list, restaurant.getUserNo());
	}

	@Override
	public Long selectRestaurantIdByUserNo(Long userNo) throws Exception {
		return rMapper.selectRestaurantIdByUserNo(userNo);
	}

	@Override
	public List<Notice> selectNoticesIdByRestaurantId(Long restaurantId) throws Exception {
		return rMapper.selectNoticesIdByRestaurantId(restaurantId);
	}

	@Override
	public void insertNotice(insertNoticeReq req) throws Exception {
		rMapper.insertNotice(req);
		
	}

	@Override
	public void updateNotice(UpdateNoticeReq req) throws Exception {
		rMapper.updateNotice(req);
		
	}

	@Override
	public void deleteNotice(long restaurantNoticeId) throws Exception {
		rMapper.deleteNotice(restaurantNoticeId);
		
	}

	@Override
	public void viewCountUp(Long restaurantId) throws Exception {
		rMapper.viewCountUp(restaurantId);
	}

	@Override
	public int getCeoLevel(Long userNo) throws Exception {
		if(rMapper.existRestaurantOwn(userNo) > 0) {
			return 1;
		}else if(rMapper.existRestaurantRequest(userNo) > 0) {
			return -1;
		}else {
			return 0;
		}
	}
}
