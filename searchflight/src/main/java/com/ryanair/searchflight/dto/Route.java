package com.ryanair.searchflight.dto;

import java.io.Serializable;

public class Route implements Serializable{

	private static final long serialVersionUID = -687991492886785033L;

	private String airportFrom;
	private String airportTo;
	private String connectingAirport;
	private String operator;

	public String getAirportFrom() {
		return this.airportFrom;
	}
	public void setAirportFrom(String airportFrom) {
		this.airportFrom = airportFrom;
	}
	public String getAirportTo() {
		return this.airportTo;
	}
	public void setAirportTo(String airportTo) {
		this.airportTo = airportTo;
	}
	public String getConnectingAirport() {
		return this.connectingAirport;
	}
	public void setConnectingAirport(String connectingAirport) {
		this.connectingAirport = connectingAirport;
	}
	public String getOperator() {
		return this.operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
}
