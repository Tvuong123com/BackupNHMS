USE NURSING_HOME_DB;

-- 1. Insert Recent Fall Incident for Resident 1 (High Fall Risk)
INSERT INTO incidents (
    resident_id, incident_type, severity_id, occurred_at, sla_deadline,
    description, incident_location, status, reported_by, reported_at, created_at
) VALUES (
    1, 'FALL', 2, '2026-11-10 03:15:00 +00:00', '2026-11-10 07:15:00 +00:00',
    'Unwitnessed fall near bedside during attempt to transfer to commode without calling staff.',
    'Room 101 - Bed A', 'OPEN', 1, '2026-11-10 03:30:00 +00:00', '2026-11-10 03:30:00 +00:00'
);

-- 2. Insert Recent Wandering Observation for Resident 1
INSERT INTO incidents (
    resident_id, incident_type, severity_id, occurred_at, sla_deadline,
    description, incident_location, status, reported_by, reported_at, created_at
) VALUES (
    1, 'ALTERCATION', 2, '2026-11-14 17:45:00 +00:00', '2026-11-14 19:45:00 +00:00',
    'Observed wandering near exit doors attempting elopement and agitated with care staff.',
    'Corridor East Wing', 'OPEN', 1, '2026-11-14 18:00:00 +00:00', '2026-11-14 18:00:00 +00:00'
);

-- 3. Insert Recent Moderate Fall for Resident 2
INSERT INTO incidents (
    resident_id, incident_type, severity_id, occurred_at, sla_deadline,
    description, incident_location, status, reported_by, reported_at, created_at
) VALUES (
    2, 'FALL', 1, '2026-11-08 08:30:00 +00:00', '2026-11-08 12:30:00 +00:00',
    'Slip on wet floor surface during morning ambulation, assisted to ground with no acute fracture.',
    'Bathroom Wing B', 'OPEN', 1, '2026-11-08 09:00:00 +00:00', '2026-11-08 09:00:00 +00:00'
);
