package com.sellics.analytics.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sellics.analytics.constant.GlobalConstant;
import com.sellics.analytics.exception.GenericServerRuntimeException;
import com.sellics.analytics.service.ApiClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Service("amazonApiClientService")
public class AmazonApiClientServiceImpl implements ApiClientService {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonApiClientServiceImpl.class);

    private static final String CACHE_CONTROL = "private, no-store, max-age=0";
    private static final Integer CACHE_EXPIRES = 0;

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    public AmazonApiClientServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {

        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * This method in charge of calling Auto-Complete API and get the response
     *
     * @param keyword
     * @return
     */
    @Override
    public List<String> callAndGetResponse(final String keyword) {


        try{

            final ResponseEntity<String> responseEntity =
                    this.restTemplate.exchange(GlobalConstant.AMAZON_AUTO_COMPLETE_API_URL + keyword,
                            HttpMethod.GET, buildHttpRequest(), String.class);

            if(HttpStatus.OK.equals(responseEntity.getStatusCode()) && responseEntity.getBody() != null) {

                // Finding out list of string object by checking the instanceof object.
                // If there are no object found then it returns empty list.

                return (List<String>) this.objectMapper.readValue(responseEntity.getBody(), List.class)
                        .stream()
                        .filter(object -> object instanceof List<?>
                                && !((List) object).isEmpty() && ((List) object).get(0) instanceof String)
                        .findFirst()
                        .orElse(Collections.emptyList());
            }

        } catch (Exception exception) {

            LOG.error("Exception while calling Auto Complete API: {}", exception);

            throw new GenericServerRuntimeException("Unexpected error occurred", exception);
        }

        return Collections.emptyList();
    }

    private HttpEntity<?> buildHttpRequest() {

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setExpires(CACHE_EXPIRES);
        httpHeaders.setCacheControl(CACHE_CONTROL);

        return new HttpEntity<>(httpHeaders);
    }
}
