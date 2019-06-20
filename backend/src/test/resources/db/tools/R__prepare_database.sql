# SET FOREIGN_KEY_CHECKS=0;
#
# SELECT
#     Concat('TRUNCATE TABLE ', TABLE_NAME)
# FROM
#     INFORMATION_SCHEMA.TABLES
# WHERE
#         table_schema = 'admin_db_test';
#
# SET FOREIGN_KEY_CHECKS=1;

DROP DATABASE IF EXISTS admin_db_test;

CREATE DATABASE IF NOT EXISTS admin_db_test;

USE admin_db_test;

