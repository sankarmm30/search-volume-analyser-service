package com.sellics.analytics.controller;

import com.sellics.analytics.SearchVolumeAnalyserApp;
import com.sellics.analytics.model.EstimateResponseDto;
import com.sellics.analytics.service.SearchVolumeAnalyserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchVolumeAnalyserApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class AmazonSearchVolumeAnalyserControllerIntegrationTest {

    private static final String KEYWORD = "iphone";
    private static final String BASE_URL = "http://localhost:";
    private static final String GET_ESTIMATE_URL = "/amazon/estimate?keyword=";

    @Autowired
    private SearchVolumeAnalyserService amazonSearchVolumeAnalyserService;
    @Autowired
    private Environment environment;

    @LocalServerPort
    private Integer port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private static final HttpHeaders HEADERS = new HttpHeaders();
    static {
        HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testGetEstimateValid() {

        // Given
        HttpEntity<Object> entity = new HttpEntity<>(null, HEADERS);

        ResponseEntity<EstimateResponseDto> response = restTemplate.exchange(
                BASE_URL + port + GET_ESTIMATE_URL + KEYWORD, HttpMethod.GET, entity,
                EstimateResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertNotNull(response.getBody().getKeyword());
        Assert.assertNotNull(response.getBody().getScore());
        Assert.assertTrue(response.getBody().getScore() > 0);
    }

    @Test
    public void testGetEstimateValidWithZeroScore() {

        // Given
        HttpEntity<Object> entity = new HttpEntity<>(null, HEADERS);

        ResponseEntity<EstimateResponseDto> response = restTemplate.exchange(
                BASE_URL + port + GET_ESTIMATE_URL + "invalidkeywordtrial", HttpMethod.GET, entity,
                EstimateResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertNotNull(response.getBody().getKeyword());
        Assert.assertNotNull(response.getBody().getScore());
        Assert.assertEquals(0, response.getBody().getScore().intValue());
    }

    @Test
    public void testGetEstimateInValidWithNullKeyword() {

        // Given
        HttpEntity<Object> entity = new HttpEntity<>(null, HEADERS);

        ResponseEntity<EstimateResponseDto> response = restTemplate.exchange(
                BASE_URL + port + GET_ESTIMATE_URL, HttpMethod.GET, entity,
                EstimateResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
    }

    @Test
    public void testGetEstimateInValidWithKeywordContainsSpecialCharacter() {

        // Given
        HttpEntity<Object> entity = new HttpEntity<>(null, HEADERS);

        ResponseEntity<EstimateResponseDto> response = restTemplate.exchange(
                BASE_URL + port + GET_ESTIMATE_URL + ".", HttpMethod.GET, entity,
                EstimateResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
    }
}
