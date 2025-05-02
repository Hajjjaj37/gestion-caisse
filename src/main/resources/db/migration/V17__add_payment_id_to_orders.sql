ALTER TABLE orders
ADD COLUMN payment_id BIGINT,
ADD FOREIGN KEY (payment_id) REFERENCES payments(id); 