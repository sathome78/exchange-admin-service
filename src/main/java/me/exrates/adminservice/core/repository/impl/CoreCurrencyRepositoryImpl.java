package me.exrates.adminservice.core.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.repository.CoreCurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Repository
public class CoreCurrencyRepositoryImpl implements CoreCurrencyRepository {

    private final NamedParameterJdbcOperations npJdbcTemplate;

    @Autowired
    public CoreCurrencyRepositoryImpl(@Qualifier("coreNPTemplate") NamedParameterJdbcOperations npJdbcTemplate) {
        this.npJdbcTemplate = npJdbcTemplate;
    }

    @Override
    public CoreCurrencyDto findById(int id) {
        final String sql = "SELECT * FROM CURRENCY WHERE id = :id";

        try {
            return npJdbcTemplate.queryForObject(sql, Collections.singletonMap("id", id), new BeanPropertyRowMapper<>(CoreCurrencyDto.class));
        } catch (Exception ex) {
            log.warn("Failed to find currency by id: {}", id);
            return null;
        }
    }

    @Override
    public CoreCurrencyDto findByName(String name) {
        final String sql = "SELECT * FROM CURRENCY WHERE name = :name";

        try {
            return npJdbcTemplate.queryForObject(sql, Collections.singletonMap("name", name), new BeanPropertyRowMapper<>(CoreCurrencyDto.class));
        } catch (Exception ex) {
            log.warn("Failed to find currency by name: {}", name);
            return null;
        }
    }

    @Override
    public List<CoreCurrencyDto> getAllCurrencies() {
        final String sql = "SELECT id, name, hidden FROM CURRENCY";

        return npJdbcTemplate.query(sql, (rs, row) -> CoreCurrencyDto.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .hidden(rs.getBoolean("hidden"))
                .build());
    }

    @Override
    public String getCurrencyName(int currencyId) {
        String sql = "SELECT name FROM CURRENCY WHERE  id = :id ";

        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("id", currencyId);

        try {
            return npJdbcTemplate.queryForObject(sql, namedParameters, String.class);
        } catch (Exception ex) {
            log.warn("Failed to find currency by id: {}", currencyId);
            return null;
        }
    }
}