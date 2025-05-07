-- Drop the existing foreign key constraint
ALTER TABLE breaks
DROP FOREIGN KEY breaks_ibfk_1;

-- Rename the column from user_id to employee_id
ALTER TABLE breaks
CHANGE COLUMN user_id employee_id BIGINT NOT NULL;

-- Add the new foreign key constraint
ALTER TABLE breaks
ADD CONSTRAINT breaks_ibfk_1
FOREIGN KEY (employee_id) REFERENCES employees(id); 