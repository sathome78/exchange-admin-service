package me.exrates.adminservice.daos.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.daos.ExchangeRatesDao;
import me.exrates.adminservice.models.api.RateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Log4j2
@Repository
public class ExchangeRatesDaoImpl implements ExchangeRatesDao {

    private final NamedParameterJdbcOperations npJdbcTemplate;
    private final JdbcOperations jdbcTemplate;

    @Autowired
    public ExchangeRatesDaoImpl(@Qualifier("NPTemplate") NamedParameterJdbcOperations npJdbcTemplate,
                                @Qualifier("template") JdbcOperations jdbcTemplate) {
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
}