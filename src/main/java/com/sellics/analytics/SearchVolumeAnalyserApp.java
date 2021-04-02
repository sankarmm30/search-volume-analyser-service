package com.sellics.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@EnableCircuitBreaker
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class SearchVolumeAnalyserApp {

	public static void main(String[] args) {

		SpringApplication.run(SearchVolumeAnalyserApp.class, args);
	}
}
