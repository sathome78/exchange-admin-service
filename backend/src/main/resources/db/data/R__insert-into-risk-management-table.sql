INSERT IGNORE INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd,
                                  refill_amount_usd, withdraw_amount_usd, inout_commission_usd,
                                  transfer_in_amount_usd, transfer_out_amount_usd,
                                  transfer_commission_usd, trade_sell_count, trade_buy_count, trade_amount_usd,
                                  trade_commission_usd,
                                  balance_dynamics_usd, source_ids)
VALUES ('2019-03-03', 43242432, 0.0000015, 1, 1, 0.2, 1, 1, 0.2, 4, 4, 20, 1, -10, '1, 2, 3, 4'),
       ('2019-03-04', 43242432, 0.0000015, 1, 1, 0.2, 1, 1, 0.2, 4, 4, 20, 1, -10, '1, 2, 3, 4');
