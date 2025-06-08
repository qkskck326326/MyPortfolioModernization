package co.kr.my_portfolio.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(name = "CommonResponse - API 공통 응답 모델", description = """
        API 공통 응답입니다.
        """)
@Getter
@AllArgsConstructor(staticName = "of")
public class CommonResponse<T> {
    @Schema(description = "해당 요청의 성공 or 실패 여부", example = "true")
    private final boolean success;
    @Schema(description = "해당 요청 결과의 데이터", example = "true")
    private final T data;
    @Schema(description = "해당 요청 결과의 메세지", example = "true")
    private final String message;

    // 성공 시 (데이터만)
    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.of(true, data, null);
    }

    // 성공 시 (데이터 + 메시지)
    public static <T> CommonResponse<T> success(T data, String message) {
        return CommonResponse.of(true, data, message);
    }

    // 실패 시 (한글 메시지)
    public static <T> CommonResponse<T> fail(String message) {
        return CommonResponse.of(false, null, message);
    }
}
