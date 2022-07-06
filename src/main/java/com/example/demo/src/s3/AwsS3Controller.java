package com.example.demo.src.s3;

import com.example.demo.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class AwsS3Controller {
    private final AwsS3Service awsS3Service;

    /**
     * Amazon S3에 이미지 업로드
     * @return 성공 시 200 Success와 함께 업로드 된 파일의 파일명 리스트 반환
     */
    @ResponseBody
    @PostMapping("/image")
    public BaseResponse<List<String>> uploadImage(@RequestParam("images") @RequestPart List<MultipartFile> multipartFile) {
        return new BaseResponse<>(awsS3Service.uploadImage(multipartFile));
    }

    /**
     * Amazon S3에 이미지 업로드 된 파일을 삭제
     * 수정 필요
     * @return 성공 시 200 Success
     */
    @DeleteMapping("/image")
    public BaseResponse<String> deleteImage(@RequestParam String fileName) {
        awsS3Service.deleteImage(fileName);
        return new BaseResponse<>("삭제 성공");
    }
}
