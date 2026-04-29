package io.waldopo.schoolsecurity.events.repo;

import io.waldopo.schoolsecurity.events.domain.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SecurityEventRepository extends JpaRepository<SecurityEvent, UUID> {
}
