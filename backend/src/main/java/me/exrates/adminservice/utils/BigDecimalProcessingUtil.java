package me.exrates.adminservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.core.domain.enums.ActionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class BigDecimalProcessingUtil {

    private static final int SCALE = 9;
    private static final RoundingMode ROUND_TYPE = RoundingMode.HALF_UP;
    private static final String PATTERN = "###,##0." + new String(new char[SCALE]).replace("\0", "0");
    private static final String PATTERN_SHORT = "###,##0." + new String(new char[SCALE]).replace("\0", "#");

    /**
     * Removes trailing zeros in BigDecimal value
     *
     * @param bigDecimal
     * @return BigDecimal value without trailing zeros
     */
    public static BigDecimal normalize(BigDecimal bigDecimal) {
        return normalize(bigDecimal, ROUND_TYPE);
    }

    public static BigDecimal normalize(BigDecimal bigDecimal, RoundingMode roundingMode) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.setScale(SCALE, roundingMode).stripTrailingZeros().add(BigDecimal.ZERO);
    }

    /**
     * Executes arithmetic operation and returns BigDecimal value with applied <b>SCALE</b> and <b>ROUND_TYPE</b>
     * and removed trailing zeros. Or "null" if at least one of operands is "null"
     * Before execution operation to operands apply <b>SCALE</b> and <b>ROUND_TYPE</b>
     *
     * @param value1     is the first operand for operation
     * @param value2     is the second operand for operation
     * @param actionType
     * @return BigDecimal value with applied <b>SCALE</b> and <b>ROUND_TYPE</b>
     * and removed trailing zeros. Or "null" if at least one of operands is "null"
     */
    public static BigDecimal doAction(BigDecimal value1, BigDecimal value2, ActionType actionType) {
        return doAction(value1, value2, actionType, ROUND_TYPE);
    }

    public static BigDecimal doAction(BigDecimal value1, BigDecimal value2, ActionType actionType, RoundingMode roundingMode) {
        if (value1 == null || value2 == null) {
            return null;
        }
        BigDecimal result = value1;
        value1 = value1.setScale(SCALE, roundingMode);
        value2 = value2.setScale(SCALE, roundingMode);
        switch (actionType) {
            case ADD: {
                result = value1.add(value2);
                break;
            }
            case SUBTRACT: {
                result = value1.subtract(value2);
                break;
            }
            case MULTIPLY: {
                result = value1.multiply(value2);
                break;
            }
            /*calculate value2 percent from value1*/
            case MULTIPLY_PERCENT: {
                result = value1.multiply(value2).divide(new BigDecimal(100), roundingMode);
                break;
            }
            /*calculate the growth in percent value2 relative value1
             * 50, 120 -> 120/50*100-100 -> 140*/
            case PERCENT_GROWTH: {
                result = value2.divide(value1, roundingMode).multiply(BigDecimal.valueOf(100)).add(BigDecimal.valueOf(100).negate());
                break;
            }
            case DEVIDE: {
                result = value1.divide(value2, roundingMode);
                break;
            }
        }
        return normalize(result, roundingMode);
    }

    /**
     * Returns String converted from BigDecimal value
     * with <b>No</b> group separator and <b>Point</b> as decimal separator
     * with trailing zeros if trailingZeros is "true" or without if "false"
     *
     * @param bigDecimal    value to convert
     * @param trailingZeros determines if trailing zeros will be added
     * @return string ov value or "0" if value is null
     * 67553.116000000 => 67553.116 or 67553.116000000 (depending on trailingZeros)
     */
    public static String formatNonePoint(BigDecimal bigDecimal, boolean trailingZeros) {
        DecimalFormat df = new DecimalFormat(trailingZeros ? PATTERN : PATTERN_SHORT);
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        df.setRoundingMode(ROUND_TYPE);
        df.setGroupingUsed(false);
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }
}