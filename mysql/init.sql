CREATE DATABASE IF NOT EXISTS claims;
USE claims;

CREATE TABLE policy (
    id VARCHAR(250) NOT NULL,
    policy_number VARCHAR(50) NOT NULL,
    holder_name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE claim (
    id VARCHAR(250) NOT NULL,
    policy_id VARCHAR(250) NOT NULL,
    amount DOUBLE NOT NULL,
    status ENUM('PENDING','APPROVED','REJECTED') NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_claim_policy FOREIGN KEY (policy_id)
        REFERENCES policy(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

USE claims;

-- Poblar policies
INSERT INTO policy (id, policy_number, holder_name, start_date, end_date)
VALUES 
('fe3807c6-19ba-48e3-b3fe-6e9e36ab68f9', 'POL-1001', 'Juan Pérez', '2025-01-01', '2025-12-31'),
('fab8087f-94e4-4bce-b1a9-38ef30376973', 'POL-1002', 'María Gómez', '2025-02-01', '2026-01-31'),
('f0f8d169-9c4e-40d4-bbbf-a714a347da51', 'POL-1003', 'Carlos López', '2025-03-01', '2026-02-28');

-- Poblar claims
INSERT INTO claim (id, policy_id, amount, status)
VALUES
('81b11a0a-8432-4392-9f33-07860eba392a', 'fab8087f-94e4-4bce-b1a9-38ef30376973', 1500.00, 'PENDING'),
('edccc395-ef62-4479-981e-b6e9fa73412e', 'fab8087f-94e4-4bce-b1a9-38ef30376973', 3000.50, 'APPROVED'),
('a2e99c8a-7443-4f0e-aa83-7db230d73774', 'fe3807c6-19ba-48e3-b3fe-6e9e36ab68f9', 1200.75, 'REJECTED');
