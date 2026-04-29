package io.waldopo.schoolsecurity.events.api;

import io.waldopo.schoolsecurity.events.domain.SecurityEvent;
import io.waldopo.schoolsecurity.events.service.SecurityEventService;
import io.waldopo.schoolsecurity.idempotency.IdempotencyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class SecurityEventController {
    private final SecurityEventService service;
    private final IdempotencyService idempotencyService;

    public SecurityEventController(SecurityEventService service, IdempotencyService idempotencyService) {
        this.service = service;
        this.idempotencyService = idempotencyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PRINCIPAL','TEACHER')")
    public SecurityEvent create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateEventRequest request,
            Authentication authentication
    ) {
        idempotencyService.assertAndRegister(idempotencyKey, "CREATE_EVENT");
        return service.create(request, authentication.getName());
    }

    @PostMapping("/{id}/activate-alert")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public SecurityEvent activate(
            @PathVariable UUID id,
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        idempotencyService.assertAndRegister(idempotencyKey, "ACTIVATE_ALERT_" + id);
        return service.activateAlert(id);
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public SecurityEvent close(
            @PathVariable UUID id,
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        idempotencyService.assertAndRegister(idempotencyKey, "CLOSE_EVENT_" + id);
        return service.close(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRINCIPAL','TEACHER','PARENT')")
    public SecurityEvent get(@PathVariable UUID id) {
        return service.find(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PRINCIPAL','TEACHER')")
    public List<SecurityEvent> list() {
        return service.findAll();
    }
}
