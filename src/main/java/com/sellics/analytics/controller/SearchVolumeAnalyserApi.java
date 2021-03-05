package com.sellics.analytics.controller;

import com.sellics.analytics.model.EstimateResponseDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public interface SearchVolumeAnalyserApi {

    @ApiOperation(value = "Get estimated score [0 -> 100] for given keyword in amazon.com",
            response = EstimateResponseDto.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/estimate", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getEstimate(
            @Valid @NotBlank(message = "Keyword cannot be null or empty") final @RequestParam String keyword);

    ResponseEntity<?> handleTimeout(final String keyword);
}
