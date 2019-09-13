package com.ryanair.searchflight.service;

import java.util.List;

import com.ryanair.searchflight.dto.Request;
import com.ryanair.searchflight.dto.Response;
import com.ryanair.searchflight.model.AvailableRoutes;

public interface ScheduleFilterService {

	public List<Response> getSchedules(Request request, AvailableRoutes routesResult);
	
}
