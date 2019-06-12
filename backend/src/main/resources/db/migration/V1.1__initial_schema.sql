DROP TABLE IF EXISTS CURSORS;

CREATE TABLE IF NOT EXISTS CURSORS
(
    table_name   varchar(255) NOT NULL,
    table_column varchar(255) DEFAULT 'id',
    last_id      INTEGER      DEFAULT 0,
    modified_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (table_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS TRANSACTIONS;
CREATE TABLE IF NOT EXISTS TRANSACTIONS
(
    id                   INTEGER        NOT NULL,
    user_id              INTEGER        NOT NULL,
    currency_name        varchar(50)    NOT NULL,
    amount               DECIMAL(40, 9) NOT NULL,
    commission_amount    DECIMAL(40, 9) NOT NULL,
    source_type          varchar(255)   NOT NULL,
    operation_type       varchar(255)   NOT NULL,
    datetime             TIMESTAMP      NOT NULL,
    rate_in_usd          DECIMAL(20, 2) DEFAULT NULL,
    rate_in_btc          DECIMAL(40, 8) DEFAULT NULL,
    rate_btc_for_one_usd DECIMAL(18, 8) DEFAULT NULL,
    source_id            INTEGER        DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS USERS;
CREATE TABLE IF NOT EXISTS USERS
(
    user_id     INT          NOT NULL,
    pub_id      VARCHAR(50),
    email       VARCHAR(100) NOT NULL,
    password    VARCHAR(100) NOT NULL,
    regdate     TIMESTAMP    NOT NULL,
    phone       VARCHAR(100),
    user_status VARCHAR(100) NOT NULL,
    user_role   VARCHAR(100) NOT NULL,
    use2fa      BOOLEAN DEFAULT FALSE,
    kyc_status  VARCHAR(50),
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS CURRENT_CURRENCY_RATES
(
    id                       INT(40) UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    currency_id              INT(40)                      NOT NULL UNIQUE,
    currency_name            VARCHAR(45)                  NOT NULL UNIQUE,
    usd_rate                 NUMERIC(19, 8)                    DEFAULT 0,
    btc_rate                 NUMERIC(19, 8)                    DEFAULT 0,
    schedule_last_updated_at TIMESTAMP                    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS CURRENT_CURRENCY_BALANCES
(
    id                       INT(40) UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    currency_id              INT(40)                      NOT NULL UNIQUE,
    currency_name            VARCHAR(45)                  NOT NULL UNIQUE,
    balance                  NUMERIC(30, 8)                    DEFAULT 0,
    last_updated_at          TIMESTAMP                    NULL DEFAULT CURRENT_TIMESTAMP,
    schedule_last_updated_at TIMESTAMP                    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS COMPANY_EXTERNAL_WALLET_BALANCES
(
    id                   INT(40) UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    currency_id          INT(40)                      NOT NULL UNIQUE,
    currency_name        VARCHAR(45)                  NOT NULL UNIQUE,
    usd_rate             NUMERIC(19, 8)                        DEFAULT 0,
    btc_rate             NUMERIC(19, 8)                        DEFAULT 0,
    main_balance         NUMERIC(30, 8)                        DEFAULT 0,
    reserved_balance     NUMERIC(30, 8)                        DEFAULT 0,
    accounting_imbalance NUMERIC(30, 8)                        DEFAULT 0,
    total_balance        NUMERIC(30, 8)                        DEFAULT 0,
    total_balance_usd    NUMERIC(30, 8)                        DEFAULT 0,
    total_balance_btc    NUMERIC(30, 8)                        DEFAULT 0,
    last_updated_at      TIMESTAMP                    NULL     DEFAULT CURRENT_TIMESTAMP,
    sign_of_monitoring   TINYINT(1)                   NOT NULL DEFAULT 0,
    coin_range           NUMERIC(30, 8)                        DEFAULT 0,
    check_coin_range     TINYINT(1)                   NOT NULL DEFAULT 0,
    usd_range            NUMERIC(30, 8)                        DEFAULT 0,
    check_usd_range      TINYINT(1)                   NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS
(
    id             INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    currency_id    INT(40)                  NOT NULL,
    name           VARCHAR(200)             NULL,
    wallet_address VARCHAR(128),
    balance        NUMERIC(30, 8) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS INTERNAL_WALLET_BALANCES
(
    id                INT(40) UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    currency_id       INT(40)                      NOT NULL,
    currency_name     VARCHAR(45)                  NOT NULL,
    role_id           INT(40)                      NOT NULL,
    role_name         VARCHAR(45)                  NOT NULL,
    usd_rate          NUMERIC(19, 12)                   DEFAULT 0,
    btc_rate          NUMERIC(19, 12)                   DEFAULT 0,
    total_balance     NUMERIC(30, 8)                    DEFAULT 0,
    total_balance_usd NUMERIC(30, 8)                    DEFAULT 0,
    total_balance_btc NUMERIC(30, 8)                    DEFAULT 0,
    last_updated_at   TIMESTAMP                    NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY INTERNAL_WALLET_BALANCES_currency_id_role_id_uindex (currency_id, role_id)
);

CREATE TABLE IF NOT EXISTS CURRENCY_RATES_HISTORY
(
    id         INT(40) UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    content    BLOB                         NOT NULL,
    created_at TIMESTAMP                    NOT NULL
);

DROP TABLE IF EXISTS USER_ANNUAL_INSIGHTS;
CREATE TABLE IF NOT EXISTS USER_ANNUAL_INSIGHTS
(
    created                 DATE NOT NULL,
    user_id                 INT  NOT NULL,
    rate_btc_for_one_usd    DECIMAL(18, 8) DEFAULT 0,
    refill_amount_usd       DECIMAL(10, 2) DEFAULT 0,
    withdraw_amount_usd     DECIMAL(10, 2) DEFAULT 0,
    inout_commission_usd    DECIMAL(10, 2) DEFAULT 0,
    transfer_in_amount_usd  DECIMAL(10, 2) DEFAULT 0,
    transfer_out_amount_usd DECIMAL(10, 2) DEFAULT 0,
    transfer_commission_usd DECIMAL(10, 2) DEFAULT 0,
    trade_sell_count        INTEGER        DEFAULT 0,
    trade_buy_count         INTEGER        DEFAULT 0,
    trade_amount_usd        DECIMAL(10, 2) DEFAULT 0,
    trade_commission_usd    DECIMAL(10, 2) DEFAULT 0,
    balance_dynamics_usd    DECIMAL(10, 2) DEFAULT 0,
    source_ids              VARCHAR(255)   DEFAULT '',
    primary key (created, user_id)
) ENGINE InnoDB;
