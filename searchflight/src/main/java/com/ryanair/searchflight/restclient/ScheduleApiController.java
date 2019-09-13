package com.ryanair.searchflight.restclient;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ryanair.searchflight.config.PropertiesConstants;
import com.ryanair.searchflight.dto.Schedule;
import com.ryanair.searchflight.dto.Schedule.Year;
import com.ryanair.searchflight.dto.Schedule.Year.Month;

/**
 * @author sergiopf
 */
@Component
public class ScheduleApiController {

	private static final Logger log = LogManager.getLogger(ScheduleApiController.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	PropertiesConstants propertiesConstants;
	
	/**
    * Available flights for a given departure airport IATA code, an arrival airport IATA code, a year and a month.
	* Schedules API: https://servicesapi.ryanair.com/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}
    * @param input Data from initial request 
    * @param route 
    */
	public Schedule getSchedules(String airtportFrom, 
			String airportTo, 
			LocalDateTime departureDateTimeFormatted, 
			LocalDateTime arrivalDateTimeFormatted){
		
		Schedule schecule = initScheduleYearsMonths(departureDateTimeFormatted, arrivalDateTimeFormatted);
		
		for (Year year:schecule.getYears()) {
			for (Month month:year.getMonths()) {
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				
				String formatedUrlSchedules = propertiesConstants.getSchedules()
					+ "/" + airtportFrom
					+ "/" + airportTo
					+ "/years/" + String.valueOf(year.getYear())
					+ "/months/" + String.valueOf(month.getMonth());
				log.debug(formatedUrlSchedules);
				
				Month monthSchedule = restTemplate.getForObject(formatedUrlSchedules, Month.class);
				if (null != monthSchedule && null != monthSchedule.getDays())
					month.setDays(monthSchedule.getDays());
			}
		}
		return schecule;
	 }
	
	/**
	 * Build the object to insert schedule by year, month and days. Search months and years between two dates,
	 * add one month each iteration, if next date is before end it finishes.
	 * 
	 */
	private Schedule initScheduleYearsMonths(LocalDateTime start, LocalDateTime end) {
		Schedule schedule = new Schedule();
		List<Year> scheduleYears = schedule.getYears();
		for (LocalDateTime dateBucle = start; dateBucle.isBefore(end); dateBucle = dateBucle.plusDays(1)) {
	    	boolean findYear = false;
	    	for (Year scheduleYear : scheduleYears) {
	    		if (dateBucle.getYear() == scheduleYear.getYear()) {
	    			findYear = true;
	    			boolean findMonth = false;
	    			for (Month month : scheduleYear.getMonths()) {
	    				if (dateBucle.getMonthValue() == month.getMonth()) {
	    					findMonth=true;
	    					break;
	    				}
	    			}
	    			if (findMonth == false) {
	        			Month insertMonth = new Month();
	        			insertMonth.setMonth(dateBucle.getMonthValue());
	        			scheduleYear.getMonths().add(insertMonth);
	        		}
	    		}
	    	}
	    	
    		if (findYear==false) {
    			Year insertYear = new Year();
    			insertYear.setYear(dateBucle.getYear());
    			Month insertMonth = new Month();
    			insertMonth.setMonth(dateBucle.getMonthValue());
    			insertYear.getMonths().add(insertMonth);
    			scheduleYears.add(insertYear);
    		}
	    }

	    return schedule;
	}
	
}