package co.kr.my_portfolio.global.exception;

import co.kr.my_portfolio.global.exception.custom.InvalidCredentialsException;
import co.kr.my_portfolio.global.exception.custom.UnauthorizedException;
import co.kr.my_portfolio.global.exception.custom.UserNotFoundException;
import co.kr.my_portfolio.global.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation 에러 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<String>> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = bindingResult.getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        return ResponseEntity.badRequest().body(CommonResponse.fail(errorMessage));
    }
    
    // 아이디 비밀번호 불일치 예외 처리
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<CommonResponse<String>> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("[예외 발생 - 로그인 실패] {}", ex.getMessage());
        return ResponseEntity.ok(CommonResponse.fail("아이디 또는 비밀번호가 일치하지 않습니다."));
    }

    // 비인증 에러
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CommonResponse<?>> handleUserNotFound(UnauthorizedException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.fail(ex.getMessage()));
    }
    
    // 조회되지 않는 사용자 에러처리
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.fail(ex.getMessage()));
    }
    
    // 인증되지 않은 사용자 에러처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponse.fail(ex.getMessage()));
    }

    // 일반적인 예외 처리 ///////////////////
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(CommonResponse.fail(ex.getMessage()));
    }
}
