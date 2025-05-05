ALTER TABLE customers
ADD COLUMN tax_id BIGINT,
ADD CONSTRAINT fk_customer_tax
FOREIGN KEY (tax_id) REFERENCES taxes(id); 