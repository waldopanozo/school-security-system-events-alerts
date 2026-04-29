package io.waldopo.schoolsecurity.outbox;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String eventVersion;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false, length = 4000)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant publishedAt;

    protected OutboxEvent() {}

    public OutboxEvent(String eventType, String eventVersion, UUID aggregateId, String payload) {
        this.eventType = eventType;
        this.eventVersion = eventVersion;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.attempts = 0;
        this.createdAt = Instant.now();
    }

    public void markPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = Instant.now();
    }

    public void markFailedAttempt() {
        this.status = OutboxStatus.FAILED;
        this.attempts += 1;
    }

    public UUID getId() { return id; }
    public String getEventType() { return eventType; }
    public String getEventVersion() { return eventVersion; }
    public UUID getAggregateId() { return aggregateId; }
    public String getPayload() { return payload; }
    public OutboxStatus getStatus() { return status; }
    public int getAttempts() { return attempts; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getPublishedAt() { return publishedAt; }
}
