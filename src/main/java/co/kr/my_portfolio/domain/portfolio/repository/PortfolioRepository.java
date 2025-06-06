package co.kr.my_portfolio.domain.portfolio.repository;

import co.kr.my_portfolio.domain.portfolio.Portfolio;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    @Query("""
        SELECT p FROM Portfolio p
        LEFT JOIN FETCH p.tags m
        LEFT JOIN FETCH m.tag
        WHERE p.id = :id
    """)
    Portfolio findWithTagsById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Portfolio p where p.id = :id")
    Optional<Portfolio> findByIdForUpdate(@Param("id") Long id);
}
