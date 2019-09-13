package com.ryanair.searchflight.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ryanair.searchflight.dto.Request;
import com.ryanair.searchflight.dto.Route;
import com.ryanair.searchflight.model.AvailableRoutes.FromToInterconnectedFlight;

@Component
public class RouteFilter {

	public Route filterDirectFlight(Request request, List<Route> listRoutes) {
		return listRoutes.stream()
			.filter(route -> request.getDeparture().equals(route.getAirportFrom()) 
					&& request.getArrival().equals(route.getAirportTo()))
			.findAny()
			.orElse(null);
	}
	
	/**
	 * Create two airports lists, one with same airport IATA code FROM and the other with same airport IATA code TO, 
	 * then join coincidences between these two lists to create the complete route (for example: DUB - STN - WRO).
	 */
	public List<FromToInterconnectedFlight> filterInterconnectedFlight(Request request, List<Route> listRoutes) {

		List<Route> listAirportsFrom = listRoutes.stream()
			.filter(route -> request.getDeparture().equals(route.getAirportFrom()) && !request.getArrival().equals(route.getAirportTo()))
			.collect(Collectors.toList());

		List<Route> listAirportsTo = listRoutes.stream()
			.filter(route -> !request.getDeparture().equals(route.getAirportFrom()) && request.getArrival().equals(route.getAirportTo()))
			.collect(Collectors.toList());
		
		List<FromToInterconnectedFlight> interconnectedFlights= new ArrayList<>();
		for (Route routeFrom: listAirportsFrom) {
			for (Route routeTo: listAirportsTo) {
				if (routeFrom.getAirportTo().equals(routeTo.getAirportFrom())) {
					FromToInterconnectedFlight fromToRoute = new FromToInterconnectedFlight();
					fromToRoute.setFrom(routeFrom);
					fromToRoute.setTo(routeTo);
					interconnectedFlights.add(fromToRoute);
				}
			}
		}

		return interconnectedFlights;
	}
	
}
