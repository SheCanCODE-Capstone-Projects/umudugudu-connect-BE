-- ============================================================
-- Recreate tables only (ENUMs already exist)
-- Run this in pgAdmin Query Tool on umudugudu_test
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS villages (
    id         UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       VARCHAR(100) NOT NULL,
    district   VARCHAR(50)  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS isibs (
    id         UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       VARCHAR(100) NOT NULL,
    village_id UUID         NOT NULL REFERENCES villages(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS users (
    id           UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    full_name    VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15)  UNIQUE NOT NULL,
    role         user_role    NOT NULL,
    village_id   UUID         REFERENCES villages(id),
    isibo_id     UUID         REFERENCES isibs(id),
    fcm_token    VARCHAR(255),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS activities (
    id           UUID             PRIMARY KEY DEFAULT uuid_generate_v4(),
    village_id   UUID             NOT NULL REFERENCES villages(id),
    created_by   UUID             NOT NULL REFERENCES users(id),
    type         activity_type    NOT NULL,
    title        VARCHAR(200)     NOT NULL,
    scheduled_at TIMESTAMPTZ      NOT NULL,
    location     VARCHAR(255),
    status       activity_status  NOT NULL DEFAULT 'SCHEDULED',
    created_at   TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ      NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS payments (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    payer_id        UUID            NOT NULL REFERENCES users(id),
    amount_rwf      DECIMAL(10,2)   NOT NULL,
    payment_method  payment_method  NOT NULL,
    external_tx_id  VARCHAR(100)    UNIQUE,
    status          payment_status  NOT NULL DEFAULT 'PENDING',
    paid_at         TIMESTAMPTZ,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS penalties (
    id           UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    citizen_id   UUID            NOT NULL REFERENCES users(id),
    activity_id  UUID            NOT NULL REFERENCES activities(id),
    assigned_by  UUID            NOT NULL REFERENCES users(id),
    amount_rwf   DECIMAL(10,2)   NOT NULL,
    reason       TEXT,
    status       penalty_status  NOT NULL DEFAULT 'UNPAID',
    payment_id   UUID            REFERENCES payments(id),
    created_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS attendance_records (
    id              UUID               PRIMARY KEY DEFAULT uuid_generate_v4(),
    activity_id     UUID               NOT NULL REFERENCES activities(id),
    citizen_id      UUID               NOT NULL REFERENCES users(id),
    marked_by       UUID               NOT NULL REFERENCES users(id),
    status          attendance_status  NOT NULL,
    marked_at       TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    synced_offline  BOOLEAN            NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ        NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS service_requests (
    id           UUID                    PRIMARY KEY DEFAULT uuid_generate_v4(),
    citizen_id   UUID                    NOT NULL REFERENCES users(id),
    type         service_request_type    NOT NULL,
    description  TEXT                    NOT NULL,
    status       service_request_status  NOT NULL DEFAULT 'PENDING',
    reviewed_by  UUID                    REFERENCES users(id),
    response     TEXT,
    created_at   TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ             NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS notifications (
    id           UUID                  PRIMARY KEY DEFAULT uuid_generate_v4(),
    recipient_id UUID                  NOT NULL REFERENCES users(id),
    type         VARCHAR(50)           NOT NULL,
    title        VARCHAR(200),
    message      TEXT                  NOT NULL,
    channel      notification_channel  NOT NULL DEFAULT 'PUSH',
    is_read      BOOLEAN               NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ           NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS emergency_reports (
    id           UUID              PRIMARY KEY DEFAULT uuid_generate_v4(),
    reporter_id  UUID              NOT NULL REFERENCES users(id),
    type         emergency_type    NOT NULL,
    description  TEXT              NOT NULL,
    village_id   UUID              NOT NULL REFERENCES villages(id),
    status       emergency_status  NOT NULL DEFAULT 'REPORTED',
    created_at   TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ       NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id            UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    performed_by  UUID         NOT NULL REFERENCES users(id),
    action        VARCHAR(100) NOT NULL,
    entity_type   VARCHAR(100) NOT NULL,
    entity_id     UUID,
    old_value     JSONB,
    new_value     JSONB,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       TEXT        UNIQUE NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL,
    revoked     BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- Test data
-- ============================================================

INSERT INTO villages (id, name, district)
VALUES ('00000000-0000-0000-0000-000000000001', 'Kacyiru Village', 'Gasabo')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, full_name, phone_number, role, village_id, is_active)
VALUES (
    '00000000-0000-0000-0000-000000000002',
    'Test Citizen',
    '+250788000001',
    'CITIZEN',
    '00000000-0000-0000-0000-000000000001',
    true
) ON CONFLICT (id) DO NOTHING;
