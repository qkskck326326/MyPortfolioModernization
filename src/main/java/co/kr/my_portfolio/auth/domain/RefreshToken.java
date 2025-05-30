package co.kr.my_portfolio.auth.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private Long userId;

    private String token;

    private Long expiration;

    // 저장된 토큰과 클라이언트가 보낸 토큰이 같은지 확인
    public boolean isSameToken(String inputToken) {
        return this.token.equals(inputToken);
    }

    //토큰 만료까지 남은 시간이 임계값 이하면 재발급 대상
    public boolean needsReissue(long thresholdMillis) {
        long now = System.currentTimeMillis();
        return (this.expiration - now) <= thresholdMillis;
    }

    // 새로운 토큰으로 갱신
    public void updateToken(String newToken, Long newExpiration) {
        this.token = newToken;
        this.expiration = newExpiration;
    }

}
