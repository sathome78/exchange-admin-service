package me.exrates.adminservice.core.repository.impl;

import com.google.common.annotations.VisibleForTesting;
import me.exrates.adminservice.core.repository.CoreExorderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class CoreExorderRepositoryImpl implements CoreExorderRepository {

    private static final String BUY = "buy";
    private static final String SELL = "sell";


    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Autowired
    public CoreExorderRepositoryImpl(@Qualifier("coreNPTemplate") NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public Map<String, Integer> getDailyBuySellVolume() {
        String sql = "SELECT" +
                " SUM(CASE WHEN " + COL_OPERATION_TYPE_ID + " = 3 THEN " + COL_AMOUNT_BASE + " ELSE 0 END) AS " + SELL + "," +
                " SUM(CASE WHEN " + COL_OPERATION_TYPE_ID + " = 4 THEN " + COL_AMOUNT_BASE + " ELSE 0 END) AS " + BUY  +
                " FROM " + TABLE +
                " WHERE " + COL_STATUS_ID + " = 3" +
                " AND "+ COL_DATE_ACCEPTION + " > CURRENT_TIMESTAMP - INTERVAL 1 DAY;";
        final Map<String, BigDecimal> rawValues = namedParameterJdbcOperations.query(sql, Collections.emptyMap(), rs -> {
            final Map<String, BigDecimal> values = new HashMap<>(2);
            while (rs.next()) {
                values.put(BUY, rs.getBigDecimal(BUY));
                values.put(SELL, rs.getBigDecimal(SELL));
            }
            return values;
        });
        return getPercentage(rawValues);
    }

    @Override
    public int getDailyUniqueUsersQuantity() {
        String sql = "SELECT " + COL_USER_ID + ", " + COL_USER_ACCEPTOR_ID +
                " FROM " + TABLE +
                " WHERE " + COL_STATUS_ID + " = 3" +
                " AND "+ COL_DATE_ACCEPTION + " > CURRENT_TIMESTAMP - INTERVAL 1 DAY;";
        final Set<Integer> uniqUsers = namedParameterJdbcOperations.query(sql, Collections.emptyMap(), rs -> {
            final Set<Integer> users = new HashSet<>();
            while (rs.next()) {
                users.add(rs.getInt(COL_USER_ID));
                users.add(rs.getInt(COL_USER_ACCEPTOR_ID));
            }
            return users;
        });
        return uniqUsers.size();

    }

    @VisibleForTesting
    protected Map<String, Integer> getPercentage(Map<String, BigDecimal> rawValues) {
        Map<String, Integer> result = new HashMap<>(2);
        if (rawValues.isEmpty()) {
            result.put(BUY, 0);
            result.put(SELL, 0);
            return result;
        }
        BigDecimal total = rawValues.getOrDefault(BUY, BigDecimal.ZERO).add(rawValues.getOrDefault(SELL, BigDecimal.ZERO));
        int buyPercent = rawValues.getOrDefault(BUY, BigDecimal.ZERO).intValue() * 100 / total.intValue();
        result.put(BUY, buyPercent);
        result.put(SELL, 100 - buyPercent);
        return result;
    }
}
