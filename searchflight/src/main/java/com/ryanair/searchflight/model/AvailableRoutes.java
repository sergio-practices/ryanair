package com.ryanair.searchflight.model;

import java.util.List;

import com.ryanair.searchflight.dto.Route;

/**
 * @author sergiopf
 */
public class AvailableRoutes {

	private Route directFlight;
	public Route getDirectFlight() {
		return this.directFlight;
	}
	public void setDirectFlight(Route directFlight) {
		this.directFlight = directFlight;
	}
	
	public List<FromToInterconnectedFlight> interconnectedFlights;
	public List<FromToInterconnectedFlight> getInterconnectedFlights() {
		return this.interconnectedFlights;
	}
	public void setInterconnectedFlights(List<FromToInterconnectedFlight> interconnectedFlights) {
		this.interconnectedFlights = interconnectedFlights;
	}
	public static class FromToInterconnectedFlight {
		private Route from;
		private Route to;
	
		public Route getFrom() {
			return this.from;
		}
		public void setFrom(Route from) {
			this.from = from;
		}
		public Route getTo() {
			return this.to;
		}
		public void setTo(Route to) {
			this.to = to;
		}
	}
}
