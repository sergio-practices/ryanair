package com.ryanair.searchflight.restclient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ryanair.searchflight.config.PropertiesConstants;
import com.ryanair.searchflight.dto.Route;

/**
 * @author sergiopf
 */
@Component
public class RouteApiController {

	private static final Logger log = LogManager.getLogger(ScheduleApiController.class);

	private static final String CONST_RYANAIR="RYANAIR";
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	PropertiesConstants propertiesConstants;
	
	private static List<Route> filteredRoutes;
	
	/**
	 * Returns a list of all available routes based on the airport's IATA codes.
	 * API: https://services-api.ryanair.com/locate/3/routes
     */
	public List<Route> getRoutes() {
		if (null == filteredRoutes) {
			refreshFilterRoutes();
		}
		return filteredRoutes;
	}
	
	/**
	 * Update filteredRoutes every 60 seconds (for testing)
	 */

	public synchronized void refreshFilterRoutes() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		log.debug("Caching routes: " + propertiesConstants.getRoutes());
		List<Route> allRoutes = restTemplate.exchange(propertiesConstants.getRoutes(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Route>>(){}).getBody();
		filteredRoutes = allRoutes.stream()
				.filter(route -> null == route.getConnectingAirport())
				.filter(route -> CONST_RYANAIR.equals(route.getOperator()))
				.collect(Collectors.toList());
	}
	

}