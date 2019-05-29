package me.exrates.adminservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.domain.api.RateDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RateHistoryDto {

    @JsonIgnore
    private byte[] content;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    private List<RateDto> rates;
}