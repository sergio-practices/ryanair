package com.ryanair.searchflight.service;

import com.ryanair.searchflight.dto.Request;
import com.ryanair.searchflight.model.AvailableRoutes;

public interface RouteFilterService {

	public AvailableRoutes getRoutes(Request request);
	
}
