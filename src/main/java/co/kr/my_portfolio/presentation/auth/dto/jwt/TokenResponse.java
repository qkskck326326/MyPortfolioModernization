package co.kr.my_portfolio.presentation.auth.dto.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(name = "TokenResponse - access, refresh token 셋", description = """
        로그인 요청으로 반환된 토큰 셋.
        Access Token 과 Response Token 으로 이루어져 있습니다.
        로그인 성공시 반환됩니다.
        """)
@Getter
@AllArgsConstructor(staticName = "of")
public class TokenResponse {
    @Schema(description = "엑세스 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI2Iiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3NDkzNTQ0MjgsImV4cCI6MTc0OTM1ODAyOH0.YZlyb-6ZQDqIRbzdX4UhWEANNS_JClFB-D9_c954NeKBkNjmIzKJ0dcXberusuM1CeYfryMYwt3pATy5iRT6PA")
    private String accessToken;
    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI2Iiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3NDkzNTQ0MjgsImV4cCI6MTc1MDU2NDAyOH0.-pH9cnG0INkMUzM1jXp3D8Dhp-54rLkBuf4dMX3a2smdK5k1n_rq3n5IWEd5eVGB-4ESVbMIIXe4_cZiYfbhiQ")
    private String refreshToken;
}
