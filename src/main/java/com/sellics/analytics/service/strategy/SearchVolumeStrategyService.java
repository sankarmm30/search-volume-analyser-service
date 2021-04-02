package com.sellics.analytics.service.strategy;

import com.sellics.analytics.model.EstimateResponseDto;

import java.util.List;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public interface SearchVolumeStrategyService {

    /**
     * This menthod is in charge of calculating the score for the given keyword.
     *
     * @param keyword
     * @return
     */
    Integer calculateScore(final String keyword);
}
