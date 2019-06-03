CREATE TABLE IF NOT EXISTS TRANSACTION
(
    id                int         NOT NULL,
    amount            float       NOT NULL,
    commission_amount float       NOT NULL,
    user_wallet_id    int         NOT NULL,
    source_type       VARCHAR(55) NOT NULL,
    operation_type_id int         NOT NULL,
    currency_id       int         NOT NULL,
    datetime          timestamp default CURRENT_TIMESTAMP
);

INSERT INTO TRANSACTION (id, amount, commission_amount, user_wallet_id, source_type, operation_type_id, currency_id)
VALUES (1, 1, 0.1, 1, 'WITHDRAW', 2, 2),
       (2, 1, 0.1, 1, 'REFILL', 1, 2),
       (3, 1, 0.1, 1, 'WITHDRAW', 2, 2),
       (4, 1, 0.1, 1, 'REFILL', 1, 2),
       (5, 1, 0.1, 1, 'WITHDRAW', 2, 2),
       (6, 1, 0.1, 1, 'REFILL', 1, 5),
       (7, 1, 0.1, 1, 'WITHDRAW', 2, 2),
       (8, 1, 0.1, 1, 'REFILL', 1, 4),
       (9, 1, 0.1, 1, 'WITHDRAW', 2, 2),
       (10, 1, 0.1, 1, 'REFILL', 1, 2),
       (11, 1, 0.1, 1, 'WITHDRAW', 2, 5),
       (12, 1, 0.1, 1, 'REFILL', 1, 2),
       (13, 1, 0.1, 1, 'WITHDRAW', 2, 5),
       (14, 1, 0.1, 1, 'REFILL', 1, 5),
       (15, 1, 0.1, 1, 'WITHDRAW', 2, 4),
       (16, 1, 0.1, 1, 'REFILL', 1, 4),
       (17, 1, 0.1, 1, 'WITHDRAW', 2, 5),
       (18, 1, 0.1, 1, 'REFILL', 1, 2),
       (19, 1, 0.1, 1, 'WITHDRAW', 2, 4),
       (20, 1, 0.1, 1, 'REFILL', 1, 5),
       (21, 1, 0.1, 1, 'WITHDRAW', 2, 4),
       (22, 1, 0.1, 1, 'REFILL', 1, 2),
       (23, 1, 0.1, 1, 'WITHDRAW', 2, 4),
       (24, 1, 0.1, 1, 'REFILL', 1, 2),
       (25, 1, 0.1, 1, 'ORDER', 2, 4),
       (26, 1, 0.1, 1, 'WITHDRAW', 2, 2),
       (27, 1, 0.1, 1, 'ORDER', 2, 4),
       (28, 1, 0.1, 1, 'WITHDRAW', 2, 5),
       (29, 1, 0.1, 1, 'ORDER', 2, 5),
       (30, 1, 0.1, 1, 'WITHDRAW', 2, 4),
       (31, 1, 0.1, 1, 'ORDER', 2, 2),
       (32, 1, 0.1, 1, 'ORDER', 2, 2);

CREATE TABLE WALLET
(
    id          int NOT NULL,
    currency_id int NOT NULL,
    user_id     int NOT NULL
);

INSERT INTO WALLET (id, currency_id, user_id)
VALUES (1, 1, 9),
       (1, 1, 10),
       (1, 1, 11);

CREATE TABLE CURRENCY
(
    id   int NOT NULL,
    name varchar(45) DEFAULT NULL
);

INSERT INTO CURRENCY(id, name)
VALUES (4, 'BTC'),
       (5, 'LTC'),
       (2, 'USD');

CREATE TABLE OPERATION_TYPE
(
    id   int         NOT NULL,
    name varchar(45) NOT NULL
);

INSERT INTO OPERATION_TYPE(id, name)
VALUES (1, 'INPUT'),
       (2, 'OUTPUT'),
       (3, 'SELL'),
       (4, 'BUY'),
       (5, 'WALLET_INNER_TRANSFER'),
       (6, 'REFERRAL'),
       (8, 'MANUAL'),
       (9, 'USER_TRANSFER');

CREATE TABLE IF NOT EXISTS CURSORS
(
    table_name   varchar(255) NOT NULL,
    table_column varchar(255) DEFAULT 'id',
    last_id      int          DEFAULT 0,
    modified_at  timestamp    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (table_name)
);
