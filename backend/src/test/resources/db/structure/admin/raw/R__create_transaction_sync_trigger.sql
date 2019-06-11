DROP TRIGGER IF EXISTS PREPARE_AFTER_TRANSACTIONS;

DELIMITER $$

CREATE TRIGGER PREPARE_AFTER_TRANSACTIONS
    AFTER INSERT
    ON TRANSACTIONS
    FOR EACH ROW
BEGIN

    DECLARE v_amount_in_usd DECIMAL(20, 2);
    DECLARE v_btc_rate DECIMAL(20, 8);
    DECLARE v_commission_in_usd DECIMAL(20, 2);
    DECLARE v_created DATE;

    SET v_amount_in_usd = ABS(NEW.amount) * NEW.rate_in_usd;
    SET v_commission_in_usd = NEW.commission_amount * NEW.rate_in_usd;
    SET v_btc_rate = NEW.rate_btc_for_one_usd;
    SET v_created = DATE(NEW.datetime);

    IF (NEW.source_type = 'REFILL' AND NEW.operation_type = 'INPUT') THEN
        INSERT INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd, refill_amount_usd,
                                          inout_commission_usd, balance_dynamics_usd, source_ids)
            VALUE (v_created, NEW.user_id, NEW.rate_btc_for_one_usd, v_amount_in_usd, v_commission_in_usd,
                   v_amount_in_usd, NEW.id)
        ON DUPLICATE KEY UPDATE rate_btc_for_one_usd = v_btc_rate,
                                refill_amount_usd    = refill_amount_usd + v_amount_in_usd,
                                inout_commission_usd = inout_commission_usd + v_commission_in_usd,
                                balance_dynamics_usd = balance_dynamics_usd + v_amount_in_usd,
                                source_ids           = CONCAT(source_ids, ',', NEW.id);
    END IF;

    IF (NEW.source_type = 'WITHDRAW' AND NEW.operation_type = 'OUTPUT') THEN
        INSERT INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd, withdraw_amount_usd,
                                          inout_commission_usd, balance_dynamics_usd, source_ids)
            VALUE (v_created, NEW.user_id, NEW.rate_btc_for_one_usd, v_amount_in_usd, v_commission_in_usd,
                   v_amount_in_usd, NEW.id)
        ON DUPLICATE KEY UPDATE rate_btc_for_one_usd = v_btc_rate,
                                withdraw_amount_usd  = withdraw_amount_usd + v_amount_in_usd,
                                inout_commission_usd = inout_commission_usd + v_commission_in_usd,
                                balance_dynamics_usd = balance_dynamics_usd - v_amount_in_usd,
                                source_ids           = CONCAT(source_ids, ',', NEW.id);
    END IF;

    IF (NEW.source_type = 'USER_TRANSFER' AND NEW.operation_type = 'INPUT') THEN
        INSERT INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd, transfer_in_amount_usd,
                                          transfer_commission_usd, balance_dynamics_usd, source_ids)
            VALUE (v_created, NEW.user_id, NEW.rate_btc_for_one_usd, v_amount_in_usd, v_commission_in_usd,
                   v_amount_in_usd, NEW.id)
        ON DUPLICATE KEY UPDATE rate_btc_for_one_usd    = v_btc_rate,
                                transfer_in_amount_usd  = transfer_in_amount_usd + v_amount_in_usd,
                                transfer_commission_usd = transfer_commission_usd + v_commission_in_usd,
                                source_ids              = CONCAT(source_ids, ',', NEW.id),
                                balance_dynamics_usd    = balance_dynamics_usd + v_amount_in_usd;
    END IF;

    IF (NEW.source_type = 'USER_TRANSFER' AND NEW.operation_type = 'OUTPUT') THEN
        INSERT INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd, transfer_out_amount_usd,
                                          transfer_commission_usd, balance_dynamics_usd, source_ids)
            VALUE (v_created, NEW.user_id, NEW.rate_btc_for_one_usd, v_amount_in_usd, v_commission_in_usd,
                   v_amount_in_usd, NEW.id)
        ON DUPLICATE KEY UPDATE rate_btc_for_one_usd    = v_btc_rate,
                                transfer_out_amount_usd = transfer_out_amount_usd + v_amount_in_usd,
                                transfer_commission_usd = transfer_commission_usd + v_commission_in_usd,
                                source_ids              = CONCAT(source_ids, ',', NEW.id),
                                balance_dynamics_usd    = balance_dynamics_usd - v_amount_in_usd;
    END IF;

    IF (NEW.source_type = 'ORDER' AND NEW.operation_type = 'INPUT') THEN
        IF (SELECT EXISTS(SELECT *
                          FROM TRANSACTIONS t
                          WHERE t.source_id = 17 AND t.user_id = 1 AND t.operation_type = 'OUTPUT') > 0)
        THEN
            INSERT INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd, trade_amount_usd,
                                              trade_commission_usd, balance_dynamics_usd, source_ids)
                VALUE (v_created, NEW.user_id, NEW.rate_btc_for_one_usd, v_amount_in_usd, v_commission_in_usd,
                       v_amount_in_usd, NEW.id)
            ON DUPLICATE KEY UPDATE rate_btc_for_one_usd = v_btc_rate,
                                    trade_amount_usd     = trade_amount_usd + v_amount_in_usd,
                                    trade_commission_usd = trade_commission_usd + v_commission_in_usd,
                                    source_ids           = CONCAT(source_ids, ',', NEW.id),
                                    balance_dynamics_usd = balance_dynamics_usd + v_amount_in_usd;
        ELSE
            INSERT INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd, trade_amount_usd,
                                              trade_commission_usd, balance_dynamics_usd, source_ids, trade_buy_count)
                VALUE (v_created, NEW.user_id, NEW.rate_btc_for_one_usd, v_amount_in_usd, v_commission_in_usd,
                       v_amount_in_usd, NEW.id, 1)
            ON DUPLICATE KEY UPDATE rate_btc_for_one_usd = v_btc_rate,
                                    trade_amount_usd     = trade_amount_usd + v_amount_in_usd,
                                    trade_commission_usd = trade_commission_usd + v_commission_in_usd,
                                    source_ids           = CONCAT(source_ids, ',', NEW.id),
                                    balance_dynamics_usd = balance_dynamics_usd + v_amount_in_usd,
                                    trade_buy_count      = trade_buy_count + 1;
        END IF;
    END IF;

    IF (NEW.source_type = 'ORDER' AND NEW.operation_type = 'OUTPUT') THEN
        IF (SELECT EXISTS(SELECT *
                          FROM TRANSACTIONS t
                          WHERE t.source_id = 17 AND t.user_id = 1 AND t.operation_type = 'INPUT') > 0)
        THEN
            INSERT INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd, trade_amount_usd,
                                              trade_commission_usd, balance_dynamics_usd, source_ids)
                VALUE (v_created, NEW.user_id, NEW.rate_btc_for_one_usd, v_amount_in_usd, v_commission_in_usd,
                       v_amount_in_usd, NEW.id)
            ON DUPLICATE KEY UPDATE rate_btc_for_one_usd = v_btc_rate,
                                    trade_amount_usd     = trade_amount_usd + v_amount_in_usd,
                                    trade_commission_usd = trade_commission_usd + v_commission_in_usd,
                                    source_ids           = CONCAT(source_ids, ',', NEW.id),
                                    balance_dynamics_usd = balance_dynamics_usd - v_amount_in_usd;
        ELSE
            INSERT INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd, trade_amount_usd,
                                              trade_commission_usd, balance_dynamics_usd, source_ids, trade_sell_count)
                VALUE (v_created, NEW.user_id, NEW.rate_btc_for_one_usd, v_amount_in_usd, v_commission_in_usd,
                       v_amount_in_usd, NEW.id, 1)
            ON DUPLICATE KEY UPDATE rate_btc_for_one_usd = v_btc_rate,
                                    trade_amount_usd     = trade_amount_usd + v_amount_in_usd,
                                    trade_commission_usd = trade_commission_usd + v_commission_in_usd,
                                    source_ids           = CONCAT(source_ids, ',', NEW.id),
                                    balance_dynamics_usd = balance_dynamics_usd + v_amount_in_usd,
                                    trade_sell_count      = trade_sell_count + 1;
        END IF;
    END IF;

END $$

DELIMITER ;
