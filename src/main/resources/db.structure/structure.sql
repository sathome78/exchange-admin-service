CREATE TABLE IF NOT EXISTS `CURSORS`
(
    `table_name`   varchar(255) DEFAULT 'id',
    `table_column` varchar(255) NOT NULL,
    `last_id`      LONG         DEFAULT 0,
    `modified_at`  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`table_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `TRANSACTION`
(
    `id`                INTEGER       NOT NULL,
    `user_id`           INTEGER       NOT NULL,
    `currency_name`     varchar(50)   NOT NULL,
    `amount`            DOUBLE(40, 9) NOT NULL,
    `commission_amount` DOUBLE(40, 9) NOT NULL,
    `source_type`       varchar(255)  NOT NULL,
    `operation_type`    varchar(255)  NOT NULL,
    `datetime`          TIMESTAMP     NOT NULL,
    `rate_in_usd`       DOUBLE(20, 2) DEFAULT NULL,
    `rate_in_btc`       DOUBLE(40, 8) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE INDEX idx_transaction_user_ops ON TRANSACTION (datetime, user_id, source_type, operation_type);
