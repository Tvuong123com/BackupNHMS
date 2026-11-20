-- Clinical Demo Seed for DFRS, FRIDs, and Behavioral History
-- Aligns incident dates and active medications to the current test window (November 2026)

USE NURSING_HOME_DB;

-- 1. Insert Recent Fall Incident for Resident 1 (High Fall Risk)
INSERT INTO incidents (
    resident_id, incident_type, severity_id, occurred_at, 
    description, incident_location, status, reported_by, reported_at, created_at
) VALUES (
    1, 'FALL', 2, '2026-11-10 03:15:00 +00:00',
    'Unwitnessed fall near bedside during attempt to transfer to commode without calling staff.',
    'Room 101 - Bed A', 'REPORTED', 1, '2026-11-10 03:30:00 +00:00', '2026-11-10 03:30:00 +00:00'
);

-- 2. Insert Recent Wandering / Elopement Observation for Resident 1 (Memory Care)
INSERT INTO incidents (
    resident_id, incident_type, severity_id, occurred_at, 
    description, incident_location, status, reported_by, reported_at, created_at
) VALUES (
    1, 'ELOPEMENT', 2, '2026-11-14 17:45:00 +00:00',
    'Observed pacing near alarmed courtyard exit door, disoriented to time and attempting to exit facility.',
    'Corridor East Wing', 'REPORTED', 1, '2026-11-14 18:00:00 +00:00', '2026-11-14 18:00:00 +00:00'
);

-- 3. Insert Recent Moderate Fall for Resident 2
INSERT INTO incidents (
    resident_id, incident_type, severity_id, occurred_at, 
    description, incident_location, status, reported_by, reported_at, created_at
) VALUES (
    2, 'FALL', 1, '2026-11-08 08:30:00 +00:00',
    'Slip on wet floor surface during morning ambulation, assisted to ground with no acute fracture.',
    'Bathroom Wing B', 'RESOLVED', 1, '2026-11-08 09:00:00 +00:00', '2026-11-08 09:00:00 +00:00'
);

-- 4. Insert High-Risk FRID Medications for Resident 1 (Sedative/Hypnotic FRID)
INSERT INTO medication_orders (
    drug_name, dosage, route, frequency, is_controlled_substance, 
    status, resident_id, prescribed_by, is_deleted, created_at, updated_at
) VALUES 
('Lorazepam', '1mg', 'Oral', 'Once daily at bedtime', 1, 'ACTIVE', 1, 1, 0, '2026-11-01 08:00:00 +00:00', '2026-11-01 08:00:00 +00:00'),
('Zolpidem', '5mg', 'Oral', 'PRN insomnia at night', 1, 'ACTIVE', 1, 1, 0, '2026-11-01 08:00:00 +00:00', '2026-11-01 08:00:00 +00:00');

-- 5. Insert Cardiovascular / Diuretic FRID for Resident 2 (Orthostatic risk)
INSERT INTO medication_orders (
    drug_name, dosage, route, frequency, is_controlled_substance, 
    status, resident_id, prescribed_by, is_deleted, created_at, updated_at
) VALUES 
('Furosemide', '40mg', 'Oral', 'Every morning', 0, 'ACTIVE', 2, 1, 0, '2026-11-01 08:00:00 +00:00', '2026-11-01 08:00:00 +00:00'),
('Amlodipine', '5mg', 'Oral', 'Once daily', 0, 'ACTIVE', 2, 1, 0, '2026-11-01 08:00:00 +00:00', '2026-11-01 08:00:00 +00:00');

-- 6. Insert Antipsychotic FRID for Resident 3
INSERT INTO medication_orders (
    drug_name, dosage, route, frequency, is_controlled_substance, 
    status, resident_id, prescribed_by, is_deleted, created_at, updated_at
) VALUES 
('Haloperidol', '2mg', 'Oral', 'Twice daily', 0, 'ACTIVE', 3, 1, 0, '2026-11-01 08:00:00 +00:00', '2026-11-01 08:00:00 +00:00');
