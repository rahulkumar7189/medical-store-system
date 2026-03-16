-- Add Customers table to track patients/buyers
CREATE TABLE IF NOT EXISTS Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Link Sales to Customers
ALTER TABLE Sales ADD COLUMN customer_id INT;
ALTER TABLE Sales ADD FOREIGN KEY (customer_id) REFERENCES Customers(customer_id);

-- Update existing data: No customer (null) or link to a generic if needed.
