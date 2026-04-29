package io.waldopo.schoolsecurity.events.api;

import io.waldopo.schoolsecurity.events.domain.EventType;
import io.waldopo.schoolsecurity.events.domain.SeverityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEventRequest(
        @NotNull EventType type,
        @NotBlank String incidentType,
        @NotNull SeverityLevel severity,
        @NotBlank String location
) {}
