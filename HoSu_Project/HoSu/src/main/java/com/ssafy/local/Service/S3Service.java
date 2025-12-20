package com.ssafy.local.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.bucket-name}")
    private String bucketName;

    /**
     * 단일 이미지 업로드
     */
    public String uploadImage(MultipartFile file) {
        validateImageFile(file);

        String fileName = createFileName(file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata));
        } catch (IOException e) {
            log.error("이미지 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 업로드에 실패했습니다.");
        }

        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    /**
     * 다중 이미지 업로드 (최대 5장)
     */
    public List<String> uploadImages(List<MultipartFile> files) {
        if (files.size() > 5) {
            throw new IllegalArgumentException("이미지는 최대 5장까지 업로드 가능합니다.");
        }

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String imageUrl = uploadImage(file);
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }

    /**
     * 이미지 삭제
     */
    public void deleteImage(String imageUrl) {
        String fileName = extractFileNameFromUrl(imageUrl);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
            log.info("이미지 삭제 성공: {}", fileName);
        } catch (Exception e) {
            log.error("이미지 삭제 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 삭제에 실패했습니다.");
        }
    }

    /**
     * 이미지 파일 검증
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 파일 크기 제한 (5MB)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다.");
        }

        // 이미지 파일 형식 검증
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
    }

    /**
     * 고유한 파일명 생성
     */
    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }

    /**
     * URL에서 파일명 추출
     */
    private String extractFileNameFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}
