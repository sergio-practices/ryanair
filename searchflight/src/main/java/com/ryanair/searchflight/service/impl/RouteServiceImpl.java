package com.ryanair.searchflight.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ryanair.searchflight.dto.Request;
import com.ryanair.searchflight.dto.Route;
import com.ryanair.searchflight.model.AvailableRoutes;
import com.ryanair.searchflight.restclient.RouteApiController;
import com.ryanair.searchflight.service.RouteFilterService;
import com.ryanair.searchflight.util.RouteFilter;

/**
 * @author sergiopf
 */
@Service
public class RouteServiceImpl implements RouteFilterService{
	
	@Autowired
	RouteApiController routesApiController;

	@Autowired
	RouteFilter routeFilter;
	
	/**
     * Routes with: connectingAirport set to null and operator set to RYANAIR should be used.
     * Result with all direct flights if available (for example: DUB - WRO)
     * and all interconnected flights (for example: DUB - STN - WRO)
     *	@param input Data from initial request
	 */
	@Override
	public AvailableRoutes getRoutes(Request request) {
		AvailableRoutes result = new AvailableRoutes();
		
		List<Route> listRoutes = routesApiController.getRoutes();

		result.setDirectFlight(routeFilter.filterDirectFlight(request, listRoutes));
		result.setInterconnectedFlights(routeFilter.filterInterconnectedFlight(request, listRoutes));
		
		return result;
	}

}
