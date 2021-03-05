package com.sellics.analytics.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sellics.analytics.constant.GlobalConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericExceptionResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GlobalConstant.DATE_TIME_FORMAT)
    private Date timestamp;

    private Integer status;
    private String message;
    private List<String> errors;
    private String path;
}