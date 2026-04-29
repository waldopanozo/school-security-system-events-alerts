package io.waldopo.schoolsecurity.events;

import io.waldopo.schoolsecurity.events.api.CreateEventRequest;
import io.waldopo.schoolsecurity.events.domain.EventType;
import io.waldopo.schoolsecurity.events.domain.SeverityLevel;
import io.waldopo.schoolsecurity.events.service.SecurityEventService;
import io.waldopo.schoolsecurity.outbox.OutboxRelay;
import io.waldopo.schoolsecurity.outbox.OutboxRepository;
import io.waldopo.schoolsecurity.outbox.OutboxStatus;
import io.waldopo.schoolsecurity.postmortem.PostmortemRequest;
import io.waldopo.schoolsecurity.postmortem.PostmortemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class SecurityEventServiceTest {
    @Autowired
    private SecurityEventService service;
    @Autowired
    private PostmortemService postmortemService;
    @Autowired
    private OutboxRepository outboxRepository;
    @Autowired
    private OutboxRelay outboxRelay;
    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void closeShouldFailWithoutPostmortem() {
        var event = service.create(new CreateEventRequest(EventType.INCIDENT, "FIGHT", SeverityLevel.HIGH, "Courtyard"), "principal");
        assertThrows(IllegalStateException.class, () -> service.close(event.getId()));
    }

    @Test
    void closeShouldWorkWithPostmortem() {
        var event = service.create(new CreateEventRequest(EventType.DRILL, "EVACUATION", SeverityLevel.MEDIUM, "Block A"), "principal");
        postmortemService.create(new PostmortemRequest(event.getId(), "ok", "simulation", "improvement"));
        var closed = service.close(event.getId());
        assertEquals("CLOSED", closed.getStatus().name());
    }

    @Test
    void createShouldWritePendingOutboxEvent() {
        service.create(new CreateEventRequest(EventType.INCIDENT, "FIGHT", SeverityLevel.HIGH, "Gym"), "principal");
        assertTrue(outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING).size() >= 1);
    }

    @Test
    void relayShouldPublishAndMarkOutboxAsPublished() {
        service.create(new CreateEventRequest(EventType.INCIDENT, "FIGHT", SeverityLevel.HIGH, "Gym"), "principal");
        outboxRelay.publishPending();
        var published = outboxRepository.findAll().stream()
                .filter(e -> e.getStatus() == OutboxStatus.PUBLISHED)
                .count();
        assertTrue(published >= 1);
        verify(kafkaTemplate).send(anyString(), anyString(), anyString());
    }
}
