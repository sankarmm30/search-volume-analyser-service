package com.sellics.analytics.service.impl;

import com.sellics.analytics.exception.GenericClientRuntimeException;
import com.sellics.analytics.exception.GenericServerRuntimeException;
import com.sellics.analytics.model.EstimateResponseDto;
import com.sellics.analytics.service.ApiClientService;
import com.sellics.analytics.service.SearchVolumeAnalyserService;
import com.sellics.analytics.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Service("amazonSearchVolumeAnalyserService")
public class AmazonSearchVolumeAnalyserServiceImpl implements SearchVolumeAnalyserService {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonSearchVolumeAnalyserServiceImpl.class);

    private static final Integer STRATEGY_ONE_TOTAL_KEYWORDS_EXPECTED = 10;
    private static final Integer STRATEGY_TWO_TOTAL_KEYWORDS_EXPECTED = 350;
    private static final Integer STRATEGY_TWO_WEIGHTAGE = 80;
    private static final Integer STRATEGY_THREE_WEIGHTAGE = 10;
    private static final Integer STRATEGY_THREE_TOTAL_KEYWORDS_EXPECTED = 35;
    private static final String SINGLE_SPACE = " ";

    private static List<String> searchSupportList;
    static {

        searchSupportList = new ArrayList<>();
        searchSupportList.addAll(Arrays.asList("a","b","c","d","e","f","g"));
        searchSupportList.addAll(Arrays.asList("h","i","j","k","l","m","n"));
        searchSupportList.addAll(Arrays.asList("o","p","q","r","s","t","u"));
        searchSupportList.addAll(Arrays.asList("v","w","x","y","z","1","2"));
        searchSupportList.addAll(Arrays.asList("3","4","5","6","7","8","9", ""));
    }

    private ApiClientService amazonApiClientService;

    public AmazonSearchVolumeAnalyserServiceImpl(
            @Qualifier("amazonApiClientService") ApiClientService amazonApiClientService) {

        this.amazonApiClientService = amazonApiClientService;
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
            int finalScore = getStrategyOneScore(keyword) + getStrategyTwoScore(keyword) + getStrategyThreeScore(keyword);

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

    /**
     * This method calculates the score based on the number of unique keywords received from Auto-Complete for the search
     * with FIRST character of given keyword. Each unique keyword will get 1 score.
     *
     * Example: given keyword is "iphone" and we received 9 unique keyword for the search with character "i" then
     * method will return 9.
     *
     * @param keyword
     * @return
     */
    private int getStrategyOneScore(final String keyword) {

        int score= Math.toIntExact(this.amazonApiClientService.callAndGetResponse(String.valueOf(keyword.charAt(0)))
                .stream()
                .filter(var -> var.contains(keyword))
                .distinct()
                .limit(STRATEGY_ONE_TOTAL_KEYWORDS_EXPECTED)
                .count());

        LOG.debug("Score calculated in strategy one: {}", score);

        return score;
    }

    /**
     * This method calculates the score based on the number of unique keywords received from Auto-Complete for the search
     * with APPENDING alphabets and numeric character in SUFFIX with given keyword.
     *
     * Formula: score = (no.of unique keyword received * weightage) / (no.of unique keyword expected)
     *
     * @param keyword
     * @return
     */
    private int getStrategyTwoScore(final String keyword) {

        // Calling Auto-Complete API in parallel and collecting all unique keywords in Set.
        Set<String> uniqueKeywords =
                searchSupportList
                        .parallelStream()
                        .flatMap(str -> this.amazonApiClientService.callAndGetResponse(keyword + SINGLE_SPACE + str).stream())
                        .filter(var -> var.contains(keyword))
                        .limit(STRATEGY_TWO_TOTAL_KEYWORDS_EXPECTED)
                        .collect(Collectors.toSet());

        LOG.debug("Total unique keywords received in strategy two: {}", uniqueKeywords.size());

        int score = Math.round(uniqueKeywords.size() * STRATEGY_TWO_WEIGHTAGE / STRATEGY_TWO_TOTAL_KEYWORDS_EXPECTED);

        LOG.debug("Score calculated in strategy two: {}", score);

        return score > 0 ? score : uniqueKeywords.size();
    }

    /**
     * This method calculates the score based on the number of unique keywords received from Auto-Complete for the search
     * with APPENDING alphabets and numeric character in PREFIX with given keyword.
     *
     * Formula: score = (no.of unique keyword received * weightage) / (no.of unique keyword expected)
     *
     * @param keyword
     * @return
     */
    private int getStrategyThreeScore(final String keyword) {

        // Calling Auto-Complete API in parallel and collecting all unique keywords in Set.
        // This will apply break once it receives total number of expected keywords.
        Set<String> uniqueKeywords =
                searchSupportList
                        .parallelStream()
                        .filter(str -> str.length() > 0)
                        .flatMap(str -> this.amazonApiClientService.callAndGetResponse(str + SINGLE_SPACE + keyword).stream())
                        .filter(var -> var.contains(keyword))
                        .limit(STRATEGY_THREE_TOTAL_KEYWORDS_EXPECTED)
                        .collect(Collectors.toSet());

        LOG.debug("Total unique keywords received in strategy three: {}", uniqueKeywords.size());

        int score = Math.round(uniqueKeywords.size() * STRATEGY_THREE_WEIGHTAGE / STRATEGY_THREE_TOTAL_KEYWORDS_EXPECTED);

        LOG.debug("Score calculated in strategy three: {}", score);

        return score > 0 ? score : uniqueKeywords.size();
    }
}
