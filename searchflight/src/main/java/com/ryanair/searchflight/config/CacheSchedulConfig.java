package com.ryanair.searchflight.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ryanair.searchflight.restclient.RouteApiController;

@Configuration
@EnableScheduling
public class CacheSchedulConfig {

	@Autowired
	RouteApiController routeApiController;
	
	@Scheduled(fixedRateString = "${timing.updateData}")
	public void scheduleFilterRoutes() {
		routeApiController.refreshFilterRoutes();
	}
	
}
