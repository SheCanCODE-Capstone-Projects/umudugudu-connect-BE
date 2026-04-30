-- ============================================================
-- Umudugudu Connect — PostgreSQL 16 Schema
-- Run this in pgAdmin Query Tool against umudugudu_db
-- ============================================================

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- ENUMS
-- ============================================================

CREATE TYPE user_role AS ENUM (
    'CITIZEN',
    'ISIBO_LEADER',
    'VILLAGE_LEADER',
    'ADMIN'
);

CREATE TYPE activity_type AS ENUM (
    'UMUGANDA',
    'IMIHIGO',
    'OTHER'
);

CREATE TYPE activity_status AS ENUM (
    'SCHEDULED',
    'IN_PROGRESS',
    'COMPLETED',
    'CANCELLED'
);

CREATE TYPE attendance_status AS ENUM (
    'PRESENT',
    'ABSENT',
    'EXCUSED'
);

CREATE TYPE penalty_status AS ENUM (
    'UNPAID',
    'PAID',
    'WAIVED'
);

CREATE TYPE payment_method AS ENUM (
    'MTN_MOMO',
    'AIRTEL_MONEY'
);

CREATE TYPE payment_status AS ENUM (
    'PENDING',
    'COMPLETED',
    'FAILED'
);

CREATE TYPE service_request_type AS ENUM (
    'UBUDEHE',
    'ASSISTANCE',
    'DOCUMENT',
    'OTHER'
);

CREATE TYPE service_request_status AS ENUM (
    'PENDING',
    'INFO_REQUIRED',
    'APPROVED',
    'REJECTED'
);

CREATE TYPE notification_channel AS ENUM (
    'PUSH',
    'SMS',
    'BOTH'
);

CREATE TYPE emergency_type AS ENUM (
    'FLOOD',
    'HEALTH',
    'FIRE',
    'OTHER'
);

CREATE TYPE emergency_status AS ENUM (
    'REPORTED',
    'VERIFIED',
    'BROADCAST',
    'RESOLVED'
);

-- ============================================================
-- 2.2 villages
-- ============================================================

CREATE TABLE villages (
    id         UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       VARCHAR(100) NOT NULL,
    district   VARCHAR(50)  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 2.2 isibs
-- ============================================================

CREATE TABLE isibs (
    id         UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       VARCHAR(100) NOT NULL,
    village_id UUID         NOT NULL REFERENCES villages(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_isibs_village_id ON isibs(village_id);

-- ============================================================
-- 2.1 users
-- ============================================================

CREATE TABLE users (
    id           UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    full_name    VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15)  UNIQUE NOT NULL,
    role         user_role    NOT NULL,
    village_id   UUID         REFERENCES villages(id),   -- NULL for ADMIN
    isibo_id     UUID         REFERENCES isibs(id),      -- CITIZEN & ISIBO_LEADER only
    fcm_token    VARCHAR(255),                            -- push notification device token
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_phone_number ON users(phone_number);
CREATE INDEX idx_users_village_id   ON users(village_id);
CREATE INDEX idx_users_isibo_id     ON users(isibo_id);
CREATE INDEX idx_users_role         ON users(role);

-- ============================================================
-- 2.3 activities
-- ============================================================

CREATE TABLE activities (
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

CREATE INDEX idx_activities_village_id   ON activities(village_id);
CREATE INDEX idx_activities_created_by   ON activities(created_by);
CREATE INDEX idx_activities_scheduled_at ON activities(scheduled_at);

-- ============================================================
-- 2.4 attendance_records
-- ============================================================

CREATE TABLE attendance_records (
    id              UUID               PRIMARY KEY DEFAULT uuid_generate_v4(),
    activity_id     UUID               NOT NULL REFERENCES activities(id),
    citizen_id      UUID               NOT NULL REFERENCES users(id),
    marked_by       UUID               NOT NULL REFERENCES users(id),
    status          attendance_status  NOT NULL,
    marked_at       TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    synced_offline  BOOLEAN            NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ        NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_attendance_activity_id ON attendance_records(activity_id);
CREATE INDEX idx_attendance_citizen_id  ON attendance_records(citizen_id);
CREATE INDEX idx_attendance_marked_by   ON attendance_records(marked_by);

-- ============================================================
-- 2.6 payments  (defined before penalties — penalties FK → payments)
-- ============================================================

CREATE TABLE payments (
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

CREATE INDEX idx_payments_payer_id       ON payments(payer_id);
CREATE INDEX idx_payments_status         ON payments(status);
CREATE INDEX idx_payments_external_tx_id ON payments(external_tx_id);

-- ============================================================
-- 2.5 penalties
-- ============================================================

CREATE TABLE penalties (
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

CREATE INDEX idx_penalties_citizen_id  ON penalties(citizen_id);
CREATE INDEX idx_penalties_activity_id ON penalties(activity_id);
CREATE INDEX idx_penalties_assigned_by ON penalties(assigned_by);
CREATE INDEX idx_penalties_status      ON penalties(status);

-- ============================================================
-- 2.7 service_requests
-- ============================================================

CREATE TABLE service_requests (
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

CREATE INDEX idx_service_requests_citizen_id  ON service_requests(citizen_id);
CREATE INDEX idx_service_requests_reviewed_by ON service_requests(reviewed_by);
CREATE INDEX idx_service_requests_status      ON service_requests(status);

-- ============================================================
-- 2.8 notifications
-- ============================================================

CREATE TABLE notifications (
    id           UUID                  PRIMARY KEY DEFAULT uuid_generate_v4(),
    recipient_id UUID                  NOT NULL REFERENCES users(id),
    type         VARCHAR(50)           NOT NULL,
    title        VARCHAR(200),
    message      TEXT                  NOT NULL,
    channel      notification_channel  NOT NULL DEFAULT 'PUSH',
    is_read      BOOLEAN               NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ           NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_recipient_id ON notifications(recipient_id);
CREATE INDEX idx_notifications_is_read      ON notifications(is_read);

-- ============================================================
-- 2.8 emergency_reports
-- ============================================================

CREATE TABLE emergency_reports (
    id           UUID              PRIMARY KEY DEFAULT uuid_generate_v4(),
    reporter_id  UUID              NOT NULL REFERENCES users(id),
    type         emergency_type    NOT NULL,
    description  TEXT              NOT NULL,
    village_id   UUID              NOT NULL REFERENCES villages(id),
    status       emergency_status  NOT NULL DEFAULT 'REPORTED',
    created_at   TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_emergency_reports_reporter_id ON emergency_reports(reporter_id);
CREATE INDEX idx_emergency_reports_village_id  ON emergency_reports(village_id);
CREATE INDEX idx_emergency_reports_status      ON emergency_reports(status);

-- ============================================================
-- 2.8 audit_logs
-- ============================================================

CREATE TABLE audit_logs (
    id            UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    performed_by  UUID         NOT NULL REFERENCES users(id),
    action        VARCHAR(100) NOT NULL,
    entity_type   VARCHAR(100) NOT NULL,
    entity_id     UUID,
    old_value     JSONB,
    new_value     JSONB,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_performed_by ON audit_logs(performed_by);
CREATE INDEX idx_audit_logs_entity_type  ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_entity_id    ON audit_logs(entity_id);
CREATE INDEX idx_audit_logs_created_at   ON audit_logs(created_at);

-- ============================================================
-- refresh_tokens  (for JWT refresh flow)
-- ============================================================

CREATE TABLE refresh_tokens (
    id          UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       TEXT        UNIQUE NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL,
    revoked     BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token   ON refresh_tokens(token);

-- ============================================================
-- updated_at auto-update trigger
-- ============================================================

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_villages_updated_at         BEFORE UPDATE ON villages         FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_isibs_updated_at            BEFORE UPDATE ON isibs            FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_users_updated_at            BEFORE UPDATE ON users            FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_activities_updated_at       BEFORE UPDATE ON activities       FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_payments_updated_at         BEFORE UPDATE ON payments         FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_penalties_updated_at        BEFORE UPDATE ON penalties        FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_service_requests_updated_at BEFORE UPDATE ON service_requests FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_emergency_reports_updated_at BEFORE UPDATE ON emergency_reports FOR EACH ROW EXECUTE FUNCTION set_updated_at();
