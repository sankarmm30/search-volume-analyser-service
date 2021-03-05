package com.sellics.analytics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sellics.analytics.exception.GenericServerRuntimeException;
import com.sellics.analytics.service.impl.AmazonApiClientServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RunWith(JUnit4.class)
public class AmazonApiClientServiceImplTest {

    private static final String KEYWORD = "test";
    private static final String CACHE_CONTROL = "private, no-store, max-age=0";
    private static final String API_RESPONSE_VALID = "[\"car\",[\"carvedilol tablet\",\"carvedilol\",\"gift card\"," +
            "\"pokemon cards\",\"car accessories\",\"carpet cleaner\",\"tarot cards\",\"car seat covers\"," +
            "\"carbon monoxide detectors\",\"car accessories for women\"],[{},{},{},{},{},{},{},{},{},{}],[]," +
            "\"JHR10X9O73Y0\"]\n";
    private static final String API_RESPONSE_INVALID = "[\"car\",[],[{},{},{},{},{},{},{},{},{},{}],[],\"JHR10X9O73Y0\"]";
    private static final Integer CACHE_EXPIRES = 0;

    @Mock
    private RestTemplate restTemplateMock;
    @Mock
    private ResponseEntity<String> responseEntityMock;
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ApiClientService amazonApiClientService = new AmazonApiClientServiceImpl(restTemplateMock, objectMapper);

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

        Mockito.when(responseEntityMock.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(responseEntityMock.getBody()).thenReturn(API_RESPONSE_VALID);

        Mockito.when(this.restTemplateMock.exchange(Mockito.anyString(),
                Mockito.eq(HttpMethod.GET), Mockito.eq(buildHttpRequest()), Mockito.eq(String.class))).thenReturn(responseEntityMock);
    }

    @Test
    public void testCallAndGetResponseValid() {

        List<String> keywordList = amazonApiClientService.callAndGetResponse(KEYWORD);

        Assert.assertFalse(keywordList.isEmpty());
        Assert.assertEquals(10, keywordList.size());
    }

    @Test
    public void testCallAndGetResponseInValid() {

        Mockito.when(responseEntityMock.getBody()).thenReturn(API_RESPONSE_INVALID);

        Mockito.when(this.restTemplateMock.exchange(Mockito.anyString(),
                Mockito.eq(HttpMethod.GET), Mockito.eq(buildHttpRequest()), Mockito.eq(String.class))).thenReturn(responseEntityMock);

        List<String> keywordList = amazonApiClientService.callAndGetResponse(KEYWORD);

        Assert.assertTrue(keywordList.isEmpty());
    }

    @Test
    public void testCallAndGetResponseInValidWithNullBody() {

        Mockito.when(responseEntityMock.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        Mockito.when(this.restTemplateMock.exchange(Mockito.anyString(),
                Mockito.eq(HttpMethod.GET), Mockito.eq(buildHttpRequest()), Mockito.eq(String.class))).thenReturn(responseEntityMock);

        List<String> keywordList = amazonApiClientService.callAndGetResponse(KEYWORD);

        Assert.assertTrue(keywordList.isEmpty());
    }

    @Test(expected = GenericServerRuntimeException.class)
    public void testCallAndGetResponseWithException() {

        Mockito.when(this.restTemplateMock.exchange(Mockito.anyString(),
                Mockito.eq(HttpMethod.GET), Mockito.eq(buildHttpRequest()), Mockito.eq(String.class)))
                .thenThrow(new IllegalStateException());

        amazonApiClientService.callAndGetResponse(KEYWORD);
    }

    @Test
    public void testCallAndGetResponseInValidWithHttpStatusIsNotOkay() {

        Mockito.when(responseEntityMock.getBody()).thenReturn(null);

        Mockito.when(this.restTemplateMock.exchange(Mockito.anyString(),
                Mockito.eq(HttpMethod.GET), Mockito.eq(buildHttpRequest()), Mockito.eq(String.class))).thenReturn(responseEntityMock);

        List<String> keywordList = amazonApiClientService.callAndGetResponse(KEYWORD);

        Assert.assertTrue(keywordList.isEmpty());
    }

    private HttpEntity<?> buildHttpRequest() {

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setExpires(CACHE_EXPIRES);
        httpHeaders.setCacheControl(CACHE_CONTROL);

        return new HttpEntity<>(httpHeaders);
    }
}
