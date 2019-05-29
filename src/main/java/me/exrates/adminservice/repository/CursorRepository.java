package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.CoreCursor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

public interface CursorRepository {

    String TABLE_NAME = "CURSORS";
    String COL_TABLE_NAME = "table_name";
    String COL_COLUMN_NAME = "table_column";
    String COL_LAST_ID = "last_id";
    String COL_MODIFIED = "modified_at";

    String UPDATE_CURSOR_TEMPLATE = "REPLACE INTO CURSORS (last_id, table_name, table_column) SELECT MAX(%s), \'%s\', \'%s\' FROM %s;";

    Long findLastByTable(String tableName);

    boolean updateCursorByTable(String sql);

    CoreCursor save(CoreCursor coreCursor);

    Collection<CoreCursor> findAll();
}
