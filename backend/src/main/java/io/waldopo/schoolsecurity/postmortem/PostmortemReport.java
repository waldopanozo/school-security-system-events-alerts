package io.waldopo.schoolsecurity.postmortem;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "postmortem_report")
public class PostmortemReport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID eventId;

    @Column(nullable = false, length = 2000)
    private String summary;

    @Column(nullable = false, length = 2000)
    private String rootCause;

    @Column(nullable = false, length = 2000)
    private String correctiveActions;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected PostmortemReport() {}

    public PostmortemReport(UUID eventId, String summary, String rootCause, String correctiveActions) {
        this.eventId = eventId;
        this.summary = summary;
        this.rootCause = rootCause;
        this.correctiveActions = correctiveActions;
    }
}
