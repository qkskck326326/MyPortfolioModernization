package co.kr.my_portfolio.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    boolean existsBySlug(String candidate);

    boolean existsByNickname(String nickname);

    User findBySlug(String slug);
}