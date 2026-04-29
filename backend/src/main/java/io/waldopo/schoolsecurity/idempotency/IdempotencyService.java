package io.waldopo.schoolsecurity.idempotency;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdempotencyService {
    private final IdempotencyRepository repository;

    public IdempotencyService(IdempotencyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void assertAndRegister(String key, String operation) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key is required");
        }
        if (repository.existsById(key)) {
            throw new IllegalStateException("Duplicate operation detected for Idempotency-Key");
        }
        repository.save(new IdempotencyRecord(key, operation));
    }
}
