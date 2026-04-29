CREATE TABLE outbox_event (
    id UUID PRIMARY KEY,
    event_type VARCHAR(120) NOT NULL,
    event_version VARCHAR(20) NOT NULL,
    aggregate_id UUID NOT NULL,
    payload VARCHAR(4000) NOT NULL,
    status VARCHAR(20) NOT NULL,
    attempts INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP
);

CREATE INDEX idx_outbox_status_created_at ON outbox_event(status, created_at);
