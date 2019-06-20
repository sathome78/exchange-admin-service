CREATE TABLE IF NOT EXISTS USER_INOUT_STATUS
(
    user_id             INTEGER NOT NULL,
    refill_amount_usd   DECIMAL(12, 2) DEFAULT 0,
    withdraw_amount_usd DECIMAL(12, 2) DEFAULT 0,
    modified            TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
);
