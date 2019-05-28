package me.exrates.adminservice.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.repository.CurrencyDao;
import me.exrates.adminservice.domain.CurrencyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Log4j2
@Repository
public class CurrencyDaoImpl implements CurrencyDao {

    // todo let's separate repo for admin and core
    private final NamedParameterJdbcTemplate npJdbcTemplate;

    @Autowired
    public CurrencyDaoImpl(@Qualifier("coreTemplate") NamedParameterJdbcTemplate npJdbcTemplate) {
        this.npJdbcTemplate = npJdbcTemplate;
    }

    @Override
    public CurrencyDto findByName(String name) {
        final String sql = "SELECT * FROM CURRENCY WHERE name = :name";

        try {
            return npJdbcTemplate.queryForObject(sql, Collections.singletonMap("name", name), new BeanPropertyRowMapper<>(CurrencyDto.class));
        } catch (Exception ex) {
            log.warn("Failed to find currency for name: {}", name, ex);
            throw ex;
        }
    }

    public List<CurrencyDto> getAllCurrencies() {
        String sql = "SELECT id, name, hidden FROM CURRENCY";

        return npJdbcTemplate.query(sql, (rs, row) -> CurrencyDto.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .hidden(rs.getBoolean("hidden"))
                .build());
    }
}
