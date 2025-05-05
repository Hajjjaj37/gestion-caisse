-- Supprimer d'abord toutes les contraintes de clé étrangère si elles existent
SET @constraint_exists = (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS 
    WHERE CONSTRAINT_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'schedule_breaksets' 
    AND CONSTRAINT_NAME = 'schedule_breaksets_ibfk_1');
SET @sql = IF(@constraint_exists > 0, 'ALTER TABLE schedule_breaksets DROP FOREIGN KEY schedule_breaksets_ibfk_1', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @constraint_exists = (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS 
    WHERE CONSTRAINT_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'schedule_days' 
    AND CONSTRAINT_NAME = 'schedule_days_ibfk_1');
SET @sql = IF(@constraint_exists > 0, 'ALTER TABLE schedule_days DROP FOREIGN KEY schedule_days_ibfk_1', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @constraint_exists = (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS 
    WHERE CONSTRAINT_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'recurring_breaks' 
    AND CONSTRAINT_NAME = 'recurring_breaks_ibfk_1');
SET @sql = IF(@constraint_exists > 0, 'ALTER TABLE recurring_breaks DROP FOREIGN KEY recurring_breaks_ibfk_1', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Supprimer les tables existantes dans le bon ordre
DROP TABLE IF EXISTS recurring_schedule_breaks;
DROP TABLE IF EXISTS recurring_schedule_days;
DROP TABLE IF EXISTS recurring_schedules;

-- Créer la nouvelle table recurring_schedules
CREATE TABLE recurring_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- Créer la table pour les jours de la semaine
CREATE TABLE recurring_schedule_days (
    schedule_id BIGINT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    FOREIGN KEY (schedule_id) REFERENCES recurring_schedules(id) ON DELETE CASCADE
);

-- Créer la table de jointure pour les pauses
CREATE TABLE recurring_schedule_breaks (
    schedule_id BIGINT NOT NULL,
    break_id BIGINT NOT NULL,
    FOREIGN KEY (schedule_id) REFERENCES recurring_schedules(id) ON DELETE CASCADE,
    FOREIGN KEY (break_id) REFERENCES recurring_breaks(id) ON DELETE CASCADE
);

-- Recréer la contrainte de clé étrangère pour recurring_breaks
ALTER TABLE recurring_breaks ADD CONSTRAINT recurring_breaks_ibfk_1 
FOREIGN KEY (schedule_id) REFERENCES recurring_schedules(id) ON DELETE CASCADE; 