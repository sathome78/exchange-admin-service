package me.exrates.adminservice.daos.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.daos.ExchangeRatesDao;
import me.exrates.adminservice.models.api.RateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Repository
public class ExchangeRatesDaoImpl implements ExchangeRatesDao {

    private final NamedParameterJdbcOperations npJdbcTemplate;

    @Autowired
    public ExchangeRatesDaoImpl(@Qualifier("NPTemplate") NamedParameterJdbcOperations npJdbcTemplate) {
        this.npJdbcTemplate = npJdbcTemplate;
    }

    @Override
    public RateDto getRateByCurrencyName(String currencyName) {
        final String sql = "SELECT ccr.currency_name, ccr.usd_rate, ccr.btc_rate FROM CURRENT_CURRENCY_RATES ccr WHERE ccr.currency_name = :currency_name";

        try {
            return npJdbcTemplate.queryForObject(sql, Collections.singletonMap("currency_name", currencyName), (rs, row) -> RateDto.builder()
                    .currencyName(rs.getString("currency_name"))
                    .usdRate(rs.getBigDecimal("usd_rate"))
                    .btcRate(rs.getBigDecimal("btc_rate"))
                    .build());
        } catch (Exception ex) {
            log.debug("Currency with name: {} not found", currencyName);
            return null;
        }
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
    public boolean addCurrencyExchangeRates(RateDto rateDto) {
        final String sql = "INSERT INTO CURRENT_CURRENCY_RATES (currency_name, usd_rate, btc_rate) VALUES (:currency_name, :usd_rate, :btc_rate)";

        Map<String, Object> params = new HashMap<>();
        params.put("currency_name", rateDto.getCurrencyName());
        params.put("usd_rate", rateDto.getUsdRate());
        params.put("btc_rate", rateDto.getBtcRate());

        return npJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean updateCurrencyExchangeRates(RateDto rateDto) {
        final String sql = "UPDATE CURRENT_CURRENCY_RATES ccr SET ccr.usd_rate = :usd_rate, ccr.btc_rate = :btc_rate";

        Map<String, Object> params = new HashMap<>();
        params.put("usd_rate", rateDto.getUsdRate());
        params.put("btc_rate", rateDto.getBtcRate());

        return npJdbcTemplate.update(sql, params) > 0;
    }
}