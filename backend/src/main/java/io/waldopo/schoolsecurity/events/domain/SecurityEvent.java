package io.waldopo.schoolsecurity.events.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "security_event")
public class SecurityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false)
    private String incidentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeverityLevel severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected SecurityEvent() {}

    public SecurityEvent(EventType type, String incidentType, SeverityLevel severity, String location, String createdBy) {
        this.type = type;
        this.incidentType = incidentType;
        this.severity = severity;
        this.status = EventStatus.CREATED;
        this.location = location;
        this.createdBy = createdBy;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void activateAlert() {
        this.status = EventStatus.ALERT_ACTIVATED;
        this.updatedAt = Instant.now();
    }

    public void close() {
        this.status = EventStatus.CLOSED;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public EventType getType() { return type; }
    public String getIncidentType() { return incidentType; }
    public SeverityLevel getSeverity() { return severity; }
    public EventStatus getStatus() { return status; }
    public String getLocation() { return location; }
    public String getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
