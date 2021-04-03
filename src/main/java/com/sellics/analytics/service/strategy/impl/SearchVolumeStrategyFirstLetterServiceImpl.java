package com.sellics.analytics.service.strategy.impl;


import com.sellics.analytics.service.ApiClientService;
import com.sellics.analytics.service.strategy.SearchVolumeStrategyService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
 * This method calculates the score based on the number of unique keywords received from Auto-Complete for the search
 * with FIRST character of given keyword. Each unique keyword will get 1 score.
 *
 * Example: given keyword is "iphone" and we received 9 unique keyword for the search with character "i" then
 * method will return 9.
 *
 */
@Service("searchVolumeStrategyFirstLetterService")
public class SearchVolumeStrategyFirstLetterServiceImpl extends AbstractSearchVolumeStrategyServiceImpl
        implements SearchVolumeStrategyService {

    private static final Integer STRATEGY_FL_TOTAL_KEYWORDS_EXPECTED = 10;
    private static final Integer STRATEGY_FL_TOTAL_WEIGHTAGE = 10;

    public SearchVolumeStrategyFirstLetterServiceImpl(ApiClientService amazonApiClientService) {

        super(amazonApiClientService);
    }

    @Override
    protected Integer getTotalKeywordExpected() {

        return STRATEGY_FL_TOTAL_KEYWORDS_EXPECTED;
    }

    @Override
    protected Integer getWeightage() {

        return STRATEGY_FL_TOTAL_WEIGHTAGE;
    }

    @Override
    protected List<String> getSearchKeywords(String keyword) {

        return Collections.singletonList(String.valueOf(keyword.charAt(0)));
    }
}
