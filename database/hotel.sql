
-- HOTEL ROOM MANAGEMENT SYSTEM
-- Database creation

CREATE DATABASE IF NOT EXISTS hotel_management;

USE hotel_management;

-- 1. ROOMS TABLE
CREATE TABLE IF NOT EXISTS rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type ENUM('SINGLE', 'DOUBLE', 'DELUXE') NOT NULL,
    daily_price DECIMAL(10,2) NOT NULL,
    maintenance_status BOOLEAN DEFAULT FALSE,
    CHECK (daily_price > 0)
);

-- 2. GUESTS TABLE
CREATE TABLE IF NOT EXISTS guests (
    guest_id INT AUTO_INCREMENT PRIMARY KEY,
    guest_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(255),
    government_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. STAYS TABLE
CREATE TABLE IF NOT EXISTS stays (
    stay_id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in DATETIME NOT NULL,
    check_out DATETIME DEFAULT NULL,
    daily_price DECIMAL(10,2) NOT NULL,
    total_bill DECIMAL(10,2) DEFAULT NULL,

    FOREIGN KEY (guest_id) REFERENCES guests(guest_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),

    CHECK (check_out IS NULL OR check_out >= check_in),
    CHECK (daily_price > 0)
);

-- 3.1 Prevent multiple active stays in the same room
CREATE UNIQUE INDEX unique_active_room
ON stays (
    room_id,
    (CASE WHEN check_out IS NULL THEN 1 ELSE NULL END)
);

-- 4. MAINTENANCE TABLE
CREATE TABLE IF NOT EXISTS maintenance (
    maintenance_id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    start_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_date DATETIME DEFAULT NULL,
    status ENUM('ACTIVE', 'COMPLETED') DEFAULT 'ACTIVE',

    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

-- SAMPLE ROOM DATA
INSERT IGNORE INTO rooms
(room_number, room_type, daily_price, maintenance_status)
VALUES
('101', 'SINGLE', 1000.00, FALSE),
('102', 'SINGLE', 1000.00, FALSE),
('103', 'DOUBLE', 1500.00, FALSE),
('104', 'DOUBLE', 1500.00, FALSE),
('105', 'DELUXE', 2500.00, FALSE),
('106', 'DELUXE', 2500.00, FALSE),
('107', 'SINGLE', 1000.00, FALSE),
('108', 'DOUBLE', 1500.00, FALSE);

-- VERIFY THE ROOM DATA
SELECT * FROM rooms;
