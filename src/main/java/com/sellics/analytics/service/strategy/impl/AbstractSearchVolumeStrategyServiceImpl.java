package com.sellics.analytics.service.strategy.impl;


import com.sellics.analytics.service.ApiClientService;
import com.sellics.analytics.service.strategy.SearchVolumeStrategyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public abstract class AbstractSearchVolumeStrategyServiceImpl implements SearchVolumeStrategyService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSearchVolumeStrategyServiceImpl.class);

    private ApiClientService amazonApiClientService;

    public AbstractSearchVolumeStrategyServiceImpl(ApiClientService amazonApiClientService) {

        this.amazonApiClientService = amazonApiClientService;
    }

    @Override
    public Integer calculateScore(String keyword) {

        // Calling Auto-Complete API in parallel and collecting all unique keywords in Set.
        // This will apply break once it receives total number of expected keywords.

        Set<String> uniqueKeywords =
                this.getSearchKeywords(keyword)
                        .parallelStream()
                        .flatMap(str -> this.amazonApiClientService.callAndGetResponse(str).stream())
                        .filter(var -> var.contains(keyword))
                        .limit(this.getTotalKeywordExpected())
                        .collect(Collectors.toSet());

        LOG.info("Total unique keywords received: {}", uniqueKeywords.size());

        int score = Math.round(uniqueKeywords.size() * this.getWeightage() / this.getTotalKeywordExpected());

        LOG.info("Score calculated: {}", score);

        return score > 0 ? score : uniqueKeywords.size();
    }

    protected abstract Integer getTotalKeywordExpected();

    protected abstract Integer getWeightage();

    protected abstract List<String> getSearchKeywords(final String keyword);
}
