package io.waldopo.schoolsecurity.templates;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/templates")
public class TemplatesController {
    @GetMapping
    @PreAuthorize("hasAnyRole('PRINCIPAL','TEACHER')")
    public List<Map<String, String>> list() {
        return List.of(
                Map.of("channel", "EMAIL", "name", "GENERAL_TEMPLATE", "version", "v1"),
                Map.of("channel", "SMS", "name", "SHORT_TEMPLATE", "version", "v1"),
                Map.of("channel", "PUSH", "name", "PUSH_TEMPLATE", "version", "v1")
        );
    }
}
