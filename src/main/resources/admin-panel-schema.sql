CREATE TABLE IF NOT EXISTS admin_panel.CURRENT_CURRENCY_RATES
(
  id                          INT(40) UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  currency_id                 INT(40)                        NOT NULL UNIQUE,
  currency_name               VARCHAR (45)                   NOT NULL UNIQUE,
  usd_rate                    NUMERIC(19, 8)                          DEFAULT 0,
  btc_rate                    NUMERIC(19, 8)                          DEFAULT 0,
  schedule_last_updated_at    TIMESTAMP                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT IGNORE INTO admin_panel.CURRENT_CURRENCY_RATES (currency_id, currency_name)
SELECT cur.id, cur.name
FROM birzha.CURRENCY cur;


CREATE TABLE IF NOT EXISTS admin_panel.CURRENT_CURRENCY_BALANCES
(
  id                          INT(40) UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  currency_id                 INT(40)                        NOT NULL UNIQUE,
  currency_name               VARCHAR (45)                   NOT NULL UNIQUE,
  balance                     NUMERIC(30, 8)                          DEFAULT 0,
  last_updated_at             TIMESTAMP                      NULL     DEFAULT CURRENT_TIMESTAMP,
  schedule_last_updated_at    TIMESTAMP                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT IGNORE INTO admin_panel.CURRENT_CURRENCY_BALANCES (currency_id, currency_name)
SELECT cur.id, cur.name
FROM birzha.CURRENCY cur;


CREATE TABLE IF NOT EXISTS admin_panel.COMPANY_EXTERNAL_WALLET_BALANCES
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

INSERT IGNORE INTO admin_panel.COMPANY_EXTERNAL_WALLET_BALANCES (currency_id, currency_name)
SELECT cur.id, cur.name
FROM birzha.CURRENCY cur;


CREATE TABLE IF NOT EXISTS admin_panel.COMPANY_WALLET_EXTERNAL_RESERVED_ADDRESS
(
  id             INT UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  currency_id    INT(40)                    NOT NULL,
  name           VARCHAR(200)               NULL,
  wallet_address VARCHAR(128),
  balance        NUMERIC(30, 8)                      DEFAULT 0
);


CREATE TABLE IF NOT EXISTS admin_panel.INTERNAL_WALLET_BALANCES
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

INSERT IGNORE INTO admin_panel.INTERNAL_WALLET_BALANCES (currency_id, currency_name, role_id, role_name)
SELECT cur.id AS currency_id, cur.name AS currency_name, ur.id AS role_id, ur.name AS role_name
FROM birzha.CURRENCY cur CROSS JOIN birzha.USER_ROLE ur
ORDER BY cur.id, ur.id