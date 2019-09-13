package com.ryanair.searchflight.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import com.ryanair.searchflight.constraint.DateRange;

/**
 *  @DateTimeFormat, has problem to overwrite its  fixed message (Failed to convert property value of type change)
 *  so to create personalized messages we use a ConstraintValidator, and set LocalDateTime fields 
 *	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
 * @author sergi
 *
 */
@DateRange
public class Request implements Serializable{

	private static final long serialVersionUID = -687991492884056763L;
	
	@NotEmpty
	@Size(min = 3, max = 3, message = "{size.three}")
	String departure;

	@NotEmpty
	String departureDateTime;
	
	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
	LocalDateTime departureDateTimeFormatted;

	@NotEmpty
	@Size(min = 3, max = 3, message = "{size.three}")
	String arrival;
	
	@NotEmpty
	String arrivalDateTime;
	
	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
	LocalDateTime arrivalDateTimeFormatted;
	
	public String getDeparture() {
		return this.departure;
	}
	public void setDeparture(String departure) {
		this.departure = departure;
	}
	public String getDepartureDateTime() {
		return this.departureDateTime;
	}
	public void setDepartureDateTime(String departureDateTime) {
		this.departureDateTime = departureDateTime;
	}
	public LocalDateTime getDepartureDateTimeFormatted() {
		return this.departureDateTimeFormatted;
	}
	public void setDepartureDateTimeFormatted(LocalDateTime departureDateTimeFormatted) {
		this.departureDateTimeFormatted = departureDateTimeFormatted;
	}
	public String getArrival() {
		return this.arrival;
	}
	public void setArrival(String arrival) {
		this.arrival = arrival;
	}
	public String getArrivalDateTime() {
		return this.arrivalDateTime;
	}
	public void setArrivalDateTime(String arrivalDateTime) {
		this.arrivalDateTime = arrivalDateTime;
	}
	public LocalDateTime getArrivalDateTimeFormatted() {
		return this.arrivalDateTimeFormatted;
	}
	public void setArrivalDateTimeFormatted(LocalDateTime arrivalDateTimeFormatted) {
		this.arrivalDateTimeFormatted = arrivalDateTimeFormatted;
	}
	
}
