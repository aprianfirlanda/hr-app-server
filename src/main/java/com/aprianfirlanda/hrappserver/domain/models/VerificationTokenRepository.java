package com.aprianfirlanda.hrappserver.domain.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByIdAndExpireDateAfter(UUID id, Instant expireDate);

}