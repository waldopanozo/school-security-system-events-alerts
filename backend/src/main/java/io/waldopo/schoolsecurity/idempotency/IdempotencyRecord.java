package io.waldopo.schoolsecurity.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "idempotency_record")
public class IdempotencyRecord {
    @Id
    private String idempotencyKey;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected IdempotencyRecord() {}

    public IdempotencyRecord(String idempotencyKey, String operation) {
        this.idempotencyKey = idempotencyKey;
        this.operation = operation;
    }
}
