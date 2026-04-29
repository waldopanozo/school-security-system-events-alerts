package io.waldopo.schoolsecurity.postmortem;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostmortemService {
    private final PostmortemRepository repository;

    public PostmortemService(PostmortemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PostmortemReport create(PostmortemRequest request) {
        return repository.save(new PostmortemReport(
                request.eventId(),
                request.summary(),
                request.rootCause(),
                request.correctiveActions()
        ));
    }
}
