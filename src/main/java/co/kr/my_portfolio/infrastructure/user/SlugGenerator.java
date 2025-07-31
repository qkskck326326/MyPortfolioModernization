package co.kr.my_portfolio.infrastructure.user;

import co.kr.my_portfolio.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
@RequiredArgsConstructor
public class SlugGenerator {

    private final UserRepository userRepository;
    private final KoreanSlugGenerator koreanSlugGenerator;

    public String generate(String rawInput) {
        String baseSlug = koreanSlugGenerator.convert(rawInput);
        String candidate = baseSlug;
        int i = 1;

        while (userRepository.existsBySlug(candidate)) {
            candidate = baseSlug + "-" + i++;
        }

        return candidate;
    }
}
