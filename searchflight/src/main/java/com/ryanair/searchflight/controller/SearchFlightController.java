package com.ryanair.searchflight.controller;

import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ryanair.searchflight.dto.Request;
import com.ryanair.searchflight.dto.Response;
import com.ryanair.searchflight.model.AvailableRoutes;
import com.ryanair.searchflight.service.RouteFilterService;
import com.ryanair.searchflight.service.ScheduleFilterService;

@Controller
@RequestMapping(path = "/searchflight/3/steps", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchFlightController {
	
	private static final Logger log = LogManager.getLogger(SearchFlightController.class);	
	
	@Autowired
	RouteFilterService routesCalculation;
   
	@Autowired
	ScheduleFilterService scheduleCalculation;
	
   /**
    * Response to following request URI with given query parameters:
    * interconnections?departure={departure}&arrival={arrival}&departureDateTime={departureDateTime}&arrivalDateTime={arrivalDateTime}
    */
   @GetMapping("/interconnections")
   public @ResponseBody List<Response> getInterconnections(@Validated Request request){ 

	   long init = System.currentTimeMillis();
	   
	   AvailableRoutes routes = this.routesCalculation.getRoutes(request);
	   List<Response> response = this.scheduleCalculation.getSchedules(request, routes);

	   long end = System.currentTimeMillis();
	   log.warn("TIME PROCESS SCHEDULES: "+ (end - init));

	   return response;
	}
	
}
