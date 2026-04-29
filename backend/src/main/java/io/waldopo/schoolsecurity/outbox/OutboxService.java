package io.waldopo.schoolsecurity.outbox;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class OutboxService {
    private final OutboxRepository repository;

    public OutboxService(OutboxRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void enqueue(String eventType, UUID aggregateId, Map<String, Object> payload) {
        String body = """
                {"eventType":"%s","eventVersion":"v1","aggregateId":"%s","payload":"%s"}
                """.formatted(eventType, aggregateId, payload);
        repository.save(new OutboxEvent(eventType, "v1", aggregateId, body));
    }
}
