package io.waldopo.schoolsecurity.postmortem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PostmortemRequest(
        @NotNull UUID eventId,
        @NotBlank String summary,
        @NotBlank String rootCause,
        @NotBlank String correctiveActions
) {}
