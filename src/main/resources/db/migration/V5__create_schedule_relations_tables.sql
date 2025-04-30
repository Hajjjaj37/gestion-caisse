CREATE TABLE IF NOT EXISTS schedule_days (
    schedule_id BIGINT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    PRIMARY KEY (schedule_id, day_of_week),
    FOREIGN KEY (schedule_id) REFERENCES recurring_schedules(id)
);

CREATE TABLE IF NOT EXISTS schedule_breaksets (
    schedule_id BIGINT NOT NULL,
    break_id BIGINT NOT NULL,
    PRIMARY KEY (schedule_id, break_id),
    FOREIGN KEY (schedule_id) REFERENCES recurring_schedules(id),
    FOREIGN KEY (break_id) REFERENCES recurring_breaks(id)
); 