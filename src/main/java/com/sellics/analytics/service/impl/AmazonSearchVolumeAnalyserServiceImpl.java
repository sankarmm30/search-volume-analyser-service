package com.sellics.analytics.service.impl;

import com.sellics.analytics.exception.GenericClientRuntimeException;
import com.sellics.analytics.exception.GenericServerRuntimeException;
import com.sellics.analytics.model.EstimateResponseDto;
import com.sellics.analytics.service.SearchVolumeAnalyserService;
import com.sellics.analytics.service.strategy.SearchVolumeStrategyService;
import com.sellics.analytics.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Service("amazonSearchVolumeAnalyserService")
public class AmazonSearchVolumeAnalyserServiceImpl implements SearchVolumeAnalyserService {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonSearchVolumeAnalyserServiceImpl.class);

    private ApplicationContext applicationContext;

    public AmazonSearchVolumeAnalyserServiceImpl(ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }

    /**
     * This method is in charge of estimating score for the keyword.
     *
     * The final score is the sum of below three strategies and it has 10, 80 and 10 weightage respectively.
     *
     * Strategy-1: Score is calculated based on the number of unique keywords received from Auto-Complete for the search
     *             with FIRST character of given keyword. Each unique keyword will get 1 score.
     *             Example: given keyword is "iphone" and we received 9 unique keyword for the search with character "i" .
     *             Then the score for Strategy-1 is 9
     *
     * Strategy-2: Score is calculated based on the number of unique keywords received from Auto-Complete for the search
     *             with APPENDING alphabets and numeric character in SUFFIX with given keyword.
     *             Here 350 unique keywords are expected to score "80".
     *             Formula: score = (no.of unique keyword received * weightage) / (no.of unique keyword expected)
     *
     * Strategy-2: Score is calculated based on the number of unique keywords received from Auto-Complete for the search
     *             with APPENDING alphabets and numeric character in PREFIX with given keyword.
     *             Here 35 unique keywords are expected to score "80".
     *             Formula: score = (no.of unique keyword received * weightage) / (no.of unique keyword expected)
     *
     * @param keyword
     * @return
     */
    @Override
    public EstimateResponseDto estimateSearchVolume(final String keyword) {

        try {

            if(!StringUtils.hasText(keyword)){

                throw new GenericClientRuntimeException("Keyword cannot be null or empty");
            }

            if(CommonUtil.isStringContainsSpecialChar(keyword)){

                throw new GenericClientRuntimeException("Special characters are not allowed in keyword");
            }

            // Calculating the final score by adding each strategy scores

            int finalScore = this.applicationContext.getBeansOfType(SearchVolumeStrategyService.class)
                    .values()
                    .stream()
                    .mapToInt(service -> service.calculateScore(keyword))
                    .sum();

            LOG.info("Final score for the keyword: {} is {}", keyword, finalScore);

            return EstimateResponseDto.builder()
                    .keyword(keyword)
                    .score(finalScore)
                    .build();

        } catch(GenericClientRuntimeException exception) {

            throw exception;

        } catch (Exception exception) {

            LOG.error("Exception while estimating search-volume: {}", exception);

            throw new GenericServerRuntimeException("Unexpected error occurred", exception);
        }
    }
}
