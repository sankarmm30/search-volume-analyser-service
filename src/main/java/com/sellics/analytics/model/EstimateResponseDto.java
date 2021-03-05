package com.sellics.analytics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EstimateResponseDto {

    private String keyword;
    private Integer score;
}
