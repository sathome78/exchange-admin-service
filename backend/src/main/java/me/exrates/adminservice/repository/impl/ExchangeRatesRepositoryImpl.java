package me.exrates.adminservice.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.domain.RateHistoryDto;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.repository.ExchangeRatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Repository
public class ExchangeRatesRepositoryImpl implements ExchangeRatesRepository {

    private final NamedParameterJdbcOperations npJdbcTemplate;
    private final JdbcOperations jdbcTemplate;

    @Autowired
    public ExchangeRatesRepositoryImpl(@Qualifier("adminNPTemplate") NamedParameterJdbcOperations npJdbcTemplate,
                                       @Qualifier("adminTemplate") JdbcOperations jdbcTemplate) {
        this.npJdbcTemplate = npJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RateDto> getAllExchangeRates() {
        final String sql = "SELECT ccr.currency_name, ccr.usd_rate, ccr.btc_rate FROM CURRENT_CURRENCY_RATES ccr";

        return npJdbcTemplate.query(sql, (rs, row) -> RateDto.builder()
                .currencyName(rs.getString("currency_name"))
                .usdRate(rs.getBigDecimal("usd_rate"))
                .btcRate(rs.getBigDecimal("btc_rate"))
                .build());
    }

    @Override
    public void updateCurrencyExchangeRates(List<RateDto> rates) {
        final String sql = "UPDATE CURRENT_CURRENCY_RATES ccr " +
                "SET ccr.usd_rate = ?, ccr.btc_rate = ? " +
                "WHERE ccr.currency_name = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RateDto rateDto = rates.get(i);
                ps.setBigDecimal(1, rateDto.getUsdRate());
                ps.setBigDecimal(2, rateDto.getBtcRate());
                ps.setString(3, rateDto.getCurrencyName());
            }

            @Override
            public int getBatchSize() {
                return rates.size();
            }
        });
    }

    @Override
    public void saveCurrencyExchangeRatesHistory(byte[] zippedBytes) {
        final String sql = "INSERT INTO CURRENCY_RATES_HISTORY(content, created_at) VALUES (:content, :created_at)";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("content", zippedBytes);
                put("created_at", Timestamp.valueOf(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0)));
            }
        };
        npJdbcTemplate.update(sql, params);
    }

    @Override
    public List<RateHistoryDto> getExchangeRatesHistoryByDate(LocalDateTime fromDate, LocalDateTime toDate) {
        String sql = "SELECT crh.content, crh.created_at" +
                " FROM CURRENCY_RATES_HISTORY crh" +
                " WHERE crh.created_at BETWEEN :from_date AND :to_date";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("from_date", Timestamp.valueOf(fromDate));
                put("to_date", Timestamp.valueOf(toDate));
            }
        };

        return npJdbcTemplate.query(sql, params, (rs, row) -> RateHistoryDto.builder()
                .content(rs.getBytes("content"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build());
    }
}