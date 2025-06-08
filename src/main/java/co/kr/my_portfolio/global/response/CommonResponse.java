package co.kr.my_portfolio.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class CommonResponse<T> {

    private final boolean success;
    private final T data;
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
