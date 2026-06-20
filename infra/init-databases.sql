-- PeerPay – Database Initialization Script
-- Creates separate logical databases for each microservice

CREATE DATABASE peerpay_payments;
CREATE DATABASE peerpay_ledger;
CREATE DATABASE peerpay_reconciliation;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE peerpay_users TO peerpay;
GRANT ALL PRIVILEGES ON DATABASE peerpay_payments TO peerpay;
GRANT ALL PRIVILEGES ON DATABASE peerpay_ledger TO peerpay;
GRANT ALL PRIVILEGES ON DATABASE peerpay_reconciliation TO peerpay;
