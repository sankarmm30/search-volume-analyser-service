package com.sellics.analytics.service.strategy.impl;


import com.sellics.analytics.service.ApiClientService;
import com.sellics.analytics.service.strategy.SearchVolumeStrategyService;
import com.sellics.analytics.util.CommonUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
 * This method calculates the score based on the number of unique keywords received from Auto-Complete for the search
 * with APPENDING alphabets and numeric character in PREFIX with given keyword.
 *
 * Formula: score = (no.of unique keyword received * weightage) / (no.of unique keyword expected)
 *
 */
@Service("searchVolumeStrategyPrefixService")
public class SearchVolumeStrategyPrefixServiceImpl extends AbstractSearchVolumeStrategyServiceImpl
        implements SearchVolumeStrategyService {

    private static final Integer STRATEGY_PREFIX_WEIGHTAGE = 10;
    private static final Integer STRATEGY_PREFIX_TOTAL_KEYWORDS_EXPECTED = 35;

    public SearchVolumeStrategyPrefixServiceImpl(ApiClientService amazonApiClientService) {

        super(amazonApiClientService);
    }

    @Override
    protected Integer getTotalKeywordExpected() {

        return STRATEGY_PREFIX_TOTAL_KEYWORDS_EXPECTED;
    }

    @Override
    protected Integer getWeightage() {

        return STRATEGY_PREFIX_WEIGHTAGE;
    }

    @Override
    protected List<String> getSearchKeywords(String keyword) {

        return CommonUtil.getSearchCharList()
                .stream()
                .map(var -> var + " "+ keyword)
                .collect(Collectors.toList());
    }
}
