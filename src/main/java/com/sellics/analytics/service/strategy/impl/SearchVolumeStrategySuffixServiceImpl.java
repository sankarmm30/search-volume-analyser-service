package com.sellics.analytics.service.strategy.impl;


import com.sellics.analytics.service.ApiClientService;
import com.sellics.analytics.service.strategy.SearchVolumeStrategyService;
import com.sellics.analytics.util.CommonUtil;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
 * This method calculates the score based on the number of unique keywords received from Auto-Complete for the search
 * with APPENDING alphabets and numeric character in SUFFIX with given keyword.
 *
 */
@Service("searchVolumeStrategySuffixService")
public class SearchVolumeStrategySuffixServiceImpl extends AbstractSearchVolumeStrategyServiceImpl
        implements SearchVolumeStrategyService {

    private static final Integer STRATEGY_TWO_TOTAL_KEYWORDS_EXPECTED = 350;
    private static final Integer STRATEGY_TWO_WEIGHTAGE = 80;

    public SearchVolumeStrategySuffixServiceImpl(ApiClientService amazonApiClientService) {

        super(amazonApiClientService);
    }

    @Override
    protected Integer getTotalKeywordExpected() {

        return STRATEGY_TWO_TOTAL_KEYWORDS_EXPECTED;
    }

    @Override
    protected Integer getWeightage() {

        return STRATEGY_TWO_WEIGHTAGE;
    }

    @Override
    protected List<String> getSearchKeywords(String keyword) {

        List<String> searchKeywordList = CommonUtil.getSearchCharList();

        searchKeywordList.add("");  // Adding empty string to search exact keyword

        return searchKeywordList
                .stream()
                .map(var -> keyword + " " + var)
                .collect(Collectors.toList());
    }
}
