package io.waldopo.schoolsecurity.outbox;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxRelay {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public OutboxRelay(
            OutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.outbox.topic:school.security.event.v1}") String topic
    ) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Scheduled(fixedDelayString = "${app.outbox.relay-delay-ms:2000}")
    @Transactional
    public void publishPending() {
        var pending = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        for (OutboxEvent event : pending) {
            try {
                kafkaTemplate.send(topic, event.getAggregateId().toString(), event.getPayload());
                event.markPublished();
            } catch (RuntimeException ex) {
                event.markFailedAttempt();
            }
        }
        outboxRepository.saveAll(pending);
    }
}
