package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoreUserOperationAuthorityDto {

    private List<CoreUserOperationAuthorityOptionDto> options;

    @NotNull
    private Integer userId;
}