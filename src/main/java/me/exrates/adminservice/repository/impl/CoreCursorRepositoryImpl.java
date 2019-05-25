package me.exrates.adminservice.repository.impl;

import me.exrates.adminservice.domain.CoreCursor;
import me.exrates.adminservice.repository.CoreCursorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public class CoreCursorRepositoryImpl implements CoreCursorRepository {

    public static final Logger logger = LoggerFactory.getLogger(CoreCursorRepositoryImpl.class);

    private final NamedParameterJdbcTemplate adminTemplate;

    @Autowired
    public CoreCursorRepositoryImpl(@Qualifier("adminTemplate") NamedParameterJdbcTemplate adminTemplate) {
        this.adminTemplate = adminTemplate;
    }

    @Override
    public Long findLastByTable(String tableName) {
        try {
            String sql = String.format("SELECT %s FROM %s WHERE %s = :tableName", COL_LAST_ID, TABLE_NAME, COL_TABLE_NAME);
            MapSqlParameterSource params = new MapSqlParameterSource("tableName", tableName);
            return adminTemplate.queryForObject(sql, params, Long.class);
        } catch (DataAccessException e) {
            logger.warn("Failed to find last cursor value for table name: " + tableName, e);
            return -1L;
        }
    }

    @Override
    public CoreCursor save(CoreCursor coreCursor) {
        String sql = "INSERT INTO " + TABLE_NAME + " VALUE (:tableName, :tableColumnName, :cursorPosition) "
                + " ON DUPLICATE KEY UPDATE " + COL_LAST_ID + " = :cursorPosition";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tableName", coreCursor.getTableName())
                .addValue("tableColumnName", coreCursor.getTableColumn())
                .addValue("cursorPosition", coreCursor.getCursorPosition());
        final int rowsUpdated = adminTemplate.update(sql, params);
        if (rowsUpdated > 0) {
            coreCursor.setModified(LocalDateTime.now());
        }
        return coreCursor;
    }

    @Override
    public Collection<CoreCursor> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return adminTemplate.query(sql, getRowMapper());
    }

    private RowMapper<CoreCursor> getRowMapper() {
        return (rs, i) -> CoreCursor.builder()
                .tableName(rs.getString(COL_TABLE_NAME))
                .tableColumn(rs.getString(COL_COLUMN_NAME))
                .cursorPosition(rs.getLong(COL_LAST_ID))
                .modified(rs.getTimestamp(COL_MODIFIED).toLocalDateTime())
                .build();
    }

}
