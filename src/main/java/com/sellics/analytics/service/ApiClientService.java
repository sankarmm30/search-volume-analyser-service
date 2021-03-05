package com.sellics.analytics.service;

import java.util.List;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public interface ApiClientService {

    List<String> callAndGetResponse(final String keyword);
}
