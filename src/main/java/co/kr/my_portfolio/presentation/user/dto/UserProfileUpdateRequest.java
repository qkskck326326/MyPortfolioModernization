package co.kr.my_portfolio.presentation.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserProfileUpdateRequest {

    @Size(max = 30, message = "닉네임은 최대 30자까지 가능합니다.")
    private String nickname;

    @Size(max = 500, message = "썸네일 URL은 최대 500자까지 가능합니다.")
    private String userThumbnail;

    @Size(max = 2000, message = "소개글은 최대 2000자까지 가능합니다.")
    private String introduce;

    @Size(max = 100, message = "GitHub 주소는 최대 100자까지 가능합니다.")
    private String github;

    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birth;
}
