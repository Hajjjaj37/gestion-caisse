CREATE TABLE IF NOT EXISTS recurring_breaks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration_minutes INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    comment TEXT,
    schedule_id BIGINT NULL,
    FOREIGN KEY (schedule_id) REFERENCES recurring_schedules(id)
); 