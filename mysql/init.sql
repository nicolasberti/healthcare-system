CREATE DATABASE IF NOT EXISTS claims;
USE claims;

CREATE TABLE claim (
    id VARCHAR(250) NOT NULL,
    member_id VARCHAR(250) NOT NULL,
    amount DOUBLE NOT NULL,
    status ENUM('PENDING','APPROVED','REJECTED') NOT NULL,
    PRIMARY KEY (id)
);

USE claims;

-- -- Poblar claims
-- INSERT INTO claim (id, policy_id, amount, status)
-- VALUES
-- ('81b11a0a-8432-4392-9f33-07860eba392a', 'fab8087f-94e4-4bce-b1a9-38ef30376973', 1500.00, 'PENDING'),
-- ('edccc395-ef62-4479-981e-b6e9fa73412e', 'fab8087f-94e4-4bce-b1a9-38ef30376973', 3000.50, 'APPROVED'),
-- ('a2e99c8a-7443-4f0e-aa83-7db230d73774', 'fe3807c6-19ba-48e3-b3fe-6e9e36ab68f9', 1200.75, 'REJECTED');
