package io.waldopo.schoolsecurity.notifications;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "event_notification")
public class NotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private String channel;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String templateName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected NotificationLog() {}

    public NotificationLog(UUID eventId, String channel, String recipient, String templateName, String status) {
        this.eventId = eventId;
        this.channel = channel;
        this.recipient = recipient;
        this.templateName = templateName;
        this.status = status;
    }
}
