package io.waldopo.schoolsecurity.postmortem;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PostmortemRepository extends JpaRepository<PostmortemReport, UUID> {
    Optional<PostmortemReport> findByEventId(UUID eventId);
}
