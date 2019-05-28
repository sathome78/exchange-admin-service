CREATE TABLE IF NOT EXISTS `CURSORS`
(
    `table_name`   varchar(255)          DEFAULT 'id',
    `table_column` varchar(255) NOT NULL,
    `last_id`      INTEGER      NOT NULL DEFAULT 0,
    `modified_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`table_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
