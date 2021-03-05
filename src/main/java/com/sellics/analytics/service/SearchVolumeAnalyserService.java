package com.sellics.analytics.service;

import com.sellics.analytics.model.EstimateResponseDto;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public interface SearchVolumeAnalyserService {

    /**
     * This menthod is in charge of estimating searching voulme for the given keyword.
     *
     * @param keyword
     * @return
     */
    EstimateResponseDto estimateSearchVolume(final String keyword);
}
