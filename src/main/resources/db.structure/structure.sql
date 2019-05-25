CREATE TABLE IF NOT EXISTS `CURSORS`
(
    `id`      int(11)      NOT NULL AUTO_INCREMENT,
    `table_name`   varchar(255)  NOT NULL,
    `table_column`    varchar(255)  NOT NULL,
    `last_id` INTEGER DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
