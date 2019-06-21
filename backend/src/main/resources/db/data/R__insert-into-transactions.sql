INSERT INTO TRANSACTIONS (id, user_id, currency_name, amount, commission_amount, source_type, operation_type, datetime,
                          rate_in_usd, rate_in_btc, rate_btc_for_one_usd, source_id)
VALUES (1, 43242432, 'USD', 10, 1, 'ORDER', 'OUTPUT', CURRENT_TIMESTAMP, 10, 0.00015, 0.000015, 1),
       (2, 43242432, 'USD', 10, 1, 'ORDER', 'OUTPUT', CURRENT_TIMESTAMP, 10, 0.00015, 0.000015, 2),
       (3, 43242432, 'USD', 10, 1, 'ORDER', 'INPUT', CURRENT_TIMESTAMP, 10, 0.00015, 0.000015, 3),
       (4, 43242432, 'USD', 10, 1, 'ORDER', 'INPUT', CURRENT_TIMESTAMP, 10, 0.00015, 0.000015, 4),
       (5, 43242432, 'USD', 10, 1, 'ORDER', 'OUTPUT', CURRENT_TIMESTAMP, 10, 0.00015, 0.000015, 5),
       (6, 43242432, 'USD', 10, 1, 'ORDER', 'OUTPUT', CURRENT_TIMESTAMP, 10, 0.00015, 0.000015, 6),
       (7, 43242432, 'BTC', 1, 0.1, 'ORDER', 'INPUT', CURRENT_TIMESTAMP, 8500, 1, 0.000015, 7),
       (8, 43242432, 'BTC', 1, 0.1, 'ORDER', 'OUTPUT', CURRENT_TIMESTAMP, 8500, 1, 0.000015, 8),
       (9, 43242432, 'BTC', 1, 0.1, 'ORDER', 'INPUT', CURRENT_TIMESTAMP, 8500, 1, 0.000015, 9),
       (10, 43242432, 'BTC', 1, 0.1, 'ORDER', 'INPUT', CURRENT_TIMESTAMP, 8500, 1, 0.000015, 10),
       (11, 43242432, 'ETH', 50, 5, 'ORDER', 'OUTPUT', CURRENT_TIMESTAMP, 150, 0.00011, 0.000015, 11),
       (12, 43242432, 'ETH', 50, 5, 'ORDER', 'INPUT', CURRENT_TIMESTAMP, 150, 0.00011, 0.000015, 12);