DROP TRIGGER IF EXISTS PREPARE_AFTER_USER_INSIGHTS;

DELIMITER $$

CREATE TRIGGER PREPARE_AFTER_USER_INSIGHTS
    AFTER INSERT
    ON admin_db.USER_ANNUAL_INSIGHTS
    FOR EACH ROW
BEGIN

    INSERT INTO USER_INOUT_STATUS (user_id, refill_amount_usd, withdraw_amount_usd)
        VALUE (NEW.user_id, ABS(NEW.refill_amount_usd), NEW.withdraw_amount_usd)
    ON DUPLICATE KEY UPDATE refill_amount_usd = refill_amount_usd + ABS(NEW.refill_amount_usd),
                            withdraw_amount_usd  = withdraw_amount_usd + ABS(NEW.withdraw_amount_usd);

END $$

DELIMITER ;
