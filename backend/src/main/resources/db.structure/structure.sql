CREATE TABLE IF NOT EXISTS CURSORS
(
    table_name   varchar(255) NOT NULL,
    table_column varchar(255) DEFAULT 'id',
    last_id      INTEGER         DEFAULT 0,
    modified_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (table_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS TRANSACTION
(
    id                INTEGER       NOT NULL,
    user_id           INTEGER       NOT NULL,
    currency_name     varchar(50)   NOT NULL,
    amount            DOUBLE(40, 9) NOT NULL,
    commission_amount DOUBLE(40, 9) NOT NULL,
    source_type       varchar(255)  NOT NULL,
    operation_type    varchar(255)  NOT NULL,
    datetime          TIMESTAMP     NOT NULL,
    rate_in_usd       DOUBLE(20, 2) DEFAULT NULL,
    rate_in_btc       DOUBLE(40, 8) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE INDEX idx_transaction_user_ops ON TRANSACTION (datetime, user_id, source_type, operation_type);

CREATE TABLE IF NOT EXISTS CURRENT_CURRENCY_RATES
(
    id                          INT(40) UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
    currency_id                 INT(40)                        NOT NULL UNIQUE,
    currency_name               VARCHAR (45)                   NOT NULL UNIQUE,
    usd_rate                    NUMERIC(19, 8)                          DEFAULT 0,
    btc_rate                    NUMERIC(19, 8)                          DEFAULT 0,
    schedule_last_updated_at    TIMESTAMP                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

# INSERT IGNORE INTO CURRENT_CURRENCY_RATES (currency_id, currency_name)
# SELECT cur.id, cur.name
# FROM birzha.CURRENCY cur;


CREATE TABLE IF NOT EXISTS CURRENT_CURRENCY_BALANCES
(
    id                          INT(40) UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
    currency_id                 INT(40)                        NOT NULL UNIQUE,
    currency_name               VARCHAR (45)                   NOT NULL UNIQUE,
    balance                     NUMERIC(30, 8)                          DEFAULT 0,
    last_updated_at             TIMESTAMP                      NULL     DEFAULT CURRENT_TIMESTAMP,
    schedule_last_updated_at    TIMESTAMP                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

# INSERT IGNORE INTO CURRENT_CURRENCY_BALANCES (currency_id, currency_name)
# SELECT cur.id, cur.name
# FROM birzha.CURRENCY cur;


CREATE TABLE IF NOT EXISTS COMPANY_EXTERNAL_WALLET_BALANCES
(
    id                INT(40) UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
    currency_id       INT(40)                        NOT NULL UNIQUE,
    currency_name     VARCHAR (45)                   NOT NULL UNIQUE,
    usd_rate          NUMERIC(19, 8)                          DEFAULT 0,
    btc_rate          NUMERIC(19, 8)                          DEFAULT 0,
    main_balance      NUMERIC(30, 8)                          DEFAULT 0,
    reserved_balance  NUMERIC(30, 8)                          DEFAULT 0,
    total_balance     NUMERIC(30, 8)                          DEFAULT 0,
    total_balance_usd NUMERIC(30, 8)                          DEFAULT 0,
    total_balance_btc NUMERIC(30, 8)                          DEFAULT 0,
    last_updated_at   TIMESTAMP                      NULL     DEFAULT CURRENT_TIMESTAMP
);

# INSERT IGNORE INTO COMPANY_EXTERNAL_WALLET_BALANCES (currency_id, currency_name)
# SELECT cur.id, cur.name
# FROM birzha.CURRENCY cur;


CREATE TABLE IF NOT EXISTS COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS
(
    id             INT UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
    currency_id    INT(40)                    NOT NULL,
    name           VARCHAR(200)               NULL,
    wallet_address VARCHAR(128),
    balance        NUMERIC(30, 8)                      DEFAULT 0
);


CREATE TABLE IF NOT EXISTS INTERNAL_WALLET_BALANCES
(
    id                INT(40) UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
    currency_id       INT(40)                        NOT NULL,
    currency_name     VARCHAR (45)                   NOT NULL,
    role_id           INT(40)                        NOT NULL,
    role_name         VARCHAR (45)                   NOT NULL,
    usd_rate          NUMERIC(19, 12)                           DEFAULT 0,
    btc_rate          NUMERIC(19, 12)                           DEFAULT 0,
    total_balance     NUMERIC(30, 8)                            DEFAULT 0,
    total_balance_usd NUMERIC(30, 8)                            DEFAULT 0,
    total_balance_btc NUMERIC(30, 8)                            DEFAULT 0,
    last_updated_at   TIMESTAMP                      NULL       DEFAULT CURRENT_TIMESTAMP
);

# INSERT IGNORE INTO INTERNAL_WALLET_BALANCES (currency_id, currency_name, role_id, role_name)
# SELECT cur.id AS currency_id, cur.name AS currency_name, ur.id AS role_id, ur.name AS role_name
# FROM birzha.CURRENCY cur CROSS JOIN birzha.USER_ROLE ur
# ORDER BY cur.id, ur.id