package com.sellics.analytics.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.sellics.analytics.exception.GenericClientRuntimeException;
import com.sellics.analytics.exception.GenericServerRuntimeException;
import com.sellics.analytics.exception.RequestTimeoutException;
import com.sellics.analytics.model.EstimateResponseDto;
import com.sellics.analytics.model.GenericExceptionResponseDto;
import com.sellics.analytics.service.SearchVolumeAnalyserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;

@RestController("amazonSearchVolumeAnalyserController")
@RequestMapping(value = "/amazon")
@Api(value = "Amazon Search Volume Controller", description = "This controller provides endpoint to get " +
        " estimated search volume for amazon client")
public class AmazonSearchVolumeAnalyserController implements SearchVolumeAnalyserApi {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonSearchVolumeAnalyserController.class);

    private static final String REQUEST_TIMEOUT_MS = "10000";
    private static final String COMMAND_KEY = "AMAZON-API-CK";
    private static final String FALLBACK_METHOD_NAME = "handleTimeout";
    private static final String TIMEOUT_PROPERTY = "execution.isolation.thread.timeoutInMilliseconds";
    private static final String GET_ESTIMATE_PATH = "/amazon/estimate";

    private SearchVolumeAnalyserService amazonSearchVolumeAnalyserService;

    public AmazonSearchVolumeAnalyserController(
            @Qualifier("amazonSearchVolumeAnalyserService") SearchVolumeAnalyserService amazonSearchVolumeAnalyserService) {

        this.amazonSearchVolumeAnalyserService = amazonSearchVolumeAnalyserService;
    }

    @HystrixCommand(commandKey = COMMAND_KEY, fallbackMethod = FALLBACK_METHOD_NAME,
            ignoreExceptions = {GenericClientRuntimeException.class, GenericServerRuntimeException.class},
            commandProperties = {@HystrixProperty(name = TIMEOUT_PROPERTY, value = REQUEST_TIMEOUT_MS)
    })
    @ApiOperation(value = "Get estimated score [0 -> 100] for given keyword in amazon.com",
            response = EstimateResponseDto.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEstimate(
            @Valid @NotBlank(message = "Keyword cannot be null or empty") final @RequestParam String keyword) {

        return ResponseEntity.ok(this.amazonSearchVolumeAnalyserService.estimateSearchVolume(keyword));
    }

    /**
     * This method in charge of returning meaningful response when timeout occurs
     *
     * @param keyword
     * @return
     */
    public ResponseEntity<?> handleTimeout(String keyword) {

        return new ResponseEntity<>(
                GenericExceptionResponseDto.builder()
                        .timestamp(Date.from(Instant.now()))
                        .status(HttpStatus.REQUEST_TIMEOUT.value())
                        .errors(Collections.singletonList(RequestTimeoutException.MESSAGE))
                        .message(HttpStatus.REQUEST_TIMEOUT.getReasonPhrase())
                        .path(GET_ESTIMATE_PATH)
                        .build()
                , HttpStatus.NOT_FOUND);
    }
}
