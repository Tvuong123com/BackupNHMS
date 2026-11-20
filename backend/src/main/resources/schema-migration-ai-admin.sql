-- Apply to the configured SQL Server database before enabling F1/F6 persistence.
IF OBJECT_ID(N'dbo.ai_chat_turns', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ai_chat_turns (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        conversation_id NVARCHAR(36) NOT NULL,
        owner_email NVARCHAR(255) NOT NULL,
        role NVARCHAR(12) NOT NULL,
        content NVARCHAR(MAX) NOT NULL,
        created_at DATETIMEOFFSET(6) NOT NULL
    );
    CREATE INDEX IX_ai_chat_turns_owner_conversation
        ON dbo.ai_chat_turns(owner_email, conversation_id, created_at);
END;
GO

-- Older care-plan schemas also omitted the creator reference used by JPA.
IF OBJECT_ID(N'dbo.care_plans', N'U') IS NOT NULL
    AND COL_LENGTH(N'dbo.care_plans', N'created_by') IS NULL
BEGIN
    ALTER TABLE dbo.care_plans ADD created_by BIGINT NULL;
    EXEC(N'UPDATE dbo.care_plans SET created_by = COALESCE(last_reviewed_by, (SELECT MIN(id) FROM dbo.users)) WHERE created_by IS NULL');
END;
GO

-- The legacy incident table predates IncidentEntity.createdAt in some installs.
-- Backfill from the recorded report timestamp so existing incidents remain readable.
IF OBJECT_ID(N'dbo.incidents', N'U') IS NOT NULL
    AND COL_LENGTH(N'dbo.incidents', N'created_at') IS NULL
BEGIN
    ALTER TABLE dbo.incidents ADD created_at DATETIMEOFFSET(6) NULL;
    EXEC(N'UPDATE dbo.incidents SET created_at = COALESCE(reported_at, occurred_at, SYSDATETIMEOFFSET())');
    EXEC(N'ALTER TABLE dbo.incidents ALTER COLUMN created_at DATETIMEOFFSET(6) NOT NULL');
END;
GO

IF OBJECT_ID(N'dbo.ai_summary_drafts', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ai_summary_drafts (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        audience NVARCHAR(16) NOT NULL,
        resident_id BIGINT NULL,
        created_by NVARCHAR(255) NOT NULL,
        shift_start DATETIMEOFFSET(6) NOT NULL,
        shift_end DATETIMEOFFSET(6) NOT NULL,
        source_events_json NVARCHAR(MAX) NOT NULL,
        draft_json NVARCHAR(MAX) NOT NULL,
        created_at DATETIMEOFFSET(6) NOT NULL
    );
    CREATE INDEX IX_ai_summary_drafts_owner ON dbo.ai_summary_drafts(created_by, id DESC);
END;
GO

IF OBJECT_ID(N'dbo.ai_voice_observations', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ai_voice_observations (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        resident_id BIGINT NOT NULL,
        task_id BIGINT NULL,
        confirmed_by NVARCHAR(255) NOT NULL,
        transcript NVARCHAR(MAX) NOT NULL,
        parsed_json NVARCHAR(MAX) NOT NULL,
        confirmed_at DATETIMEOFFSET(6) NOT NULL
    );
    CREATE INDEX IX_ai_voice_observations_resident ON dbo.ai_voice_observations(resident_id, confirmed_at);
END;
GO

IF OBJECT_ID(N'dbo.ai_vector_documents', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ai_vector_documents (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        source_type NVARCHAR(32) NOT NULL,
        source_id NVARCHAR(64) NOT NULL,
        resident_id BIGINT NULL,
        title NVARCHAR(255) NOT NULL,
        content NVARCHAR(MAX) NOT NULL,
        embedding_json NVARCHAR(MAX) NOT NULL,
        updated_at DATETIMEOFFSET(6) NOT NULL
    );
    CREATE UNIQUE INDEX UX_ai_vector_documents_source
        ON dbo.ai_vector_documents(source_type, source_id);
END;
GO

IF OBJECT_ID(N'dbo.ai_fall_screenings', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ai_fall_screenings (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        resident_id BIGINT NOT NULL,
        assessed_by NVARCHAR(255) NOT NULL,
        history_of_falling INT NOT NULL,
        secondary_diagnosis INT NOT NULL,
        ambulatory_aid INT NOT NULL,
        iv_therapy INT NOT NULL,
        gait INT NOT NULL,
        mental_status INT NOT NULL,
        total_score INT NOT NULL,
        assessed_at DATETIMEOFFSET(6) NOT NULL
    );
    CREATE INDEX IX_ai_fall_screenings_resident ON dbo.ai_fall_screenings(resident_id, id DESC);
END;
GO

IF OBJECT_ID(N'dbo.ai_fall_risk_snapshots', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ai_fall_risk_snapshots (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        resident_id BIGINT NOT NULL,
        dfrs_score INT NOT NULL,
        risk_level NVARCHAR(20) NOT NULL,
        incident_subscore INT NOT NULL,
        medication_subscore INT NOT NULL,
        adl_subscore INT NOT NULL,
        vital_subscore INT NOT NULL,
        recent_falls_count INT NOT NULL,
        active_frid_count INT NOT NULL,
        method_version NVARCHAR(50) NULL,
        assessed_at DATETIMEOFFSET(6) NOT NULL,
        assessed_by NVARCHAR(255) NULL
    );
    CREATE INDEX IX_ai_fall_risk_snapshots_resident ON dbo.ai_fall_risk_snapshots(resident_id, assessed_at DESC);
END;
GO
