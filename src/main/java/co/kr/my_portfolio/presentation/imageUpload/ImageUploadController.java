package co.kr.my_portfolio.presentation.imageUpload;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.kr.my_portfolio.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Set;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Validated
public class ImageUploadController {

    @Value("${imgbb.api.key}")
    private String imgbbApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png", "image/jpeg", "image/webp", "image/gif"
    );

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<ImageUploadResponse>> uploadImage(
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        // 1) 입력 검증
        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("파일이 비어 있습니다."));
        }
        if (!ALLOWED_TYPES.contains(image.getContentType())) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse.fail("허용되지 않는 이미지 형식입니다."));
        }
        // (옵션) 크기 제한
        final long MAX_BYTES = 10L * 1024 * 1024; // 10MB
        if (image.getSize() > MAX_BYTES) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(CommonResponse.fail("허용 용량(10MB)을 초과했습니다."));
        }

        // 2) imgbb 호출
        String url = "https://api.imgbb.com/1/upload?key=" + imgbbApiKey;

        String base64 = Base64.getEncoder().encodeToString(image.getBytes());

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("image", base64);
        // 필요 시 form.add("name", "optional-file-name");
        // 필요 시 form.add("expiration", "600"); // 초 단위

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<String> resp = restTemplate.postForEntity(url, new HttpEntity<>(form, headers), String.class);

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(CommonResponse.fail("이미지 업로드 실패(게이트웨이)."));
            }

            JsonNode root = objectMapper.readTree(resp.getBody());
            boolean success = root.path("success").asBoolean(false);
            if (!success) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(CommonResponse.fail("이미지 업로드 실패(외부 API)."));
            }

            String imageUrl = root.path("data").path("url").asText(null);
            String deleteUrl = root.path("data").path("delete_url").asText(null);

            if (imageUrl == null || imageUrl.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(CommonResponse.fail("이미지 URL 추출 실패."));
            }

            ImageUploadResponse data = new ImageUploadResponse(imageUrl, deleteUrl);
            return ResponseEntity.ok(CommonResponse.success(data));

        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(CommonResponse.fail("이미지 업로드 중 예외 발생."));
        }
    }

    public record ImageUploadResponse(String url, String deleteUrl) {}
}