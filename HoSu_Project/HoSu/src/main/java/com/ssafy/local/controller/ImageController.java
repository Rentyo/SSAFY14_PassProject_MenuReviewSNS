package com.ssafy.local.controller;

import com.ssafy.local.Service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Image", description = "이미지 업로드 API")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    @Operation(summary = "단일 이미지 업로드", description = "이미지를 S3에 업로드하고 URL을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 크기 초과", content = @Content)
    })
    public ResponseEntity<String> uploadImage(
            @Parameter(description = "업로드할 이미지 파일 (5MB 이하, 이미지 형식만 가능)", required = true) @RequestParam("file") MultipartFile file) {
        String imageUrl = s3Service.uploadImage(file);
        return ResponseEntity.ok(imageUrl);
    }

    @PostMapping("/upload/multiple")
    @Operation(summary = "다중 이미지 업로드", description = "최대 5개의 이미지를 S3에 업로드하고 URL 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "파일 개수 초과 또는 잘못된 파일", content = @Content)
    })
    public ResponseEntity<List<String>> uploadImages(
            @Parameter(description = "업로드할 이미지 파일 목록 (최대 5개)", required = true) @RequestParam("files") List<MultipartFile> files) {
        List<String> imageUrls = s3Service.uploadImages(files);
        return ResponseEntity.ok(imageUrls);
    }
}
