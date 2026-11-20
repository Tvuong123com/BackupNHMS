-- Additive and repeatable migration: keep all existing private STAFF/FAMILY drafts.
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
END;
GO
IF COL_LENGTH(N'dbo.ai_summary_drafts', N'handover_json') IS NULL
    ALTER TABLE dbo.ai_summary_drafts ADD handover_json NVARCHAR(MAX) NULL;
GO
IF COL_LENGTH(N'dbo.ai_summary_drafts', N'approval_status') IS NULL
    ALTER TABLE dbo.ai_summary_drafts ADD approval_status NVARCHAR(16) NOT NULL
        CONSTRAINT DF_ai_summary_drafts_approval DEFAULT N'DRAFT' WITH VALUES;
GO
IF COL_LENGTH(N'dbo.ai_summary_drafts', N'approved_by') IS NULL
    ALTER TABLE dbo.ai_summary_drafts ADD approved_by NVARCHAR(255) NULL;
GO
IF COL_LENGTH(N'dbo.ai_summary_drafts', N'approved_at') IS NULL
    ALTER TABLE dbo.ai_summary_drafts ADD approved_at DATETIMEOFFSET(6) NULL;
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id = OBJECT_ID(N'dbo.ai_summary_drafts') AND name = N'IX_ai_summary_drafts_shared')
    CREATE INDEX IX_ai_summary_drafts_shared ON dbo.ai_summary_drafts(audience, approval_status, approved_at DESC);
GO
