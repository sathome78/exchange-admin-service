SET FOREIGN_KEY_CHECKS = 0;

INSERT IGNORE INTO USER_ROLE (id, name, user_role_business_feature_id, user_role_group_feature_id,
                              user_role_report_group_feature_id)
VALUES (1, 'ADMINISTRATOR', 1, 1, 1),
       (2, 'ACCOUNTANT', 1, 1, 1),
       (3, 'ADMIN_USER', 1, 1, 1),
       (4, 'USER', 2, 2, 2),
       (5, 'ROLE_CHANGE_PASSWORD', null, 2, null),
       (6, 'EXCHANGE', 3, 2, 2),
       (7, 'VIP_USER', 4, 2, 2),
       (8, 'TRADER', 5, 2, 3),
       (9, 'FIN_OPERATOR', 1, 1, 1),
       (10, 'BOT_TRADER', 6, 3, 4),
       (11, 'ICO_MARKET_MAKER', 7, 2, 2),
       (12, 'OUTER_MARKET_BOT', 9, 3, 4);

INSERT IGNORE INTO USER_STATUS (id, name, description)
VALUES (1, 'registered', 'without email confirmation'),
       (2, 'activated', 'with email confirmation'),
       (3, 'blocked', 'blocked by admin'),
       (4, 'banned_in_chat', 'banned in chat by admin');

INSERT IGNORE INTO USER (id, pub_id, nickname, email, password, regdate, phone, finpassword, status, ipaddress, roleid,
                         preferred_lang, avatar_path, tmp_poll_passed, login_pin, use2fa, 2fa_last_notify_date,
                         withdraw_pin, transfer_pin, temporary_banned, change_2fa_setting_pin, api_token_setting_pin,
                         GA, kyc_verification_step, kyc_status, kyc_reference, country, firstName, lastName, birthDay)
    VALUE
    (1, 'HJGFJHFGSDASDGFSD', 'admin', 'admin@exrates.me',
     '$2a$10$ywda3/fTYHWR6E9e9KXUj.5tB3xFO1jdIBJs3BLpO6ORRSJZMg3v.', '2019-04-16 10:05:29', '+380672223344', null, 3,
     '127.0.0.1', 1, 'en', null, 1, null, 0, null, null, null, 0,
     '$2a$10$oyFeDvhk55fpvUKxvtcsI.zrUC669qcnN1iB4QMjY2obPgxLSQOR.', null, 'GA1.2.1627413492.1555445430', 0, 'SUCCESS',
     'none', 'UK', 'Charles', 'James', null),
    (2, 'SIAAWWASADDWDDWAW', 'admin1', 'admin1@exrates.me',
     '$2a$10$ywda3/fTYHWR6E9e9KXUj.5tB3xFO1jdIBJs3BLpO6ORRSJZMg3v.', '2019-04-16 10:05:29', '+380672223344', null, 2,
     '127.0.0.1', 1, 'en', null, 1, null, 0, null, null, null, 0,
     '$2a$10$oyFeDvhk55fpvUKxvtcsI.zrUC669qcnN1iB4QMjY2obPgxLSQOR.', null, 'GA1.2.1627413492.1555445430', 0, 'SUCCESS',
     'none', 'UK', 'Charles', 'James', null),
    (3, 'JHGGGJHGJHGHGJGJG', 'user', 'user@exrates.me', '$2a$10$ywda3/fTYHWR6E9e9KXUj.5tB3xFO1jdIBJs3BLpO6ORRSJZMg3v.',
     '2019-04-16 10:05:29', '+380672223344', null, 2, '127.0.0.1', 4, 'en', null, 1, null, 0, null, null, null, 0,
     '$2a$10$oyFeDvhk55fpvUKxvtcsI.zrUC669qcnN1iB4QMjY2obPgxLSQOR.', null, 'GA1.2.1627413492.1555445430', 0, 'none',
     'none', null, null, null, null),
    (4, 'SIAAWWASADDTRYYWAW', 'bot1', 'bot1@exrates.me',
     '$2a$10$ywda3/fTYHWR6E9e9KXUj.5tB3xFO1jdIBJs3BLpO6ORRSJZMg3v.', '2019-04-16 10:05:29', '+380672223344', null, 2,
     '127.0.0.1', 10, 'en', null, 1, null, 0, null, null, null, 0,
     '$2a$10$oyFeDvhk55fpvUKxvtcsI.zrUC669qcnN1iB4QMjY2obPgxLSQOR.', null, 'GA1.2.1627413492.1555445430', 0, 'SUCCESS',
     'none', 'UK', 'Charles', 'James', null),
    (5, 'JHGGGJHGJHGTRYJGJG', 'bot2', 'bot2@exrates.me', '$2a$10$ywda3/fTYHWR6E9e9KXUj.5tB3xFO1jdIBJs3BLpO6ORRSJZMg3v.',
     '2019-04-16 10:05:29', '+380672223344', null, 2, '127.0.0.1', 12, 'en', null, 1, null, 0, null, null, null, 0,
     '$2a$10$oyFeDvhk55fpvUKxvtcsI.zrUC669qcnN1iB4QMjY2obPgxLSQOR.', null, 'GA1.2.1627413492.1555445430', 0, 'none',
     'none', null, null, null, null);

INSERT IGNORE INTO OPERATION_TYPE (id, name, description)
VALUES (1, 'Input', null),
       (2, 'Output', null),
       (3, 'sell', null),
       (4, 'buy', null),
       (5, 'wallet_inner_transfer', 'between active and reserved balance'),
       (6, 'referral', null),
       (7, 'storno', 'for storno operation'),
       (8, 'manual', null),
       (9, 'user_transfer', null);

INSERT IGNORE INTO CURRENCY (id, name, description, hidden, max_scale_for_refill, max_scale_for_withdraw,
                             max_scale_for_transfer, process_type, scale)
VALUES (4, 'BTC', 'Bitcoin', 0, 8, 8, 8, 'CRYPTO', 8),
       (14, 'ETH', 'Ethereum', 0, 8, 8, 8, 'CRYPTO', 8),
       (5, 'LTC', 'Litecoin', 1, 8, 8, 8, 'CRYPTO', 8),
       (2, 'USD', 'US Dollar', 0, 2, 2, 2, 'FIAT', 2),
       (23, 'XLM', 'Stellar Lumens', 0, 6, 6, 6, 'CRYPTO', 8);

INSERT IGNORE INTO WALLET (id, currency_id, user_id, active_balance, reserved_balance, ieo_reserve)
VALUES (1, 4, 1, 100000000, 0, 0),
       (2, 14, 1, 100000000, 0, 0),
       (3, 5, 1, 100000000, 0, 0),
       (4, 2, 1, 100000000, 0, 0),
       (5, 23, 1, 100000000, 0, 0),
       (6, 4, 4, 100000000, 0, 0),
       (7, 14, 4, 100000000, 0, 0),
       (8, 5, 4, 100000000, 0, 0),
       (9, 2, 4, 100000000, 0, 0),
       (10, 23, 4, 100000000, 0, 0),
       (11, 4, 5, 100000000, 0, 0),
       (12, 14, 5, 100000000, 0, 0),
       (13, 5, 5, 100000000, 0, 0),
       (14, 2, 5, 100000000, 0, 0),
       (15, 23, 5, 100000000, 0, 0);

INSERT IGNORE INTO COMPANY_WALLET (id, currency_id, balance, commission_balance)
VALUES (1, 4, 100000000, 100000000);

INSERT IGNORE INTO TRANSACTION (id, user_wallet_id, currency_id, active_balance_before, amount, commission_amount, source_type,
                                operation_type_id, source_id)
VALUES (1, 1, 4, 10, 1.0, 0.1, 'REFILL', 1, 1),
       (2, 2, 14, 11, 10.0, 1, 'REFILL', 1, 2),
       (3, 2, 2, 9, 1.0, 0.1, 'REFILL', 1, 3),
       (4, 5, 23, 10, 10.0, 1, 'REFILL', 1, 4),
       (5, 1, 4, 12, 1.0, 0.1, 'WITHDRAW', 2, 5),
       (6, 2, 14, 0, 10.0, 1, 'WITHDRAW', 2, 6),
       (7, 2, 2, 0, 1.0, 0.1, 'WITHDRAW', 2, 7),
       (8, 5, 23, 0, 10.0, 1, 'WITHDRAW', 2, 8),
       (9, 1, 4, 10, 1.0, 0.1, 'USER_TRANSFER', 2, 9),
       (10, 2, 14, 13, 10.0, 1, 'USER_TRANSFER', 1, 10),
       (11, 2, 2, 5, 1.0, 0.1, 'USER_TRANSFER', 2, 11),
       (12, 5, 23, 5, 10.0, 1, 'USER_TRANSFER', 1, 12),
       (13, 1, 4, 10, 1.0, 0.1, 'ORDER', 2, 13),
       (14, 2, 14, 3, 10.0, 1, 'ORDER', 1, 14),
       (15, 2, 2, 66, 1.0, 0.1, 'ORDER', 2, 15),
       (16, 5, 23, 77, 10.0, 1, 'ORDER', 1, 16);

INSERT IGNORE INTO CURRENCY_PAIR (id, currency1_id, currency2_id, name, pair_order, hidden, market, ticker_name,
                           permitted_link, type, scale)
VALUES (1, 4, 2, 'BTC/USD', 13, 0, 'USD', 'BTC/USD', 0, 'MAIN', 2),
       (2, 4, 3, 'BTC/EUR', 201, 0, 'FIAT', 'BTC/EUR', 0, 'MAIN', 2),
       (3, 4, 1, 'BTC/RUB', 15, 1, 'FIAT', 'BTC/RUB', 0, 'MAIN', 2),
       (4, 5, 4, 'LTC/BTC', 219, 0, 'BTC', 'LTC/BTC', 0, 'MAIN', 8),
       (5, 5, 2, 'LTC/USD', 220, 0, 'USD', 'LTC/USD', 0, 'MAIN', 2);

INSERT IGNORE INTO EXORDERS (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert,
                             user_acceptor_id, date_creation, date_acception, status_id, order_source_id, counter_order_id)
VALUES (1, 1, 1, 3, 0.25, 1.24, 0.41, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1),
       (2, 1, 2, 3, 0.50, 1.33, 0.62, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1),
       (3, 1, 3, 4, 0.75, 1.24, 0.41, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1),
       (4, 1, 1, 4, 0.25, 2.55, 0.41, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1),
       (5, 1, 1, 3, 0.25, 11.04, 0.41, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1),
       (6, 1, 1, 3, 0.25, 100.24, 0.41, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1),
       (7, 1, 1, 4, 0.25, 10.24, 0.41, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1);

TRUNCATE TABLE REFILL_REQUEST;
INSERT INTO REFILL_REQUEST (id, amount, date_creation, status_id, currency_id, user_id,
                            commission_id, merchant_id, merchant_transaction_id, refill_request_param_id,
                            refill_request_address_id)
VALUES (1, 1.000000000, CURRENT_TIMESTAMP - INTERVAL 88 DAY, 12, 2, 1, 15, 15, null, null, null),
       (2, 1.000000000, CURRENT_TIMESTAMP - INTERVAL 28 DAY, 12, 5, 1, 15, 15, null, null, null),
       (3, 1.000000000, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 12, 4, 1, 15, 15, null, null, null),
       (4, 1.000000000, CURRENT_TIMESTAMP - INTERVAL 1 DAY, 12, 2, 1, 15, 15, null, null, null),
       (5, 1.000000000, CURRENT_TIMESTAMP - INTERVAL 88 DAY, 12, 2, 2, 15, 15, null, null, null),
       (6, 1.000000000, CURRENT_TIMESTAMP - INTERVAL 28 DAY, 12, 5, 2, 15, 15, null, null, null),
       (7, 1.000000000, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 12, 4, 2, 15, 15, null, null, null),
       (8, 1.000000000, CURRENT_TIMESTAMP - INTERVAL 1 DAY, 12, 2, 2, 15, 15, null, null, null);

REPLACE INTO REFILL_REQUEST_ADDRESS (id, currency_id, merchant_id, address, user_id, date_generation)
VALUES (1, 2, 0, 'HASH1', 1, CURRENT_TIMESTAMP - INTERVAL 92 DAY),
       (2, 4, 0, 'HASH2', 1, CURRENT_TIMESTAMP - INTERVAL 92 DAY),
       (3, 14, 0, 'HASH3', 1, CURRENT_TIMESTAMP - INTERVAL 92 DAY),
       (4, 2, 0, 'HASH4', 1, CURRENT_TIMESTAMP - INTERVAL 85 DAY),
       (5, 4, 0, 'HASH5', 1, CURRENT_TIMESTAMP - INTERVAL 85 DAY),
       (6, 14, 0, 'HASH6', 1, CURRENT_TIMESTAMP - INTERVAL 85 DAY),
       (7, 2, 0, 'HASH7', 1, CURRENT_TIMESTAMP - INTERVAL 25 DAY),
       (8, 4, 0, 'HASH8', 1, CURRENT_TIMESTAMP - INTERVAL 25 DAY),
       (9, 14, 0, 'HASH9', 1, CURRENT_TIMESTAMP - INTERVAL 25 DAY),
       (10, 2, 0, 'HASH10', 1, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
       (11, 4, 0, 'HASH11', 1, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
       (12, 14, 0, 'HASH12', 1, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
       (13, 2, 0, 'HASH13', 1, CURRENT_TIMESTAMP - INTERVAL 30 HOUR),
       (14, 4, 0, 'HASH14', 1, CURRENT_TIMESTAMP - INTERVAL 30 HOUR),
       (15, 4, 0, 'HASH15', 1, CURRENT_TIMESTAMP - INTERVAL 30 HOUR),
       (16, 2, 0, 'HASH16', 2, CURRENT_TIMESTAMP - INTERVAL 92 DAY),
       (17, 4, 0, 'HASH17', 2, CURRENT_TIMESTAMP - INTERVAL 92 DAY),
       (18, 14, 0, 'HASH18', 2, CURRENT_TIMESTAMP - INTERVAL 92 DAY),
       (19, 2, 0, 'HASH19', 2, CURRENT_TIMESTAMP - INTERVAL 85 DAY),
       (20, 4, 0, 'HASH20', 2, CURRENT_TIMESTAMP - INTERVAL 85 DAY),
       (21, 14, 0, 'HASH21', 2, CURRENT_TIMESTAMP - INTERVAL 85 DAY),
       (22, 2, 0, 'HASH22', 2, CURRENT_TIMESTAMP - INTERVAL 25 DAY),
       (23, 4, 0, 'HASH23', 2, CURRENT_TIMESTAMP - INTERVAL 25 DAY),
       (24, 14, 0, 'HASH24', 2, CURRENT_TIMESTAMP - INTERVAL 25 DAY),
       (25, 2, 0, 'HASH25', 2, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
       (26, 4, 0, 'HASH26', 2, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
       (27, 14, 0, 'HASH27', 2, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
       (28, 2, 0, 'HASH28', 2, CURRENT_TIMESTAMP - INTERVAL 30 HOUR),
       (29, 4, 0, 'HASH29', 2, CURRENT_TIMESTAMP - INTERVAL 30 HOUR),
       (30, 4, 0, 'HASH30', 2, CURRENT_TIMESTAMP - INTERVAL 30 HOUR),
       (31, 2, 0, 'HASH31', 3, CURRENT_TIMESTAMP - INTERVAL 92 DAY),
       (32, 4, 0, 'HASH32', 3, CURRENT_TIMESTAMP - INTERVAL 92 DAY),
       (33, 14, 0, 'HASH33', 3, CURRENT_TIMESTAMP - INTERVAL 92 DAY),
       (34, 2, 0, 'HASH34', 3, CURRENT_TIMESTAMP - INTERVAL 85 DAY),
       (35, 4, 0, 'HASH35', 3, CURRENT_TIMESTAMP - INTERVAL 85 DAY),
       (36, 14, 0, 'HASH36', 3, CURRENT_TIMESTAMP - INTERVAL 85 DAY),
       (37, 2, 0, 'HASH37', 3, CURRENT_TIMESTAMP - INTERVAL 25 DAY),
       (38, 4, 0, 'HASH38', 3, CURRENT_TIMESTAMP - INTERVAL 25 DAY),
       (39, 14, 0, 'HASH39', 3, CURRENT_TIMESTAMP - INTERVAL 25 DAY),
       (40, 2, 0, 'HASH40', 3, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
       (41, 4, 0, 'HASH41', 3, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
       (42, 14, 0, 'HASH42', 3, CURRENT_TIMESTAMP - INTERVAL 6 DAY),
       (43, 2, 0, 'HASH43', 3, CURRENT_TIMESTAMP - INTERVAL 30 HOUR),
       (44, 4, 0, 'HASH44', 3, CURRENT_TIMESTAMP - INTERVAL 30 HOUR),
       (45, 4, 0, 'HASH45', 3, CURRENT_TIMESTAMP - INTERVAL 30 HOUR);


REPLACE INTO refill_request (id, amount, date_creation, status_id, user_id, refill_request_address_id)
VALUES (1, 10, CURRENT_TIMESTAMP - INTERVAL 92 DAY, 9, 1, 1),
       (2, 10, CURRENT_TIMESTAMP - INTERVAL 92 DAY, 7, 7, 2),
       (3, 10, CURRENT_TIMESTAMP - INTERVAL 92 DAY, 4, 1, null),
       (4, 10, CURRENT_TIMESTAMP - INTERVAL 85 DAY, 9, 1, 4),
       (5, 10, CURRENT_TIMESTAMP - INTERVAL 92 DAY, 7, 7, 5),
       (6, 10, CURRENT_TIMESTAMP - INTERVAL 85 DAY, 4, 1, null),
       (7, 10, CURRENT_TIMESTAMP - INTERVAL 25 DAY, 9, 1, 7),
       (8, 10, CURRENT_TIMESTAMP - INTERVAL 25 DAY, 7, 7, 8),
       (9, 10, CURRENT_TIMESTAMP - INTERVAL 25 DAY, 4, 1, null),
       (10, 10, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 9, 1, 10),
       (11, 10, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 7, 7, 11),
       (12, 10, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 4, 1, null),
       (13, 10, CURRENT_TIMESTAMP - INTERVAL 30 HOUR, 9, 1, 13),
       (14, 10, CURRENT_TIMESTAMP - INTERVAL 30 HOUR, 7, 1, 14),
       (15, 10, CURRENT_TIMESTAMP - INTERVAL 30 HOUR, 4, 1, null),
       (16, 10, CURRENT_TIMESTAMP - INTERVAL 92 DAY, 9, 2, 16),
       (17, 10, CURRENT_TIMESTAMP - INTERVAL 92 DAY, 7, 2, 17),
       (18, 10, CURRENT_TIMESTAMP - INTERVAL 92 DAY, 4, 2, null),
       (19, 10, CURRENT_TIMESTAMP - INTERVAL 85 DAY, 9, 2, 19),
       (20, 10, CURRENT_TIMESTAMP - INTERVAL 92 DAY, 7, 2, 20),
       (21, 10, CURRENT_TIMESTAMP - INTERVAL 85 DAY, 4, 2, null),
       (22, 10, CURRENT_TIMESTAMP - INTERVAL 25 DAY, 9, 2, 22),
       (23, 10, CURRENT_TIMESTAMP - INTERVAL 25 DAY, 7, 2, 23),
       (24, 10, CURRENT_TIMESTAMP - INTERVAL 25 DAY, 4, 2, null),
       (25, 10, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 9, 2, 25),
       (26, 10, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 7, 2, 26),
       (27, 10, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 4, 2, null),
       (28, 10, CURRENT_TIMESTAMP - INTERVAL 30 HOUR, 9, 2, 28),
       (29, 10, CURRENT_TIMESTAMP - INTERVAL 30 HOUR, 7, 2, 29),
       (30, 10, CURRENT_TIMESTAMP - INTERVAL 30 HOUR, 4, 2, null),
       (31, 10, CURRENT_TIMESTAMP - INTERVAL 92 DAY, 9, 3, 31),
       (32, 10, CURRENT_TIMESTAMP - INTERVAL 92 DAY, 7, 3, 32),
       (33, 10, CURRENT_TIMESTAMP - INTERVAL 92 DAY, 4, 3, null),
       (34, 10, CURRENT_TIMESTAMP - INTERVAL 85 DAY, 9, 3, 34),
       (35, 10, CURRENT_TIMESTAMP - INTERVAL 92 DAY, 7, 3, 35),
       (36, 10, CURRENT_TIMESTAMP - INTERVAL 85 DAY, 4, 3, null),
       (37, 10, CURRENT_TIMESTAMP - INTERVAL 25 DAY, 9, 3, 37),
       (38, 10, CURRENT_TIMESTAMP - INTERVAL 25 DAY, 7, 3, 38),
       (39, 10, CURRENT_TIMESTAMP - INTERVAL 25 DAY, 4, 3, null),
       (40, 10, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 9, 3, 40),
       (41, 10, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 7, 3, 41),
       (42, 10, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 4, 3, null),
       (43, 10, CURRENT_TIMESTAMP - INTERVAL 30 HOUR, 9, 3, 43),
       (44, 10, CURRENT_TIMESTAMP - INTERVAL 30 HOUR, 7, 3, 44),
       (45, 10, CURRENT_TIMESTAMP - INTERVAL 30 HOUR, 4, 3, null);

INSERT INTO IP_Log (ip, user_id, date, event)
VALUES ('123.12.12.12', 1, CURRENT_TIMESTAMP - INTERVAL 3 DAY, 'LOGIN_SUCCESS'),
       ('123.12.12.12', 1, CURRENT_TIMESTAMP - INTERVAL 1 DAY, 'LOGIN_SUCCESS'),
       ('123.12.12.12', 1, CURRENT_TIMESTAMP - INTERVAL 1 HOUR, 'WITHDRAW'),
       ('123.12.12.12', 2, CURRENT_TIMESTAMP - INTERVAL 2 DAY, 'LOGIN_SUCCESS'),
       ('123.12.12.12', 2, CURRENT_TIMESTAMP - INTERVAL 1 DAY, 'WITHDRAW'),
       ('123.12.12.12', 2, CURRENT_TIMESTAMP - INTERVAL 1 HOUR, 'LOGIN_SUCCESS');

INSERT IGNORE INTO IP_Log (id, user_id, date, event)
VALUES (1, 1, CURRENT_TIMESTAMP, 'LOGIN_SUCCESS');

INSERT IGNORE REFERRAL_LEVEL (id, level, percent)
VALUES (1, 1, 10);

INSERT IGNORE REFERRAL_TRANSACTION (id, order_id, referral_level_id, user_id, initiator_id, status)
VALUES (1, 1, 1, 1, 2, 'PAYED');

INSERT IGNORE INTO TRANSACTION_STATUS (id)
VALUES (1);

INSERT IGNORE INTO COMMISSION (id, operation_type, user_role)
VALUES (1, 1, 1);

INSERT IGNORE INTO USER_ADMIN_AUTHORITY_ROLE_APPLICATION (user_id, admin_authority_id, applied_to_role_id)
VALUES (1, 8, 1);

INSERT IGNORE INTO CURRENT_CURRENCY_RATES (currency_id, currency_name)
SELECT cur.id, cur.name
FROM CURRENCY cur;

INSERT IGNORE INTO CURRENT_CURRENCY_BALANCES (currency_id, currency_name)
SELECT cur.id, cur.name
FROM CURRENCY cur;