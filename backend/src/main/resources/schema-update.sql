-- Database Schema Update Script
-- Safely aligns SQL Server database schema with Java Entities

-- 1. Check and add columns for inventory_categories
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('inventory_categories') AND name = 'is_deleted')
    ALTER TABLE inventory_categories ADD is_deleted bit NOT NULL DEFAULT 0;
GO

-- 2. Check and add columns for durable_medical_equipment
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('durable_medical_equipment') AND name = 'is_deleted')
    ALTER TABLE durable_medical_equipment ADD is_deleted bit NOT NULL DEFAULT 0;
GO

-- 3. Check and add columns for consumable_supplies
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('consumable_supplies') AND name = 'is_deleted')
    ALTER TABLE consumable_supplies ADD is_deleted bit NOT NULL DEFAULT 0;
GO

-- 4. Check and add columns for incidents
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('incidents') AND name = 'created_at')
    ALTER TABLE incidents ADD created_at datetimeoffset(0) NOT NULL DEFAULT SYSDATETIMEOFFSET();
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('incidents') AND name = 'occurred_at')
    ALTER TABLE incidents ADD occurred_at datetimeoffset(0) NOT NULL DEFAULT SYSDATETIMEOFFSET();
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('incidents') AND name = 'incident_location')
    ALTER TABLE incidents ADD incident_location nvarchar(255);
GO

-- 5. Check and add created_by column for care_plans
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('care_plans') AND name = 'created_by')
    ALTER TABLE care_plans ADD created_by BIGINT NULL;
GO
