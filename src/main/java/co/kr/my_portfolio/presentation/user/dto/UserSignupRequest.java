package co.kr.my_portfolio.presentation.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserSignupRequest{

    @Email(message = "유효한 이메일 형식을 입력해주세요.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
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