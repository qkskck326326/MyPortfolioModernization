package co.kr.my_portfolio.test;

import co.kr.my_portfolio.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/test/admin")
    public ResponseEntity<CommonResponse<String>> tsetAdminApi(){
        return ResponseEntity.ok(CommonResponse.success("데이터", "성공"));
    }
}
