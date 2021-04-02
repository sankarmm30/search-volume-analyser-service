package com.sellics.analytics.service.strategy;

import com.sellics.analytics.service.ApiClientService;
import com.sellics.analytics.service.strategy.impl.SearchVolumeStrategyFirstLetterServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

@RunWith(JUnit4.class)
public class SearchVolumeStrategyFirstLetterServiceImplTest {

    private static final String KEYWORD = "test";

    @Mock
    private ApiClientService amazonApiClientService;

    @InjectMocks
    SearchVolumeStrategyService searchVolumeStrategyService =
            new SearchVolumeStrategyFirstLetterServiceImpl(amazonApiClientService);

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCalculateScoreValid() {

        Mockito.when(amazonApiClientService.callAndGetResponse(Mockito.eq("t")))
                .thenReturn(Collections.singletonList(KEYWORD));

        Assert.assertEquals(1, (long) searchVolumeStrategyService.calculateScore(KEYWORD));
    }

    @Test
    public void testCalculateScoreValidZeroScore() {

        Mockito.when(amazonApiClientService.callAndGetResponse(Mockito.eq("t")))
                .thenReturn(Collections.emptyList());

        Assert.assertEquals(0, (long) searchVolumeStrategyService.calculateScore(KEYWORD));
    }

    @Test
    public void testCalculateScoreValidZeroScoreWhenKeywordNotMatched() {

        Mockito.when(amazonApiClientService.callAndGetResponse(Mockito.eq("t")))
                .thenReturn(Collections.singletonList("abc"));

        Assert.assertEquals(0, (long) searchVolumeStrategyService.calculateScore(KEYWORD));
    }
}
