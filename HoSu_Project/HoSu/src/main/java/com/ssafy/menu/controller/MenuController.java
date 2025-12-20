package com.ssafy.menu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.menu.dto.InsertMenuReq;
import com.ssafy.menu.dto.InsertTagReq;
import com.ssafy.menu.dto.Menu;
import com.ssafy.menu.dto.UpdateMenuReq;
import com.ssafy.menu.dto.UpdateTagReq;
import com.ssafy.menu.service.MenuService;
import com.ssafy.restaurant.dto.Restaurant;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/menus")
@Tag(name = "Menu", description = "메뉴 API(관리자 기능 같은 경우 권한이 필요하기 때문에 swagger에서 실행이 되질 않습니다.)")
public class MenuController {
	
	private final MenuService menuService;
	
	public MenuController(@Qualifier("MenuServiceImpl") MenuService menuService) {
		this.menuService= menuService;
	}
	
	
	/** 
     * 식당 사장님 기능 : Menu 추가
     * POST
     */
	@PostMapping
	@Operation(
	    summary = "관리자 기능: 메뉴 생성",
	    description = "관리자 권한으로 식당에 메뉴를 추가합니다.",
	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
	        description = "메뉴 생성 요청 데이터",
	        required = true
	    )
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "메뉴 생성 성공"),
	    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<String> insertMenu(@RequestBody InsertMenuReq menuReq) throws Exception {
		System.out.println(menuReq);
		menuService.insertMenu(menuReq);
        return ResponseEntity.ok("성공");
    }
	
	
	/** 
     * 식당 사장님 기능 : Menu 수정
     * PUT
     */
	@PutMapping
	@Operation(
	    summary = "관리자 기능: 메뉴 수정",
	    description = "관리자 권한으로 기존 메뉴 정보를 수정합니다.",
	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
	        description = "메뉴 수정 요청 데이터",
	        required = true
	    )
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "메뉴 수정 성공"),
	    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "404", description = "해당 메뉴를 찾을 수 없음"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
    public ResponseEntity<String> updateMenu(
            @RequestBody UpdateMenuReq updateMenu) throws Exception {  
		menuService.updateMenu(updateMenu);
        return ResponseEntity.ok("성공");
    }
	
	/** 
     * 관리자 기능 : Menu 삭제
     * DELETE
     */
	@DeleteMapping("/{id}")
	@Operation(
	    summary = "관리자 기능: 메뉴 삭제",
	    description = "관리자 권한으로 메뉴를 삭제합니다.",
	    parameters = {
	        @Parameter(name = "id", description = "삭제할 메뉴의 ID", required = true, example = "12")
	    }
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "메뉴 삭제 성공"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "404", description = "삭제할 메뉴를 찾을 수 없음"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
    public ResponseEntity<String> deleteMenu(
            @PathVariable("id") Long id) throws Exception {  
		menuService.deleteMenu(id);
        return ResponseEntity.ok("성공");
    }
	
	
	/** 
     * 관리자 기능 : Tag 추가
     * POST
     */
	@PostMapping("/tag")
	@Operation(
	    summary = "관리자 기능: 태그 생성",
	    description = "관리자 권한으로 새로운 메뉴 태그를 생성합니다."
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "태그 생성 성공"),
	    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<String> insertTag(@RequestBody InsertTagReq tagReq) throws Exception {
		menuService.insertTag(tagReq);
        return ResponseEntity.ok("성공");
    }
	
	
	/** 
     * 관리자 기능 : Tag 수정
     * PUT
     */
	@PutMapping("/tag")
	@Operation(
	    summary = "관리자 기능: 태그 수정",
	    description = "관리자 권한으로 기존 태그 정보를 수정합니다."
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "태그 수정 성공"),
	    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "404", description = "해당 태그를 찾을 수 없음"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
    public ResponseEntity<String> updateTag(
            @RequestBody UpdateTagReq tagReq) throws Exception {  
		menuService.updateTag(tagReq);
        return ResponseEntity.ok("성공");
    }
	
	/** 
     * 관리자 기능 : Tag 삭제
     * DELETE
     */
	@DeleteMapping("/tag/{id}")
	@Operation(
	    summary = "관리자 기능: 태그 삭제",
	    description = "관리자 권한으로 태그를 삭제합니다.",
	    parameters = {
	        @Parameter(name = "id", description = "삭제할 태그 ID", required = true, example = "5")
	    }
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "태그 삭제 성공"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "404", description = "삭제할 태그를 찾을 수 없음"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
    public ResponseEntity<String> deleteTag(
            @PathVariable("id") Long id) throws Exception {  
		menuService.deleteTag(id);
        return ResponseEntity.ok("성공");
    }
	
	
	
	
	
	/** 
	 * 식당 메뉴 조회
	 * GET /restaurantId(Long)
     */
	@Operation(
	    summary = "식당 메뉴 조회",
	    description = "특정 식당의 식당 ID를 기준으로 해당 식당의 메뉴 리스트를 조회합니다."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "메뉴 리스트 조회 성공",
	        content = @Content(
	            mediaType = "application/json",
	            array = @ArraySchema(schema = @Schema(implementation = Menu.class))
	        )
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "해당 식당을 찾을 수 없음",
	        content = @Content
	    ),
	    @ApiResponse(
	        responseCode = "500",
	        description = "서버 내부 오류",
	        content = @Content
	    )
	})
	@GetMapping("/{restaurantId}")
	public ResponseEntity<List<Menu>> getMenus(
			@PathVariable("restaurantId") Long id) throws Exception{
		System.out.println(id);
		List<Menu> list = menuService.getMenus(id);
		return ResponseEntity.ok(list);
	}
	
	
	
	/** 
     * 식당 사장님 기능 : Menu 활성/비활성화
     * PUT
     */
	@PutMapping("deactive/{menuId}/{active}")
	@Operation(
	    summary = "식당 사장님 기능: 메뉴 활성/비활성화",
	    description = "사장님 권한으로 기존 메뉴 중 하나를 활성/비활성 합니다.",
	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
	        description = "메뉴 수정 요청 데이터",
	        required = true
	    )
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "메뉴 수정 성공"),
	    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "404", description = "해당 메뉴를 찾을 수 없음"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
    public ResponseEntity<String> activeMenuChange(@PathVariable("menuId") Long menuId, @PathVariable("active") int active ) throws Exception {  
		menuService.activeMenuChange(menuId, active == 1 ? 0 : 1);
        return ResponseEntity.ok("성공");
    }
	
	
}
