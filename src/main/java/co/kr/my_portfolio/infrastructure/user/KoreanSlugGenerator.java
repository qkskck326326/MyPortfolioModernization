package co.kr.my_portfolio.infrastructure.user;

import org.springframework.stereotype.Component;

@Component
public class KoreanSlugGenerator {

    // 초성, 중성, 종성 분해를 위한 유니코드 기준
    private static final String[] CHO = { "g", "kk", "n", "d", "tt", "r", "m", "b", "pp", "s", "ss", "", "j", "jj", "ch", "k", "t", "p", "h" };
    private static final String[] JUNG = { "a", "ae", "ya", "yae", "eo", "e", "yeo", "ye", "o", "wa", "wae", "oe", "yo", "u", "wo", "we", "wi", "yu", "eu", "ui", "i" };
    private static final String[] JONG = { "", "g", "gg", "gs", "n", "nj", "nh", "d", "r", "rg", "rm", "rb", "rs", "rt", "rp", "rh", "m", "b", "bs", "s", "ss", "ng", "j", "ch", "k", "t", "p", "h" };

    public String convert(String korean) {
        StringBuilder sb = new StringBuilder();

        for (char c : korean.toCharArray()) {
            if (c >= 0xAC00 && c <= 0xD7A3) {
                int base = c - 0xAC00;
                int cho = base / (21 * 28);
                int jung = (base % (21 * 28)) / 28;
                int jong = base % 28;

                sb.append(CHO[cho]).append(JUNG[jung]);
                if (jong != 0) sb.append(JONG[jong]);
            } else if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            } else {
                sb.append("-"); // 기타 특수문자는 하이픈으로 대체
            }
        }

        return sb.toString().replaceAll("-{2,}", "-").replaceAll("(^-|-$)", "").toLowerCase();
    }
}