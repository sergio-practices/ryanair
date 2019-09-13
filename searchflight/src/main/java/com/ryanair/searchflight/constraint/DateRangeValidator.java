package com.ryanair.searchflight.constraint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.ryanair.searchflight.dto.Request;
 
public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {
    
	private static final String FIELD_DEPARTURE_DATE_TIME = "departureDateTime";
	private static final String FIELD_ARRIVAL_DATE_TIME = "arrivalDateTime";
    
    /**
     * Validation on input params wich are not inside @Validated (NotEmpty are controller by annotation)
     * No more than 7 days each search (168 hours) or wrong date format
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
    	if (null != value) {
	    	DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	    	if (null != ((Request)value).getDepartureDateTime()) {
		    	try {
		    		((Request)value).setDepartureDateTimeFormatted(LocalDateTime.parse(((Request)value).getDepartureDateTime(), formatter));
		    	}catch(DateTimeParseException ex) {
		    		context.buildConstraintViolationWithTemplate("{time.format}")
		            .addPropertyNode(FIELD_DEPARTURE_DATE_TIME)
		            .addConstraintViolation();
		    	}
	    	}
	    	
	    	if (null != ((Request)value).getArrivalDateTime()) {
		    	try {
			    	((Request)value).setArrivalDateTimeFormatted(LocalDateTime.parse(((Request)value).getArrivalDateTime(), formatter));
				}catch(DateTimeParseException ex) {
		    		context.buildConstraintViolationWithTemplate("{time.format}")
		            .addPropertyNode(FIELD_ARRIVAL_DATE_TIME)
		            .addConstraintViolation();
				}
	    	}
	    	
	    	if ( null != ((Request)value).getDepartureDateTimeFormatted()
	    			&& null != ((Request)value).getArrivalDateTimeFormatted()) {
	        	int days = (int) (((Request)value).getDepartureDateTimeFormatted().until(((Request)value).getArrivalDateTimeFormatted(), ChronoUnit.HOURS));
	    		if (days < 0 || days > 168) {
		    		context.buildConstraintViolationWithTemplate("{departureTime.range}")
		            .addPropertyNode(FIELD_DEPARTURE_DATE_TIME)
		            .addConstraintViolation();
	    			return false;
	    		}else {
	    			return true;
	    		}
			}
    	}
    	return false;
    }
}
