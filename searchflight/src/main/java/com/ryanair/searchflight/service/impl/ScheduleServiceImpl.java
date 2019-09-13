package com.ryanair.searchflight.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ryanair.searchflight.dto.Request;
import com.ryanair.searchflight.dto.Response;
import com.ryanair.searchflight.dto.Route;
import com.ryanair.searchflight.dto.Schedule;
import com.ryanair.searchflight.dto.Response.Leg;
import com.ryanair.searchflight.exception.RestTemplateNotFoundException;
import com.ryanair.searchflight.model.AvailableRoutes;
import com.ryanair.searchflight.model.AvailableRoutes.FromToInterconnectedFlight;
import com.ryanair.searchflight.restclient.ScheduleApiController;
import com.ryanair.searchflight.service.ScheduleFilterService;
import com.ryanair.searchflight.util.ScheduleFilter;

/**
 * @author sergiopf
 */
@Service
public class ScheduleServiceImpl implements ScheduleFilterService{

	private static final Logger log = LogManager.getLogger(ScheduleServiceImpl.class);

	@Autowired
	ScheduleApiController scheduleApiController;
	
	@Autowired
	ScheduleFilter scheduleFilter;

    /**
	 *	List of flights from a given departure airport not earlier than the specified departure datetime 
	 *	and arriving to a given arrival airport not later than the specified arrival datetime.
	 *	The list consist of all direct flights if available (for example: DUB - WRO) and all
	 *	interconnected flights with a maximum of one stop if available (for example: DUB - STN - WRO)
	 *	@param input Data from initial request 
	 *  @param allRoutes noStopRoute and oneStopRoutes
	 */
	@Override
	public List<Response> getSchedules(Request request, AvailableRoutes allRoutes){
		
		List<Response> response = new ArrayList<>();
		
		CompletableFuture<Response> directFlight = CompletableFuture.supplyAsync(() -> {
			Response responseDirectFlight = directFlightSchedules(request, allRoutes.getDirectFlight());
			if (null != responseDirectFlight) {
				response.add(responseDirectFlight);
			}
			return null;
		 });
		
		allRoutes.getInterconnectedFlights().parallelStream().forEach((fromToRoute) -> { 
			Response responseInterconnectedFlight = interconnectedFlightSchedules(request, fromToRoute);
			if (null != responseInterconnectedFlight) {
				response.add(responseInterconnectedFlight);
			}
		});
		
		directFlight.join();
		Collections.sort(response);
		
		return response;
	}

	/**
	 * Flights scheduled for direct flights, no stops
	 */
	private Response directFlightSchedules(Request request, Route directFlight) {
		Response response = null;
		
		try {
			Schedule directFlightRouteSchedule = scheduleApiController.getSchedules(request.getDeparture(),
					request.getArrival(),
					request.getDepartureDateTimeFormatted(),
					request.getArrivalDateTimeFormatted());

			List<Leg> legs = scheduleFilter.filterLegsDirectFlight(request, directFlight, directFlightRouteSchedule);

			if (!legs.isEmpty()) {
				response = new Response(0,legs);
			}
		}catch(RestTemplateNotFoundException nfe) {
			log.error("Not found " + nfe.getMessage());
		}catch(Exception ex) {
			log.error(ex);
		}
	
		return response;
	}
	
	/**
	 * Flights scheduled for interconnected flights, one stop.
	 * Search each route (departure-stop, stop-arrival), and filter results.
	 */
	private Response interconnectedFlightSchedules(Request request, 
			FromToInterconnectedFlight fromToRoute) {

		Response response = null;

		try {
			Route interconnectedFlightRouteFrom = fromToRoute.getFrom();
			Schedule interconnectedFlightScheduleFrom = scheduleApiController.getSchedules(interconnectedFlightRouteFrom.getAirportFrom(),
					interconnectedFlightRouteFrom.getAirportTo(),
					request.getDepartureDateTimeFormatted(),
					request.getArrivalDateTimeFormatted());
			
			Route interconnectedFlightRouteTo = fromToRoute.getTo();
			Schedule interconnectedFlightScheduleTo = scheduleApiController.getSchedules(interconnectedFlightRouteTo.getAirportFrom(),
					interconnectedFlightRouteTo.getAirportTo(),
					request.getDepartureDateTimeFormatted(),
					request.getArrivalDateTimeFormatted());
			
			List<Leg> legs = scheduleFilter.filterLegsInterconnectedFlight(request, 
					interconnectedFlightRouteFrom,
					interconnectedFlightScheduleFrom,
					interconnectedFlightRouteTo, 
					interconnectedFlightScheduleTo);
			
			if (!legs.isEmpty()) {
				response = new Response(1, legs);
			}

		}catch(RestTemplateNotFoundException nfe) {
			log.error("Not found " + nfe.getMessage());
		}catch(Exception ex) {
			log.error(ex);
		}

		return response;
	}
	
}
