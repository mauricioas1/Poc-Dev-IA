package com.roadcard.dockwebhook.repository;

import com.roadcard.dockwebhook.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long> {
    Optional<IdempotencyRecord> findByKey(String key);
    boolean existsByKey(String key);
}
