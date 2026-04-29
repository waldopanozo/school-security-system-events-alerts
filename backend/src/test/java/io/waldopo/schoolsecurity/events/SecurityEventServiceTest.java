package io.waldopo.schoolsecurity.events;

import io.waldopo.schoolsecurity.events.api.CreateEventRequest;
import io.waldopo.schoolsecurity.events.domain.EventType;
import io.waldopo.schoolsecurity.events.domain.SeverityLevel;
import io.waldopo.schoolsecurity.events.service.SecurityEventService;
import io.waldopo.schoolsecurity.postmortem.PostmortemRequest;
import io.waldopo.schoolsecurity.postmortem.PostmortemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
class SecurityEventServiceTest {
    @Autowired
    private SecurityEventService service;
    @Autowired
    private PostmortemService postmortemService;

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

    @TestConfiguration
    static class StubConfig {
        @Bean
        KafkaTemplate<String, String> kafkaTemplate() {
            return mock(KafkaTemplate.class);
        }
    }
}
