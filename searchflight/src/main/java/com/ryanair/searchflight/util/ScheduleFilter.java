package com.ryanair.searchflight.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ryanair.searchflight.dto.Request;
import com.ryanair.searchflight.dto.Route;
import com.ryanair.searchflight.dto.Schedule;
import com.ryanair.searchflight.dto.Response.Leg;
import com.ryanair.searchflight.dto.Schedule.Year;
import com.ryanair.searchflight.dto.Schedule.Year.Month;
import com.ryanair.searchflight.dto.Schedule.Year.Month.Days;
import com.ryanair.searchflight.dto.Schedule.Year.Month.Days.Flight;

@Component
public class ScheduleFilter {

	public List<Leg> filterLegsDirectFlight(Request request, 
			Route noStopRoute, 
			Schedule noStopRouteSchedule) {
		
		List<Leg> legs = new ArrayList<>();

		for (Year yearScheduled : noStopRouteSchedule.getYears()){
			for (Month monthScheduled : yearScheduled.getMonths()){
				for (Days dayScheduled : monthScheduled.getDays()) {
					for (Flight flightScheduled : dayScheduled.getFlights()) {
						
						String[] departureHourMinute = flightScheduled.getDepartureTime().split(":");
						LocalDateTime departureDateTime=LocalDateTime.of(yearScheduled.getYear(), 
								monthScheduled.getMonth(), 
								dayScheduled.getDay(), 
								Integer.parseInt(departureHourMinute[0]), 
								Integer.parseInt(departureHourMinute[1]));
						
						if (departureDateTime.equals(request.getDepartureDateTimeFormatted())
								|| departureDateTime.isAfter(request.getDepartureDateTimeFormatted())) {
						
							String[] arrivalHourMinute = flightScheduled.getArrivalTime().split(":");
							LocalDateTime arrivalDateTime=LocalDateTime.of(yearScheduled.getYear(), 
									monthScheduled.getMonth(), 
									dayScheduled.getDay(), 
									Integer.parseInt(arrivalHourMinute[0]), 
									Integer.parseInt(arrivalHourMinute[1]));
							
							if (arrivalDateTime.equals(request.getArrivalDateTimeFormatted())
									|| arrivalDateTime.isBefore(request.getArrivalDateTimeFormatted())){
								Leg leg = new Leg(noStopRoute.getAirportFrom(),
									departureDateTime,
									noStopRoute.getAirportTo(),
									arrivalDateTime);
								legs.add(leg);
							}
						}
					}
				}
			}
		}

		return legs;
	}

	/**
	 * For interconnected flights the difference between the arrival and the next departure
	 * should be 2h or greater.
	 */
	public List<Leg> filterLegsInterconnectedFlight(Request request, 
			Route routeFrom, 
			Schedule interconnectedFlighScheduleFrom,
			Route routeTo, 
			Schedule interconnectedFlighScheduleTo) {

		List<Leg> legs = new ArrayList<>();

		// Interconnected flights FROM, calculate the time from the earliest departure to remove interconnected flights to, 
		// which are before this time calculated (subtract -2h)
		LocalDateTime earlierFromArrival = null;
		for (Year yearScheduled : interconnectedFlighScheduleFrom.getYears()) {
			for (Month monthScheduled : yearScheduled.getMonths()) {
				for (Days dayScheduledFrom : monthScheduled.getDays()) {
					for (Flight flightScheduledFrom : dayScheduledFrom.getFlights()) {
						
						String[] departureHourMinute = flightScheduledFrom.getDepartureTime().split(":");
						LocalDateTime departureDateTime = LocalDateTime.of(yearScheduled.getYear(), 
								monthScheduled.getMonth(), 
								dayScheduledFrom.getDay(), 
								Integer.parseInt(departureHourMinute[0]), 
								Integer.parseInt(departureHourMinute[1]));
		
						if (departureDateTime.isEqual(request.getDepartureDateTimeFormatted())
								|| departureDateTime.isAfter(request.getDepartureDateTimeFormatted())){
							String[] arrivalHourMinute = flightScheduledFrom.getArrivalTime().split(":");
							LocalDateTime arrivalDateTime = LocalDateTime.of(yearScheduled.getYear(), 
									monthScheduled.getMonth(), 
									dayScheduledFrom.getDay(), 
									Integer.parseInt(arrivalHourMinute[0]), 
									Integer.parseInt(arrivalHourMinute[1]));
		
							if (arrivalDateTime.isEqual(request.getArrivalDateTimeFormatted())
									|| arrivalDateTime.isBefore(request.getArrivalDateTimeFormatted())){
								Leg leg = new Leg(routeFrom.getAirportFrom(),
									departureDateTime,
									routeFrom.getAirportTo(),
									arrivalDateTime);
								legs.add(leg);

								if (null == earlierFromArrival
										|| arrivalDateTime.isBefore(earlierFromArrival))
									earlierFromArrival = arrivalDateTime;
							}
						}
					}
				}
			}
		}
	
		// Interconnected flights TO, calculate the time from the latest departure to remove interconnected flights from 
		// that can't achieve those departures, which arrive after this time calculated (subtract -2h)
		LocalDateTime latestToDeparture = null;
		if (null != earlierFromArrival) {
			earlierFromArrival = earlierFromArrival.plusHours(2L);
			for (Year yearScheduled : interconnectedFlighScheduleTo.getYears()) {
				for (Month monthScheduled : yearScheduled.getMonths()) {
					for (Days dayScheduledTo : monthScheduled.getDays()) {
						for (Flight flightScheduledTo : dayScheduledTo.getFlights()) {
			
							String[] departureHourMinute = flightScheduledTo.getDepartureTime().split(":");
							LocalDateTime departureDateTime = LocalDateTime.of(yearScheduled.getYear(), 
									monthScheduled.getMonth(), 
									dayScheduledTo.getDay(), 
									Integer.parseInt(departureHourMinute[0]), 
									Integer.parseInt(departureHourMinute[1]));
		
							if ((departureDateTime.isEqual(earlierFromArrival)
									|| departureDateTime.isAfter(earlierFromArrival))) {
							
								String[] arrivalHourMinute = flightScheduledTo.getArrivalTime().split(":");
								LocalDateTime arrivalDateTime = LocalDateTime.of(yearScheduled.getYear(), 
										monthScheduled.getMonth(), 
										dayScheduledTo.getDay(), 
										Integer.parseInt(arrivalHourMinute[0]), 
										Integer.parseInt(arrivalHourMinute[1]));
		
								if (arrivalDateTime.isEqual(request.getArrivalDateTimeFormatted())
										|| arrivalDateTime.isBefore(request.getArrivalDateTimeFormatted())){
									Leg leg = new Leg(routeTo.getAirportFrom(),
										departureDateTime,
										routeTo.getAirportTo(),
										arrivalDateTime);
									legs.add(leg);

									if (null == latestToDeparture
											|| departureDateTime.isAfter(latestToDeparture))
										latestToDeparture = departureDateTime;
								}
							}
						}
					}
				}
			}
		}
		
		// Search interconnected flights from and remove when arrival date is after departure, 
		// if some of both list is empty, we delete results.
		// If the time of any list doesn't exists it means one of the lists was empty and return null
		if (null != latestToDeparture
				&& null != earlierFromArrival) {
			latestToDeparture= latestToDeparture.minusHours(2L);
			Iterator<Leg> itLegs = legs.iterator();
			while (itLegs.hasNext()) {
				Leg itLeg = itLegs.next();
				if (itLeg.getDepartureAirport().equals(routeFrom.getAirportFrom())
						&& (itLeg.getArrivalDateTime().isAfter(latestToDeparture))) {
					itLegs.remove();
				}
			}
		}else {
			legs = new ArrayList<>();
		}

		return legs;
	}
}
