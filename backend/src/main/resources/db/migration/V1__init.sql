CREATE TABLE security_event (
    id UUID PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    incident_type VARCHAR(100) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    location VARCHAR(200) NOT NULL,
    created_by VARCHAR(80) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE event_notification (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL,
    channel VARCHAR(20) NOT NULL,
    recipient VARCHAR(120) NOT NULL,
    template_name VARCHAR(80) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE postmortem_report (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    summary VARCHAR(2000) NOT NULL,
    root_cause VARCHAR(2000) NOT NULL,
    corrective_actions VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE idempotency_record (
    idempotency_key VARCHAR(150) PRIMARY KEY,
    operation VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
