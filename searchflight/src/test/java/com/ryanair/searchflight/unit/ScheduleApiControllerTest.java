package com.ryanair.searchflight.unit;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.time.LocalDateTime;
import com.ryanair.searchflight.dto.Schedule.Year.Month;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanair.searchflight.config.PropertiesConstants;
import com.ryanair.searchflight.dto.Schedule;
import com.ryanair.searchflight.restclient.ScheduleApiController;


@RunWith(SpringRunner.class)
public class ScheduleApiControllerTest {
	
	private static final String  URL_SCHEDULES = "https://FFFF/schedules";
	
	@Mock
	private PropertiesConstants propertiesConstants;
	
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private ScheduleApiController scheduleApiController;
	
	/**
	 * Check that the schedules structure is correct
	 * @throws Exception
	 */
	@Test
	public void testSchedulesFromFiles() throws Exception{
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		Mockito.when(propertiesConstants.getSchedules()).thenReturn(URL_SCHEDULES);
		Mockito.when(restTemplate.getForObject(propertiesConstants.getSchedules()+"/DUB/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_WRO_2019_12.json"),  Month.class));
		Mockito.when(restTemplate.getForObject(propertiesConstants.getSchedules()+"/DUB/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_WRO_2020_1.json"), Month.class));

		LocalDateTime departureDateTimeFormatted = LocalDateTime.of(2119, java.time.Month.DECEMBER, 31, 07, 00);
		LocalDateTime arrivalDateTimeFormatted = LocalDateTime.of(2120, java.time.Month.JANUARY, 2, 07, 00);
		Schedule schedule = scheduleApiController.getSchedules("DUB", "WRO", departureDateTimeFormatted, arrivalDateTimeFormatted);
		assertThat(schedule.getYears(), hasSize(2));
		assertThat(schedule.getYears().get(0).getMonths(), hasSize(1));
		assertThat(schedule.getYears().get(0).getMonths().get(0).getDays(), hasSize(23));
		assertThat(schedule.getYears().get(1).getMonths(), hasSize(1));
		assertThat(schedule.getYears().get(1).getMonths().get(0).getDays(), hasSize(24));
	}

}
