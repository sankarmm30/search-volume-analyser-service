package com.sellics.analytics.service;

import com.sellics.analytics.exception.GenericClientRuntimeException;
import com.sellics.analytics.model.EstimateResponseDto;
import com.sellics.analytics.service.impl.AmazonSearchVolumeAnalyserServiceImpl;
import com.sellics.analytics.service.strategy.SearchVolumeStrategyService;
import com.sellics.analytics.service.strategy.impl.SearchVolumeStrategyFirstLetterServiceImpl;
import com.sellics.analytics.service.strategy.impl.SearchVolumeStrategyPrefixServiceImpl;
import com.sellics.analytics.service.strategy.impl.SearchVolumeStrategySuffixServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class AmazonSearchVolumeAnalyserServiceImplTest {

    private static final String KEYWORD = "test";

    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private ApiClientService amazonApiClientService;

    @InjectMocks
    SearchVolumeAnalyserService amazonSearchVolumeAnalyserService = new AmazonSearchVolumeAnalyserServiceImpl(applicationContext);

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

        Map<String, SearchVolumeStrategyService> serviceMap = new HashMap<>();
        serviceMap.put("searchVolumeStrategyFirstLetterService", new SearchVolumeStrategyFirstLetterServiceImpl(amazonApiClientService));
        serviceMap.put("searchVolumeStrategyPrefixService", new SearchVolumeStrategyPrefixServiceImpl(amazonApiClientService));
        serviceMap.put("searchVolumeStrategySuffixService", new SearchVolumeStrategySuffixServiceImpl(amazonApiClientService));

        Mockito.when(this.applicationContext.getBeansOfType(SearchVolumeStrategyService.class))
                .thenReturn(serviceMap);
    }

    @Test
    public void testEstimateSearchVolumeValid() {

        Mockito.when(amazonApiClientService.callAndGetResponse(Mockito.eq("t")))
                .thenReturn(Collections.singletonList(KEYWORD));

        Mockito.when(amazonApiClientService.callAndGetResponse(Mockito.eq("test a")))
                .thenReturn(Collections.singletonList(KEYWORD));

        Mockito.when(amazonApiClientService.callAndGetResponse(Mockito.eq("a test")))
                .thenReturn(Collections.singletonList(KEYWORD));

        EstimateResponseDto estimateResponseDto = amazonSearchVolumeAnalyserService.estimateSearchVolume(KEYWORD);

        Assert.assertNotNull(estimateResponseDto);
        Assert.assertEquals(KEYWORD, estimateResponseDto.getKeyword());
        Assert.assertEquals(3, estimateResponseDto.getScore().intValue());
    }

    @Test(expected = GenericClientRuntimeException.class)
    public void testEstimateSearchVolumeInValidWithNullKeyword() {

        amazonSearchVolumeAnalyserService.estimateSearchVolume(null);
    }

    @Test(expected = GenericClientRuntimeException.class)
    public void testEstimateSearchVolumeInValidWithEmptyKeyword() {

        amazonSearchVolumeAnalyserService.estimateSearchVolume("");
    }

    @Test(expected = GenericClientRuntimeException.class)
    public void testEstimateSearchVolumeInValidWithKeywordContainsSpecialCharacter() {

        amazonSearchVolumeAnalyserService.estimateSearchVolume(".");
    }
}
