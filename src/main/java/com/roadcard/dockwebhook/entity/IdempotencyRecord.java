package com.roadcard.dockwebhook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "idempotency_record", uniqueConstraints = {@UniqueConstraint(columnNames = {"event_key"})})
@Getter
@Setter
@NoArgsConstructor
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_key", nullable = false, unique = true)
    private String key;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    public IdempotencyRecord(String key, Long createdAt) {
        this.key = key;
        this.createdAt = createdAt;
    }
}
