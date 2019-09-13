package com.ryanair.searchflight.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.doNothing;

import com.ryanair.searchflight.Application;
import com.ryanair.searchflight.config.CacheSchedulConfig;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class SearchRealControllerIT {
	
	private static final String URL_CONTROLLER = "/searchflight/3/steps/interconnections";
	private static final String PARAM_DEPARTURE = "departure";
	private static final String PARAM_ARRIVAL = "arrival";
	private static final String PARAM_DEPARTURE_DATE_TIME = "departureDateTime";
	private static final String PARAM_ARRIVAL_DATE_TIME = "arrivalDateTime";
	
	@MockBean
	private CacheSchedulConfig cacheSchedulConfig;
	
	@Autowired
	private MockMvc mvc;

	@Before
	public void setMocks() throws Exception{
		doNothing().when(cacheSchedulConfig).scheduleFilterRoutes();
	}
	
	/**
	 * Request URI, loading information from real rest APIs
	 * ?departure=DUB&arrival=WRO&departureDateTime=2019-12-31T07:00&arrivalDateTime=2020-01-02T07:00
	 * @throws Exception
	 */
	//@Ignore
	@Test
	public void getRoutesFromRealAPI() throws Exception {
		
		mvc.perform(get(URL_CONTROLLER)
				.param(PARAM_DEPARTURE, "DUB")
				.param(PARAM_ARRIVAL, "WRO")
				.param(PARAM_DEPARTURE_DATE_TIME, "2019-12-31T07:00")
				.param(PARAM_ARRIVAL_DATE_TIME, "2020-01-02T07:00")
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().isOk())
	      .andExpect(jsonPath("$", hasSize(9)))
	      .andExpect(jsonPath("$[0].stops").value("0"))
	      .andExpect(jsonPath("$[0].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[0].legs[0].departureDateTime").value("2019-12-31T07:30:00")).andExpect(jsonPath("$[0].legs[0].arrivalAirport").value("WRO")).andExpect(jsonPath("$[0].legs[0].arrivalDateTime").value("2019-12-31T11:05:00"))
	      .andExpect(jsonPath("$[0].legs[1].departureAirport").value("DUB")).andExpect(jsonPath("$[0].legs[1].departureDateTime").value("2020-01-01T19:50:00")).andExpect(jsonPath("$[0].legs[1].arrivalAirport").value("WRO")).andExpect(jsonPath("$[0].legs[1].arrivalDateTime").value("2020-01-01T23:25:00"))
	      .andExpect(jsonPath("$[1].stops").value("1"))
	      .andExpect(jsonPath("$[1].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[1].legs[0].departureDateTime").value("2020-01-01T08:05:00")).andExpect(jsonPath("$[1].legs[0].arrivalAirport").value("BLQ")).andExpect(jsonPath("$[1].legs[0].arrivalDateTime").value("2020-01-01T11:40:00"))
	      .andExpect(jsonPath("$[1].legs[1].departureAirport").value("BLQ")).andExpect(jsonPath("$[1].legs[1].departureDateTime").value("2020-01-01T16:35:00")).andExpect(jsonPath("$[1].legs[1].arrivalAirport").value("WRO")).andExpect(jsonPath("$[1].legs[1].arrivalDateTime").value("2020-01-01T18:20:00"))
	      .andExpect(jsonPath("$[2].stops").value("1"))
	      .andExpect(jsonPath("$[2].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[2].legs[0].departureDateTime").value("2019-12-31T12:30:00")).andExpect(jsonPath("$[2].legs[0].arrivalAirport").value("BRS")).andExpect(jsonPath("$[2].legs[0].arrivalDateTime").value("2019-12-31T13:35:00"))
	      .andExpect(jsonPath("$[2].legs[1].departureAirport").value("DUB")).andExpect(jsonPath("$[2].legs[1].departureDateTime").value("2019-12-31T18:40:00")).andExpect(jsonPath("$[2].legs[1].arrivalAirport").value("BRS")).andExpect(jsonPath("$[2].legs[1].arrivalDateTime").value("2019-12-31T19:45:00"))
	      .andExpect(jsonPath("$[2].legs[2].departureAirport").value("BRS")).andExpect(jsonPath("$[2].legs[2].departureDateTime").value("2020-01-01T09:20:00")).andExpect(jsonPath("$[2].legs[2].arrivalAirport").value("WRO")).andExpect(jsonPath("$[2].legs[2].arrivalDateTime").value("2020-01-01T12:35:00"))
	      .andExpect(jsonPath("$[3].stops").value("1"))
	      .andExpect(jsonPath("$[3].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[3].legs[0].departureDateTime").value("2019-12-31T16:30:00")).andExpect(jsonPath("$[3].legs[0].arrivalAirport").value("CIA")).andExpect(jsonPath("$[3].legs[0].arrivalDateTime").value("2019-12-31T20:35:00"))
	      .andExpect(jsonPath("$[3].legs[1].departureAirport").value("DUB")).andExpect(jsonPath("$[3].legs[1].departureDateTime").value("2020-01-01T08:25:00")).andExpect(jsonPath("$[3].legs[1].arrivalAirport").value("CIA")).andExpect(jsonPath("$[3].legs[1].arrivalDateTime").value("2020-01-01T12:30:00"))
	      .andExpect(jsonPath("$[3].legs[2].departureAirport").value("CIA")).andExpect(jsonPath("$[3].legs[2].departureDateTime").value("2020-01-01T16:15:00")).andExpect(jsonPath("$[3].legs[2].arrivalAirport").value("WRO")).andExpect(jsonPath("$[3].legs[2].arrivalDateTime").value("2020-01-01T18:20:00"))
	      .andExpect(jsonPath("$[4].stops").value("1"))
	      .andExpect(jsonPath("$[4].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[4].legs[0].departureDateTime").value("2019-12-31T15:55:00")).andExpect(jsonPath("$[4].legs[0].arrivalAirport").value("EMA")).andExpect(jsonPath("$[4].legs[0].arrivalDateTime").value("2019-12-31T17:00:00"))
	      .andExpect(jsonPath("$[4].legs[1].departureAirport").value("EMA")).andExpect(jsonPath("$[4].legs[1].departureDateTime").value("2020-01-01T17:05:00")).andExpect(jsonPath("$[4].legs[1].arrivalAirport").value("WRO")).andExpect(jsonPath("$[4].legs[1].arrivalDateTime").value("2020-01-01T20:20:00"))
	      .andExpect(jsonPath("$[5].stops").value("1"))
	      .andExpect(jsonPath("$[5].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[5].legs[0].departureDateTime").value("2019-12-31T07:05:00")).andExpect(jsonPath("$[5].legs[0].arrivalAirport").value("GLA")).andExpect(jsonPath("$[5].legs[0].arrivalDateTime").value("2019-12-31T08:15:00"))
	      .andExpect(jsonPath("$[5].legs[1].departureAirport").value("GLA")).andExpect(jsonPath("$[5].legs[1].departureDateTime").value("2019-12-31T13:25:00")).andExpect(jsonPath("$[5].legs[1].arrivalAirport").value("WRO")).andExpect(jsonPath("$[5].legs[1].arrivalDateTime").value("2019-12-31T16:55:00"))
	      .andExpect(jsonPath("$[6].stops").value("1"))
	      .andExpect(jsonPath("$[6].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[6].legs[0].departureDateTime").value("2019-12-31T07:05:00")).andExpect(jsonPath("$[6].legs[0].arrivalAirport").value("LPL")).andExpect(jsonPath("$[6].legs[0].arrivalDateTime").value("2019-12-31T08:00:00"))
	      .andExpect(jsonPath("$[6].legs[1].departureAirport").value("LPL")).andExpect(jsonPath("$[6].legs[1].departureDateTime").value("2019-12-31T14:30:00")).andExpect(jsonPath("$[6].legs[1].arrivalAirport").value("WRO")).andExpect(jsonPath("$[6].legs[1].arrivalDateTime").value("2019-12-31T17:45:00"))
	      .andExpect(jsonPath("$[7].stops").value("1"))
	      .andExpect(jsonPath("$[7].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[0].departureDateTime").value("2019-12-31T10:25:00")).andExpect(jsonPath("$[7].legs[0].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[0].arrivalDateTime").value("2019-12-31T11:25:00"))
	      .andExpect(jsonPath("$[7].legs[1].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[1].departureDateTime").value("2019-12-31T13:50:00")).andExpect(jsonPath("$[7].legs[1].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[1].arrivalDateTime").value("2019-12-31T14:50:00"))
	      .andExpect(jsonPath("$[7].legs[2].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[2].departureDateTime").value("2019-12-31T15:55:00")).andExpect(jsonPath("$[7].legs[2].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[2].arrivalDateTime").value("2019-12-31T17:00:00"))
	      .andExpect(jsonPath("$[7].legs[3].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[3].departureDateTime").value("2020-01-01T10:25:00")).andExpect(jsonPath("$[7].legs[3].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[3].arrivalDateTime").value("2020-01-01T11:25:00"))
	      .andExpect(jsonPath("$[7].legs[4].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[4].departureDateTime").value("2020-01-01T13:50:00")).andExpect(jsonPath("$[7].legs[4].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[4].arrivalDateTime").value("2020-01-01T14:50:00"))
	      .andExpect(jsonPath("$[7].legs[5].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[5].departureDateTime").value("2020-01-01T16:30:00")).andExpect(jsonPath("$[7].legs[5].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[5].arrivalDateTime").value("2020-01-01T17:35:00"))
	      .andExpect(jsonPath("$[7].legs[6].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[6].departureDateTime").value("2020-01-01T20:30:00")).andExpect(jsonPath("$[7].legs[6].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[6].arrivalDateTime").value("2020-01-01T21:35:00"))
	      .andExpect(jsonPath("$[7].legs[7].departureAirport").value("MAN")).andExpect(jsonPath("$[7].legs[7].departureDateTime").value("2020-01-02T21:00:00")).andExpect(jsonPath("$[7].legs[7].arrivalAirport").value("WRO")).andExpect(jsonPath("$[7].legs[7].arrivalDateTime").value("2020-01-02T00:15:00"))
	      .andExpect(jsonPath("$[8].stops").value("1"))
	      .andExpect(jsonPath("$[8].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[0].departureDateTime").value("2019-12-31T08:20:00")).andExpect(jsonPath("$[8].legs[0].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[0].arrivalDateTime").value("2019-12-31T09:40:00"))
	      .andExpect(jsonPath("$[8].legs[1].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[1].departureDateTime").value("2019-12-31T11:55:00")).andExpect(jsonPath("$[8].legs[1].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[1].arrivalDateTime").value("2019-12-31T13:15:00"))
	      .andExpect(jsonPath("$[8].legs[2].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[2].departureDateTime").value("2019-12-31T15:30:00")).andExpect(jsonPath("$[8].legs[2].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[2].arrivalDateTime").value("2019-12-31T16:50:00"))
	      .andExpect(jsonPath("$[8].legs[3].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[3].departureDateTime").value("2019-12-31T16:45:00")).andExpect(jsonPath("$[8].legs[3].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[3].arrivalDateTime").value("2019-12-31T18:10:00"))
	      .andExpect(jsonPath("$[8].legs[4].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[4].departureDateTime").value("2019-12-31T18:50:00")).andExpect(jsonPath("$[8].legs[4].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[4].arrivalDateTime").value("2019-12-31T20:10:00"))
	      .andExpect(jsonPath("$[8].legs[5].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[5].departureDateTime").value("2020-01-01T09:50:00")).andExpect(jsonPath("$[8].legs[5].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[5].arrivalDateTime").value("2020-01-01T11:15:00"))
	      .andExpect(jsonPath("$[8].legs[6].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[6].departureDateTime").value("2020-01-01T11:55:00")).andExpect(jsonPath("$[8].legs[6].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[6].arrivalDateTime").value("2020-01-01T13:15:00"))
	      .andExpect(jsonPath("$[8].legs[7].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[7].departureDateTime").value("2020-01-01T13:50:00")).andExpect(jsonPath("$[8].legs[7].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[7].arrivalDateTime").value("2020-01-01T15:10:00"))
	      .andExpect(jsonPath("$[8].legs[8].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[8].departureDateTime").value("2020-01-01T15:30:00")).andExpect(jsonPath("$[8].legs[8].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[8].arrivalDateTime").value("2020-01-01T16:50:00"))
	      .andExpect(jsonPath("$[8].legs[9].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[9].departureDateTime").value("2020-01-01T16:45:00")).andExpect(jsonPath("$[8].legs[9].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[9].arrivalDateTime").value("2020-01-01T18:10:00"))
	      .andExpect(jsonPath("$[8].legs[10].departureAirport").value("STN")).andExpect(jsonPath("$[8].legs[10].departureDateTime").value("2020-01-01T20:30:00")).andExpect(jsonPath("$[8].legs[10].arrivalAirport").value("WRO")).andExpect(jsonPath("$[8].legs[10].arrivalDateTime").value("2020-01-01T23:30:00"));
	}
}
