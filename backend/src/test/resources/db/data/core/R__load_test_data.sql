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
       (5, 23, 1, 100000000, 0, 0);

INSERT IGNORE INTO COMPANY_WALLET (id, currency_id, balance, commission_balance)
VALUES (1, 4, 100000000, 100000000);

INSERT IGNORE INTO TRANSACTION (id, user_wallet_id, currency_id, amount, commission_amount, source_type,
                                operation_type_id, source_id)
VALUES (1, 1, 4, 1.0, 0.1, 'REFILL', 1, 1),
       (2, 2, 14, 10.0, 1, 'REFILL', 1, 2),
       (3, 2, 2, 1.0, 0.1, 'REFILL', 1, 3),
       (4, 5, 23, 10.0, 1, 'REFILL', 1, 4),
       (5, 1, 4, 1.0, 0.1, 'WITHDRAW', 2, 5),
       (6, 2, 14, 10.0, 1, 'WITHDRAW', 2, 6),
       (7, 2, 2, 1.0, 0.1, 'WITHDRAW', 2, 7),
       (8, 5, 23, 10.0, 1, 'WITHDRAW', 2, 8),
       (9, 1, 4, 1.0, 0.1, 'USER_TRANSFER', 2, 9),
       (10, 2, 14, 10.0, 1, 'USER_TRANSFER', 1, 10),
       (11, 2, 2, 1.0, 0.1, 'USER_TRANSFER', 2, 11),
       (12, 5, 23, 10.0, 1, 'USER_TRANSFER', 1, 12),
       (13, 1, 4, 1.0, 0.1, 'ORDER', 2, 13),
       (14, 2, 14, 10.0, 1, 'ORDER', 1, 14),
       (15, 2, 2, 1.0, 0.1, 'ORDER', 2, 15),
       (16, 5, 23, 10.0, 1, 'ORDER', 1, 16);

INSERT IGNORE INTO TRANSACTION_STATUS (id)
VALUES (1);

INSERT IGNORE INTO EXORDERS (id, user_id, status_id)
VALUES (1, 1, 3),
       (2, 2, 3);

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