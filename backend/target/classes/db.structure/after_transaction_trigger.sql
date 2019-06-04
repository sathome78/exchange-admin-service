DELIMITER ||
CREATE TRIGGER PREPARE_AFTER_TRANSACTIONS
    AFTER INSERT
    ON TRANSACTIONS
    FOR EACH ROW
BEGIN

    DECLARE v_amount_in_usd DECIMAL(20, 2);
    DECLARE v_btc_rate DECIMAL(20, 8);
    DECLARE v_commission_in_usd DECIMAL(20, 2);
    DECLARE v_created DATE;

    SET v_amount_in_usd = NEW.amount * NEW.rate_in_usd;
    SET v_commission_in_usd = NEW.commission_amount * NEW.rate_in_usd;
    SET v_btc_rate = NEW.rate_btc_for_one_usd;
    SET v_created = DATE(NEW.datetime);

    IF (NEW.source_type = 'REFILL' AND NEW.operation_type = 'INPUT') THEN
        INSERT INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd, refill_amount_usd,
                                          inout_commission_usd, balance_dynamics_usd, last_updated_id)
            VALUE (v_created, NEW.user_id, NEW.rate_btc_for_one_usd, v_amount_in_usd, v_commission_in_usd,
                   v_amount_in_usd, NEW.id)
        ON DUPLICATE KEY UPDATE rate_btc_for_one_usd = v_btc_rate,
                                refill_amount_usd    = refill_amount_usd + v_amount_in_usd,
                                inout_commission_usd = inout_commission_usd + v_commission_in_usd,
                                balance_dynamics_usd = balance_dynamics_usd + v_amount_in_usd,
                                last_updated_id      = NEW.id;
    END IF;

    IF (NEW.source_type = 'WITHDRAW' AND NEW.operation_type = 'OUTPUT') THEN
        INSERT INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd, withdraw_amount_usd,
                                          inout_commission_usd, balance_dynamics_usd, last_updated_id)
            VALUE (v_created, NEW.user_id, NEW.rate_btc_for_one_usd, v_amount_in_usd, v_commission_in_usd,
                   v_amount_in_usd, NEW.id)
        ON DUPLICATE KEY UPDATE rate_btc_for_one_usd = v_btc_rate,
                                withdraw_amount_usd  = withdraw_amount_usd + v_amount_in_usd,
                                inout_commission_usd = inout_commission_usd + v_commission_in_usd,
                                balance_dynamics_usd = balance_dynamics_usd + v_amount_in_usd,
                                last_updated_id      = NEW.id;
    END IF;

    IF (NEW.source_type = 'USER_TRANSFER' AND NEW.operation_type IN ('INPUT', 'OUTPUT')) THEN
        INSERT INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd, transfer_amount_usd,
                                          transfer_commission_usd, balance_dynamics_usd, last_updated_id)
            VALUE (v_created, NEW.user_id, NEW.rate_btc_for_one_usd, v_amount_in_usd, v_commission_in_usd,
                   v_amount_in_usd, NEW.id)
        ON DUPLICATE KEY UPDATE rate_btc_for_one_usd    = v_btc_rate,
                                transfer_amount_usd     = transfer_amount_usd + v_amount_in_usd,
                                transfer_commission_usd = transfer_commission_usd + v_commission_in_usd,
                                balance_dynamics_usd    = balance_dynamics_usd + v_amount_in_usd,
                                last_updated_id         = NEW.id;
    END IF;

    IF (NEW.source_type = 'ORDER' AND NEW.operation_type IN ('INPUT', 'OUTPUT')) THEN
        IF ((SELECT COUNT(source_id)
             FROM USER_ANNUAL_INSIGHTS uai
             WHERE uai.source_id = NEW.source_id
               AND uai.user_id = NEW.user_id) > 0)
        THEN
            UPDATE USER_ANNUAL_INSIGHTS
            SET trade_commission_usd = trade_commission_usd + v_commission_in_usd,
                last_updated_id      = NEW.id
            WHERE user_id = NEW.id
              AND source_id = NEW.source_id;
        ELSE
            INSERT INTO USER_ANNUAL_INSIGHTS (created, user_id, rate_btc_for_one_usd, withdraw_amount_usd,
                                              inout_commission_usd, balance_dynamics_usd, last_updated_id)
                VALUE (v_created, NEW.user_id, NEW.rate_btc_for_one_usd, v_amount_in_usd, v_commission_in_usd,
                       v_amount_in_usd, NEW.id)
            ON DUPLICATE KEY UPDATE rate_btc_for_one_usd = v_btc_rate,
                                    withdraw_amount_usd  = withdraw_amount_usd + v_amount_in_usd,
                                    inout_commission_usd = inout_commission_usd + v_commission_in_usd,
                                    balance_dynamics_usd = balance_dynamics_usd + v_amount_in_usd,
                                    last_updated_id      = NEW.id;
        END IF;
    END IF;
END ;
||
DELIMITER ;
