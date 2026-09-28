-- Database Schema Migration
-- Safely migrates care_interventions table relation from care_plans to care_goals

USE NURSING_HOME_DB;
GO

-- 1. Drop existing foreign key constraint if it exists
IF EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK__care_inte__care___27F8EE98')
BEGIN
    ALTER TABLE care_interventions DROP CONSTRAINT FK__care_inte__care___27F8EE98;
    PRINT 'Dropped constraint FK__care_inte__care___27F8EE98';
END
GO

-- 2. Add care_goal_id column if not exists
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('care_interventions') AND name = 'care_goal_id')
BEGIN
    ALTER TABLE care_interventions ADD care_goal_id BIGINT NULL;
    PRINT 'Added column care_goal_id';
END
GO

-- 3. Populate care_goal_id matching by row number partition
WITH TargetGoals AS (
    SELECT id, care_plan_id,
           ROW_NUMBER() OVER (PARTITION BY care_plan_id ORDER BY id) as rn
    FROM care_goals
),
TargetInterventions AS (
    SELECT id, care_plan_id,
           ROW_NUMBER() OVER (PARTITION BY care_plan_id ORDER BY id) as rn
    FROM care_interventions
)
UPDATE ci
SET ci.care_goal_id = tg.id
FROM care_interventions ci
JOIN TargetInterventions ti ON ci.id = ti.id
JOIN TargetGoals tg ON ti.care_plan_id = tg.care_plan_id AND ti.rn = tg.rn;
PRINT 'Populated care_goal_id from care_goals';
GO

-- 4. For any safety net, set default if still null
UPDATE care_interventions 
SET care_goal_id = (SELECT MIN(id) FROM care_goals WHERE care_goals.care_plan_id = care_interventions.care_plan_id)
WHERE care_goal_id IS NULL;
PRINT 'Applied safety net for care_goal_id';
GO

-- 5. Alter column to be NOT NULL
ALTER TABLE care_interventions ALTER COLUMN care_goal_id BIGINT NOT NULL;
PRINT 'Altered care_goal_id to NOT NULL';
GO

-- 6. Add foreign key constraint
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK_care_interventions_care_goals')
BEGIN
    ALTER TABLE care_interventions ADD CONSTRAINT FK_care_interventions_care_goals FOREIGN KEY (care_goal_id) REFERENCES care_goals(id);
    PRINT 'Added FK_care_interventions_care_goals constraint';
END
GO

-- 7. Drop care_plan_id column if it exists
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('care_interventions') AND name = 'care_plan_id')
BEGIN
    ALTER TABLE care_interventions DROP COLUMN care_plan_id;
    PRINT 'Dropped column care_plan_id';
END
GO
