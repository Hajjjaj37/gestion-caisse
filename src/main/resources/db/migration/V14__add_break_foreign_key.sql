-- Ajouter la contrainte de clé étrangère pour break_id
ALTER TABLE schedule_breaksets
ADD CONSTRAINT fk_schedule_breaksets_break
FOREIGN KEY (break_id) REFERENCES recurring_breaks(id); 