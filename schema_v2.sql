-- Schema V2: Advanced Inventory, Billing, and Admin features
USE pharmacy_db;

-- =====================================================
-- Phase 10: Advanced Inventory Control
-- =====================================================

-- Add batch tracking, reorder level, and per-item tax to Medicines
ALTER TABLE Medicines ADD COLUMN batch_number VARCHAR(50) AFTER generic_name;
ALTER TABLE Medicines ADD COLUMN reorder_level INT DEFAULT 10 AFTER quantity;
ALTER TABLE Medicines ADD COLUMN tax_rate DECIMAL(5,2) DEFAULT 5.00 AFTER price;

-- Purchase Orders
CREATE TABLE IF NOT EXISTS Purchase_Orders (
    po_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expected_date DATE,
    status ENUM('PENDING','DELIVERED','CANCELLED') DEFAULT 'PENDING',
    notes TEXT,
    created_by INT,
    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id),
    FOREIGN KEY (created_by) REFERENCES Users(user_id)
);

CREATE TABLE IF NOT EXISTS PO_Items (
    po_item_id INT AUTO_INCREMENT PRIMARY KEY,
    po_id INT NOT NULL,
    med_name VARCHAR(100) NOT NULL,
    quantity_ordered INT NOT NULL,
    quantity_received INT DEFAULT 0,
    FOREIGN KEY (po_id) REFERENCES Purchase_Orders(po_id) ON DELETE CASCADE
);

-- Audit Trail
CREATE TABLE IF NOT EXISTS Audit_Log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- =====================================================
-- Phase 11: Real-World Billing
-- =====================================================

ALTER TABLE Sales ADD COLUMN payment_method ENUM('CASH','CARD','UPI') DEFAULT 'CASH' AFTER tax_amount;
ALTER TABLE Sales ADD COLUMN invoice_number VARCHAR(20) AFTER sale_id;

ALTER TABLE Sale_Items ADD COLUMN batch_number VARCHAR(50) AFTER med_id;
ALTER TABLE Sale_Items ADD COLUMN tax_rate DECIMAL(5,2) DEFAULT 5.00 AFTER price_at_sale;

-- Returns
CREATE TABLE IF NOT EXISTS Returns (
    return_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    sale_item_id INT NOT NULL,
    med_id INT,
    quantity_returned INT NOT NULL,
    refund_amount DECIMAL(10,2) NOT NULL,
    reason TEXT,
    processed_by INT,
    return_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sale_id) REFERENCES Sales(sale_id),
    FOREIGN KEY (sale_item_id) REFERENCES Sale_Items(sale_item_id),
    FOREIGN KEY (med_id) REFERENCES Medicines(med_id),
    FOREIGN KEY (processed_by) REFERENCES Users(user_id)
);

-- =====================================================
-- Phase 12: Admin & Analytics
-- =====================================================

CREATE TABLE IF NOT EXISTS Shifts (
    shift_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    open_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    close_time TIMESTAMP NULL,
    opening_cash DECIMAL(10,2) NOT NULL,
    closing_cash DECIMAL(10,2) NULL,
    expected_cash DECIMAL(10,2) NULL,
    status ENUM('OPEN','CLOSED') DEFAULT 'OPEN',
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Update existing dummy data with batch numbers
UPDATE Medicines SET batch_number = 'BATCH-PFZ-2026A', reorder_level = 20 WHERE name = 'Tylenol';
UPDATE Medicines SET batch_number = 'BATCH-JNJ-2026B', reorder_level = 15 WHERE name = 'Advil';
UPDATE Medicines SET batch_number = 'BATCH-PFZ-2025C', reorder_level = 10 WHERE name = 'Amoxil';
UPDATE Medicines SET batch_number = 'BATCH-CIP-2026D', reorder_level = 8 WHERE name = 'Zithromax';
UPDATE Medicines SET batch_number = 'BATCH-JNJ-2027A', reorder_level = 12 WHERE name = 'Lipitor';
UPDATE Medicines SET batch_number = 'BATCH-SUN-2026E', reorder_level = 15 WHERE name = 'Glucophage';
UPDATE Medicines SET batch_number = 'BATCH-GSK-2025F', reorder_level = 5 WHERE name = 'Ventolin';
UPDATE Medicines SET batch_number = 'BATCH-CIP-2026G', reorder_level = 30 WHERE name = 'Crocin Advance';
UPDATE Medicines SET batch_number = 'BATCH-SUN-2026H', reorder_level = 8 WHERE name = 'Augmentin';
UPDATE Medicines SET batch_number = 'BATCH-GSK-2027B', reorder_level = 10 WHERE name = 'Losartan Potassium';

-- Set different tax rates per category
UPDATE Medicines SET tax_rate = 5.00 WHERE category = 'Painkiller';
UPDATE Medicines SET tax_rate = 12.00 WHERE category = 'Antibiotic';
UPDATE Medicines SET tax_rate = 5.00 WHERE category = 'Anti-inflammatory';
UPDATE Medicines SET tax_rate = 5.00 WHERE category = 'Cholesterol';
UPDATE Medicines SET tax_rate = 0.00 WHERE category = 'Diabetes';
UPDATE Medicines SET tax_rate = 12.00 WHERE category = 'Respiratory';
UPDATE Medicines SET tax_rate = 5.00 WHERE category = 'Blood Pressure';
