package com.ryanair.searchflight.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class Response implements Serializable, Comparable<Response>{

	private static final long serialVersionUID = -687991492884005033L;
	
	private int stops;
	public int getStops() {
		return this.stops;
	}
	public void setStops(int stops) {
		this.stops = stops;
	}

	public List<Leg> legs;
	public List<Leg> getLegs() {
		return this.legs;
	}
	public void setLegs(List<Leg> legs) {
		this.legs = legs;
	}

	public Response (int stops, List<Leg> legs){
		this.stops = stops;
		this.legs = legs;
	}
	
	public static class Leg implements Serializable{
		
		private static final long serialVersionUID = -344365676768778411L;
		
		String departureAirport;

		@DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) 
		LocalDateTime departureDateTime;

		String arrivalAirport;

		@DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) 
		LocalDateTime arrivalDateTime;
		
		public Leg(String departureAirport, LocalDateTime departureDateTime, String arrivalAirport, LocalDateTime arrivalDateTime){
			this.departureAirport = departureAirport;
			this.departureDateTime = departureDateTime;
			this.arrivalAirport = arrivalAirport;
			this.arrivalDateTime = arrivalDateTime;
		}
		
		public String getDepartureAirport() {
			return this.departureAirport;
		}
		public void setDepartureAirport(String departureAirport) {
			this.departureAirport = departureAirport;
		}
		public LocalDateTime getDepartureDateTime() {
			return this.departureDateTime;
		}
		public void setDepartureDateTime(LocalDateTime departureDateTime) {
			this.departureDateTime = departureDateTime;
		}
		public String getArrivalAirport() {
			return this.arrivalAirport;
		}
		public void setArrivalAirport(String arrivalAirport) {
			this.arrivalAirport = arrivalAirport;
		}
		public LocalDateTime getArrivalDateTime() {
			return this.arrivalDateTime;
		}
		public void setArrivalDateTime(LocalDateTime arrivalDateTime) {
			this.arrivalDateTime = arrivalDateTime;
		}
	}
	
	
	/**
	 * Order by sequence "stops":0,"departureAirport":"DUB","arrivalAirport":"WRO" (0DUBBRO)
	 */
    @Override
    public int compareTo(Response compareResponse) {
    	List<Leg> compareLegs = compareResponse.getLegs();
    	Leg compareLeg = compareLegs.get(0);
    	
    	List<Leg> thisLegs = this.getLegs();
    	Leg thisLeg = thisLegs.get(0);
    	
        String compareDepartureArrival=compareResponse.getStops()+compareLeg.getDepartureAirport()+compareLeg.getArrivalAirport();
        String thisDepartureArrival = this.getStops() + thisLeg.getDepartureAirport() + thisLeg.getArrivalAirport();
        
        return thisDepartureArrival.compareTo(compareDepartureArrival);
    }
}
