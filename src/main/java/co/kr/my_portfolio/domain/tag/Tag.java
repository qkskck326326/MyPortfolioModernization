package co.kr.my_portfolio.domain.tag;

import co.kr.my_portfolio.domain.portfolio.Portfolio;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Tag(String name) {
        this.name = name.toLowerCase().trim();
    }

    public static Tag of(String name) {
        return new Tag(name);
    }
}
