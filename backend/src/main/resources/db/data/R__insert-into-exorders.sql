INSERT IGNORE INTO EXORDERS (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert,
                             user_acceptor_id, date_creation, date_acception, status_id, order_source_id,
                             counter_order_id)
VALUES (1, 43242432, 1, 3, 0.25, 1.24, 0.41, 432424, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1),
       (2, 43242432, 2, 3, 0.50, 1.33, 0.62, 432424, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1),
       (3, 43242432, 3, 4, 0.75, 1.24, 0.41, 432424, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1),
       (4, 43242432, 1, 4, 0.25, 2.55, 0.41, 432424, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1),
       (5, 43242432, 1, 3, 0.25, 11.04, 0.41, 432424, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1),
       (6, 43242432, 1, 3, 0.25, 100.24, 0.41, 432424, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1),
       (7, 43242432, 1, 4, 0.25, 10.24, 0.41, 432424, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 4, 1);
