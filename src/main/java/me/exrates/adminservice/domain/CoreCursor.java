package me.exrates.adminservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreCursor {

    private String tableName;
    private String tableColumn;
    private Integer cursorPosition;
    private LocalDateTime modified;
}
