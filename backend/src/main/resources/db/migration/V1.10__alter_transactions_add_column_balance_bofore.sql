ALTER TABLE TRANSACTIONS
    ADD COLUMN active_balance_before DECIMAL(18, 8) DEFAULT 0 AFTER currency_name;
