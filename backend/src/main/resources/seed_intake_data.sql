-- Seed Pre-Admission Screenings
IF NOT EXISTS (SELECT 1 FROM pre_admission_screenings)
BEGIN
    INSERT INTO pre_admission_screenings (status, resident_id, screened_by, created_at, is_current)
    VALUES 
    ('COMPLETED', 1, 1, SYSDATETIMEOFFSET(), 1),
    ('COMPLETED', 2, 1, SYSDATETIMEOFFSET(), 1),
    ('DRAFT', 3, 1, SYSDATETIMEOFFSET(), 1),
    ('COMPLETED', 4, 1, SYSDATETIMEOFFSET(), 1);
END
GO

-- Seed Admissions
IF NOT EXISTS (SELECT 1 FROM admissions)
BEGIN
    INSERT INTO admissions (admission_date, is_current, created_at, facility_id, pre_admission_screening_id, resident_id)
    VALUES 
    (SYSDATETIMEOFFSET(), 1, SYSDATETIMEOFFSET(), 1, 1, 1),
    (SYSDATETIMEOFFSET(), 1, SYSDATETIMEOFFSET(), 1, 2, 2),
    (SYSDATETIMEOFFSET(), 1, SYSDATETIMEOFFSET(), 1, 4, 4);
END
GO

-- Seed Assessments
IF NOT EXISTS (SELECT 1 FROM assessments)
BEGIN
    INSERT INTO assessments (adl_total_score, status, resident_id, assessed_by, created_at, is_current, is_overridden, suggested_care_level_id, confirmed_care_level_id, admission_id)
    VALUES 
    (24, 'CONFIRMED', 1, 1, SYSDATETIMEOFFSET(), 1, 0, 2, 2, 1),
    (15, 'CONFIRMED', 2, 1, SYSDATETIMEOFFSET(), 1, 0, 1, 1, 2),
    (32, 'DRAFT', 4, 1, SYSDATETIMEOFFSET(), 1, 0, 3, 3, 3);
END
GO
