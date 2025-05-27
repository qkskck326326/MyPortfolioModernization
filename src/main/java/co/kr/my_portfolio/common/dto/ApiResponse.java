package co.kr.my_portfolio.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;

    // 성공 시 (데이터만)
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.of(true, data, null);
    }

    // 성공 시 (데이터 + 메시지)
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.of(true, data, message);
    }

    // 실패 시 (한글 메시지)
    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.of(false, null, message);
    }
}
