package io.waldopo.schoolsecurity.events.service;

import io.waldopo.schoolsecurity.events.api.CreateEventRequest;
import io.waldopo.schoolsecurity.events.domain.EventStatus;
import io.waldopo.schoolsecurity.events.domain.SecurityEvent;
import io.waldopo.schoolsecurity.events.repo.SecurityEventRepository;
import io.waldopo.schoolsecurity.notifications.NotificationLog;
import io.waldopo.schoolsecurity.notifications.NotificationLogRepository;
import io.waldopo.schoolsecurity.postmortem.PostmortemRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SecurityEventService {
    private static final String TOPIC = "school.security.event.v1";
    private final SecurityEventRepository repository;
    private final NotificationLogRepository notificationRepository;
    private final PostmortemRepository postmortemRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public SecurityEventService(
            SecurityEventRepository repository,
            NotificationLogRepository notificationRepository,
            PostmortemRepository postmortemRepository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.repository = repository;
        this.notificationRepository = notificationRepository;
        this.postmortemRepository = postmortemRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public SecurityEvent create(CreateEventRequest request, String actor) {
        SecurityEvent event = repository.save(new SecurityEvent(
                request.type(), request.incidentType(), request.severity(), request.location(), actor));
        publish("SecurityEventCreated.v1", event.getId(), Map.of("severity", event.getSeverity().name()));
        return event;
    }

    @Transactional
    public SecurityEvent activateAlert(UUID eventId) {
        SecurityEvent event = find(eventId);
        event.activateAlert();
        simulateNotifications(event);
        publish("SecurityAlertActivated.v1", eventId, Map.of("status", event.getStatus().name()));
        return repository.save(event);
    }

    @Transactional
    public SecurityEvent close(UUID eventId) {
        SecurityEvent event = find(eventId);
        if (postmortemRepository.findByEventId(eventId).isEmpty()) {
            throw new IllegalStateException("Cannot close event without postmortem");
        }
        event.close();
        publish("SecurityEventClosed.v1", eventId, Map.of("status", EventStatus.CLOSED.name()));
        return repository.save(event);
    }

    public SecurityEvent find(UUID id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }

    public List<SecurityEvent> findAll() {
        return repository.findAll();
    }

    private void simulateNotifications(SecurityEvent event) {
        List<NotificationLog> logs = List.of(
                new NotificationLog(event.getId(), "EMAIL", "parent1@school.test", "GENERAL_TEMPLATE", "SENT"),
                new NotificationLog(event.getId(), "SMS", "56911111111", "SHORT_TEMPLATE", "SENT"),
                new NotificationLog(event.getId(), "PUSH", "guardian-app-1", "PUSH_TEMPLATE", "SENT")
        );
        notificationRepository.saveAll(logs);
        publish("SecurityNotificationDispatched.v1", event.getId(), Map.of("total", logs.size()));
    }

    private void publish(String eventType, UUID aggregateId, Map<String, Object> payload) {
        String body = """
                {"eventType":"%s","eventVersion":"v1","aggregateId":"%s","payload":"%s"}
                """.formatted(eventType, aggregateId, payload);
        kafkaTemplate.send(TOPIC, aggregateId.toString(), body);
    }
}
