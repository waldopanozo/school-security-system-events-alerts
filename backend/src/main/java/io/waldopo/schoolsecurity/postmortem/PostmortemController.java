package io.waldopo.schoolsecurity.postmortem;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/postmortems")
public class PostmortemController {
    private final PostmortemService service;

    public PostmortemController(PostmortemService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PRINCIPAL')")
    public PostmortemReport create(@Valid @RequestBody PostmortemRequest request) {
        return service.create(request);
    }
}
