package com.sajal.shortnerservice.repository;

import com.sajal.shortnerservice.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findFirstByUsedFalse();

    @Modifying
    @Query("update ShortUrl s set s.used = ?1 where s.shortUrl = ?2 and s.used = false")
    int updateUsedByShortUrl(boolean used, String shortUrl);

}
