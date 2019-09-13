package com.ryanair.searchflight.integration;


import java.io.File;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.hamcrest.Matchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

import com.ryanair.searchflight.Application;
import com.ryanair.searchflight.config.CacheSchedulConfig;
import com.ryanair.searchflight.config.PropertiesConstants;
import com.ryanair.searchflight.dto.Route;
import com.ryanair.searchflight.dto.Schedule.Year.Month;
import com.ryanair.searchflight.exception.RestTemplateNotFoundException;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class SearchFileControllerIT {
	
	private static final String URL_CONTROLLER = "/searchflight/3/steps/interconnections";
	private static final String PARAM_DEPARTURE = "departure";
	private static final String PARAM_ARRIVAL = "arrival";
	private static final String PARAM_DEPARTURE_DATE_TIME = "departureDateTime";
	private static final String PARAM_ARRIVAL_DATE_TIME = "arrivalDateTime";
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private PropertiesConstants propertiesConstants;
	
	@MockBean
	private CacheSchedulConfig cacheSchedulConfig;
	
	@MockBean
	private RestTemplate restTemplate;
	
	/**
	 * Init restclient responses from json files at src/test/java/data/* with information for the request 
	 * ?departure=DUB&arrival=WRO&departureDateTime=2019-12-31T07:00&arrivalDateTime=2020-01-02T07:00 
	 * @throws Exception
	 */
	@Before
	public void setMocks() throws Exception{
		
		doNothing().when(cacheSchedulConfig).scheduleFilterRoutes();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		CollectionType typeReference = TypeFactory.defaultInstance().constructCollectionType(List.class, Route.class);
		List<Route> routes = mapper.readValue(new File("src\\test\\java\\data\\ROUTES.json"), typeReference);
		ResponseEntity<List<Route>> response = new ResponseEntity<>(routes, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(eq(propertiesConstants.getRoutes()), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class))).thenReturn(response);
		
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/ALC/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\ALC_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/ALC/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\ALC_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/AGP/WRO/years/2119/months/12", Month.class)).thenThrow(new RestTemplateNotFoundException("https://services-api.ryanair.com/timtbl/3/schedules/AGP/WRO/years/2119/months/12")); //Empty
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/ATH/WRO/years/2119/months/12", Month.class)).thenThrow(new RestTemplateNotFoundException("https://services-api.ryanair.com/timtbl/3/schedules/ATH/WRO/years/2119/months/12")); //Empty
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/BGY/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\BGY_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/BGY/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\BGY_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/BLQ/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\BLQ_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/BLQ/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\BLQ_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/BRS/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\BRS_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/BRS/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\BRS_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/BVA/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\BVA_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/BVA/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\BVA_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/CIA/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\CIA_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/CIA/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\CIA_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/CRL/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\CRL_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/CRL/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\CRL_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/AGP/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_AGP_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/AGP/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_AGP_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/ALC/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_ALC_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/ALC/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_ALC_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/ATH/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_ATH_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/ATH/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_ATH_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/BGY/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_BGY_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/BGY/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_BGY_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/BLQ/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_BLQ_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/BLQ/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_BLQ_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/BRS/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_BRS_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/BRS/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_BRS_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/BVA/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_BVA_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/BVA/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_BVA_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/CHQ/years/2119/months/12", Month.class)).thenThrow(new RestTemplateNotFoundException("https://services-api.ryanair.com/timtbl/3/schedules/DUB/CHQ/years/2119/months/12")); //Empty
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/CIA/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_CIA_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/CIA/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_CIA_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/CRL/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_CRL_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/CRL/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_CRL_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/EDI/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_EDI_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/EDI/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_EDI_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/EMA/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_EMA_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/EMA/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_EMA_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/FAO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_FAO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/FAO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_FAO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/GDN/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_GDN_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/GDN/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_GDN_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/GLA/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_GLA_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/GLA/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_GLA_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/GRO/years/2119/months/12", Month.class)).thenThrow(new RestTemplateNotFoundException("https://services-api.ryanair.com/timtbl/3/schedules/DUB/GRO/years/2119/months/12")); //Empty
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/KBP/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_KBP_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/KBP/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_KBP_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/KBP/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_KBP_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/LBA/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_LBA_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/LBA/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_LBA_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/LIS/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_LIS_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/LIS/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_LIS_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/LPL/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_LPL_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/LPL/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_LPL_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/MAD/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_MAD_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/MAD/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_MAD_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/MAN/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_MAN_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/MAN/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_MAN_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/MLA/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_MLA_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/MLA/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_MLA_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/NAP/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_NAP_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/NAP/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_NAP_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/NCL/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_NCL_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/NCL/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_NCL_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/PMI/years/2119/months/12", Month.class)).thenThrow(new RestTemplateNotFoundException("https://services-api.ryanair.com/timtbl/3/schedules/DUB/PMI/years/2119/months/12")); //Empty
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/PMO/years/2119/months/12", Month.class)).thenThrow(new RestTemplateNotFoundException("https://services-api.ryanair.com/timtbl/3/schedules/DUB/PMO/years/2119/months/12")); //Empty
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/RYG/years/2119/months/12", Month.class)).thenThrow(new RestTemplateNotFoundException("https://services-api.ryanair.com/timtbl/3/schedules/DUB/RYG/years/2119/months/12")); //Empty
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/STN/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_STN_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/STN/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_STN_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/TFS/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_TFS_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/TFS/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_TFS_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/DUB/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\DUB_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/EDI/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\EDI_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/EDI/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\EDI_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/EMA/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\EMA_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/EMA/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\EMA_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/FAO/WRO/years/2119/months/12", Month.class)).thenThrow(new RestTemplateNotFoundException("https://services-api.ryanair.com/timtbl/3/schedules/FAO/WRO/years/2119/months/12")); //Empty
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/GDN/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\GDN_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/GDN/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\GDN_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/GLA/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\GLA_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/GLA/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\GLA_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/KBP/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\KBP_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/KBP/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\KBP_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/LBA/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\LBA_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/LBA/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\LBA_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/LIS/WRO/years/2119/months/12", Month.class)).thenThrow(new RestTemplateNotFoundException("https://services-api.ryanair.com/timtbl/3/schedules/LIS/WRO/years/2119/months/12")); //Empty
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/LPL/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\LPL_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/LPL/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\LPL_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/MAD/WRO/years/2119/months/12", Month.class)).thenThrow(new RestTemplateNotFoundException("https://services-api.ryanair.com/timtbl/3/schedules/MAD/WRO/years/2119/months/12")); //Empty
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/MAN/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\MAN_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/MAN/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\MAN_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/MLA/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\MLA_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/MLA/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\MLA_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/NAP/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\NAP_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/NAP/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\NAP_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/NCL/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\NCL_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/NCL/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\NCL_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/STN/WRO/years/2120/months/1", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\STN_WRO_2020_1.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/STN/WRO/years/2119/months/12", Month.class)).thenReturn(mapper.readValue(new File("src\\test\\java\\data\\STN_WRO_2019_12.json"), Month.class));
		Mockito.when(restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/TFS/WRO/years/2119/months/12", Month.class)).thenThrow(new RestTemplateNotFoundException("https://services-api.ryanair.com/timtbl/3/schedules/TFS/WRO/years/2119/months/12")); //Empty
	}
	
	
	/**
	 * Request URI, loading information from files to date
	 * ?departure=DUB&arrival=WRO&departureDateTime=2019-12-31T07:00&arrivalDateTime=2020-01-02T07:00
	 * simulating next century to avoid dates validation
	 * ?departure=DUB&arrival=WRO&departureDateTime=2119-12-31T07:00&arrivalDateTime=2120-01-02T07:00
	 * @throws Exception
	 */
	@Test
	public void getRoutesFromFiles() throws Exception {
		mvc.perform(get(URL_CONTROLLER)
				.param(PARAM_DEPARTURE, "DUB")
				.param(PARAM_ARRIVAL, "WRO")
				.param(PARAM_DEPARTURE_DATE_TIME, "2119-12-31T07:00")
				.param(PARAM_ARRIVAL_DATE_TIME, "2120-01-02T07:00")
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().isOk())
	      .andExpect(jsonPath("$", hasSize(9)))
	      .andExpect(jsonPath("$[0].stops").value("0"))
	      .andExpect(jsonPath("$[0].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[0].legs[0].departureDateTime").value("2119-12-31T07:30:00")).andExpect(jsonPath("$[0].legs[0].arrivalAirport").value("WRO")).andExpect(jsonPath("$[0].legs[0].arrivalDateTime").value("2119-12-31T11:05:00"))
	      .andExpect(jsonPath("$[0].legs[1].departureAirport").value("DUB")).andExpect(jsonPath("$[0].legs[1].departureDateTime").value("2120-01-01T19:50:00")).andExpect(jsonPath("$[0].legs[1].arrivalAirport").value("WRO")).andExpect(jsonPath("$[0].legs[1].arrivalDateTime").value("2120-01-01T23:25:00"))
	      .andExpect(jsonPath("$[1].stops").value("1"))
	      .andExpect(jsonPath("$[1].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[1].legs[0].departureDateTime").value("2120-01-01T08:05:00")).andExpect(jsonPath("$[1].legs[0].arrivalAirport").value("BLQ")).andExpect(jsonPath("$[1].legs[0].arrivalDateTime").value("2120-01-01T11:40:00"))
	      .andExpect(jsonPath("$[1].legs[1].departureAirport").value("BLQ")).andExpect(jsonPath("$[1].legs[1].departureDateTime").value("2120-01-01T16:35:00")).andExpect(jsonPath("$[1].legs[1].arrivalAirport").value("WRO")).andExpect(jsonPath("$[1].legs[1].arrivalDateTime").value("2120-01-01T18:20:00"))
	      .andExpect(jsonPath("$[2].stops").value("1"))
	      .andExpect(jsonPath("$[2].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[2].legs[0].departureDateTime").value("2119-12-31T12:30:00")).andExpect(jsonPath("$[2].legs[0].arrivalAirport").value("BRS")).andExpect(jsonPath("$[2].legs[0].arrivalDateTime").value("2119-12-31T13:35:00"))
	      .andExpect(jsonPath("$[2].legs[1].departureAirport").value("DUB")).andExpect(jsonPath("$[2].legs[1].departureDateTime").value("2119-12-31T18:40:00")).andExpect(jsonPath("$[2].legs[1].arrivalAirport").value("BRS")).andExpect(jsonPath("$[2].legs[1].arrivalDateTime").value("2119-12-31T19:45:00"))
	      .andExpect(jsonPath("$[2].legs[2].departureAirport").value("BRS")).andExpect(jsonPath("$[2].legs[2].departureDateTime").value("2120-01-01T09:20:00")).andExpect(jsonPath("$[2].legs[2].arrivalAirport").value("WRO")).andExpect(jsonPath("$[2].legs[2].arrivalDateTime").value("2120-01-01T12:35:00"))
	      .andExpect(jsonPath("$[3].stops").value("1"))
	      .andExpect(jsonPath("$[3].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[3].legs[0].departureDateTime").value("2119-12-31T16:30:00")).andExpect(jsonPath("$[3].legs[0].arrivalAirport").value("CIA")).andExpect(jsonPath("$[3].legs[0].arrivalDateTime").value("2119-12-31T20:35:00"))
	      .andExpect(jsonPath("$[3].legs[1].departureAirport").value("DUB")).andExpect(jsonPath("$[3].legs[1].departureDateTime").value("2120-01-01T08:25:00")).andExpect(jsonPath("$[3].legs[1].arrivalAirport").value("CIA")).andExpect(jsonPath("$[3].legs[1].arrivalDateTime").value("2120-01-01T12:30:00"))
	      .andExpect(jsonPath("$[3].legs[2].departureAirport").value("CIA")).andExpect(jsonPath("$[3].legs[2].departureDateTime").value("2120-01-01T16:15:00")).andExpect(jsonPath("$[3].legs[2].arrivalAirport").value("WRO")).andExpect(jsonPath("$[3].legs[2].arrivalDateTime").value("2120-01-01T18:20:00"))
	      .andExpect(jsonPath("$[4].stops").value("1"))
	      .andExpect(jsonPath("$[4].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[4].legs[0].departureDateTime").value("2119-12-31T15:55:00")).andExpect(jsonPath("$[4].legs[0].arrivalAirport").value("EMA")).andExpect(jsonPath("$[4].legs[0].arrivalDateTime").value("2119-12-31T17:00:00"))
	      .andExpect(jsonPath("$[4].legs[1].departureAirport").value("EMA")).andExpect(jsonPath("$[4].legs[1].departureDateTime").value("2120-01-01T17:05:00")).andExpect(jsonPath("$[4].legs[1].arrivalAirport").value("WRO")).andExpect(jsonPath("$[4].legs[1].arrivalDateTime").value("2120-01-01T20:20:00"))
	      .andExpect(jsonPath("$[5].stops").value("1"))
	      .andExpect(jsonPath("$[5].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[5].legs[0].departureDateTime").value("2119-12-31T07:05:00")).andExpect(jsonPath("$[5].legs[0].arrivalAirport").value("GLA")).andExpect(jsonPath("$[5].legs[0].arrivalDateTime").value("2119-12-31T08:15:00"))
	      .andExpect(jsonPath("$[5].legs[1].departureAirport").value("GLA")).andExpect(jsonPath("$[5].legs[1].departureDateTime").value("2119-12-31T13:25:00")).andExpect(jsonPath("$[5].legs[1].arrivalAirport").value("WRO")).andExpect(jsonPath("$[5].legs[1].arrivalDateTime").value("2119-12-31T16:55:00"))
	      .andExpect(jsonPath("$[6].stops").value("1"))
	      .andExpect(jsonPath("$[6].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[6].legs[0].departureDateTime").value("2119-12-31T07:05:00")).andExpect(jsonPath("$[6].legs[0].arrivalAirport").value("LPL")).andExpect(jsonPath("$[6].legs[0].arrivalDateTime").value("2119-12-31T08:00:00"))
	      .andExpect(jsonPath("$[6].legs[1].departureAirport").value("LPL")).andExpect(jsonPath("$[6].legs[1].departureDateTime").value("2119-12-31T14:30:00")).andExpect(jsonPath("$[6].legs[1].arrivalAirport").value("WRO")).andExpect(jsonPath("$[6].legs[1].arrivalDateTime").value("2119-12-31T17:45:00"))
	      .andExpect(jsonPath("$[7].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[0].departureDateTime").value("2119-12-31T10:25:00")).andExpect(jsonPath("$[7].legs[0].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[0].arrivalDateTime").value("2119-12-31T11:25:00"))
	      .andExpect(jsonPath("$[7].stops").value("1"))
	      .andExpect(jsonPath("$[7].legs[1].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[1].departureDateTime").value("2119-12-31T13:50:00")).andExpect(jsonPath("$[7].legs[1].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[1].arrivalDateTime").value("2119-12-31T14:50:00"))
	      .andExpect(jsonPath("$[7].legs[2].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[2].departureDateTime").value("2119-12-31T15:55:00")).andExpect(jsonPath("$[7].legs[2].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[2].arrivalDateTime").value("2119-12-31T17:00:00"))
	      .andExpect(jsonPath("$[7].legs[3].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[3].departureDateTime").value("2120-01-01T10:25:00")).andExpect(jsonPath("$[7].legs[3].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[3].arrivalDateTime").value("2120-01-01T11:25:00"))
	      .andExpect(jsonPath("$[7].legs[4].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[4].departureDateTime").value("2120-01-01T13:50:00")).andExpect(jsonPath("$[7].legs[4].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[4].arrivalDateTime").value("2120-01-01T14:50:00"))
	      .andExpect(jsonPath("$[7].legs[5].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[5].departureDateTime").value("2120-01-01T16:30:00")).andExpect(jsonPath("$[7].legs[5].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[5].arrivalDateTime").value("2120-01-01T17:35:00"))
	      .andExpect(jsonPath("$[7].legs[6].departureAirport").value("DUB")).andExpect(jsonPath("$[7].legs[6].departureDateTime").value("2120-01-01T20:30:00")).andExpect(jsonPath("$[7].legs[6].arrivalAirport").value("MAN")).andExpect(jsonPath("$[7].legs[6].arrivalDateTime").value("2120-01-01T21:35:00"))
	      .andExpect(jsonPath("$[7].legs[7].departureAirport").value("MAN")).andExpect(jsonPath("$[7].legs[7].departureDateTime").value("2120-01-02T21:00:00")).andExpect(jsonPath("$[7].legs[7].arrivalAirport").value("WRO")).andExpect(jsonPath("$[7].legs[7].arrivalDateTime").value("2120-01-02T00:15:00"))
	      .andExpect(jsonPath("$[8].stops").value("1"))
	      .andExpect(jsonPath("$[8].legs[0].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[0].departureDateTime").value("2119-12-31T08:20:00")).andExpect(jsonPath("$[8].legs[0].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[0].arrivalDateTime").value("2119-12-31T09:40:00"))
	      .andExpect(jsonPath("$[8].legs[1].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[1].departureDateTime").value("2119-12-31T11:55:00")).andExpect(jsonPath("$[8].legs[1].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[1].arrivalDateTime").value("2119-12-31T13:15:00"))
	      .andExpect(jsonPath("$[8].legs[2].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[2].departureDateTime").value("2119-12-31T15:30:00")).andExpect(jsonPath("$[8].legs[2].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[2].arrivalDateTime").value("2119-12-31T16:50:00"))
	      .andExpect(jsonPath("$[8].legs[3].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[3].departureDateTime").value("2119-12-31T16:45:00")).andExpect(jsonPath("$[8].legs[3].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[3].arrivalDateTime").value("2119-12-31T18:10:00"))
	      .andExpect(jsonPath("$[8].legs[4].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[4].departureDateTime").value("2119-12-31T18:50:00")).andExpect(jsonPath("$[8].legs[4].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[4].arrivalDateTime").value("2119-12-31T20:10:00"))
	      .andExpect(jsonPath("$[8].legs[5].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[5].departureDateTime").value("2120-01-01T09:50:00")).andExpect(jsonPath("$[8].legs[5].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[5].arrivalDateTime").value("2120-01-01T11:15:00"))
	      .andExpect(jsonPath("$[8].legs[6].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[6].departureDateTime").value("2120-01-01T11:55:00")).andExpect(jsonPath("$[8].legs[6].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[6].arrivalDateTime").value("2120-01-01T13:15:00"))
	      .andExpect(jsonPath("$[8].legs[7].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[7].departureDateTime").value("2120-01-01T13:50:00")).andExpect(jsonPath("$[8].legs[7].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[7].arrivalDateTime").value("2120-01-01T15:10:00"))
	      .andExpect(jsonPath("$[8].legs[8].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[8].departureDateTime").value("2120-01-01T15:30:00")).andExpect(jsonPath("$[8].legs[8].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[8].arrivalDateTime").value("2120-01-01T16:50:00"))
	      .andExpect(jsonPath("$[8].legs[9].departureAirport").value("DUB")).andExpect(jsonPath("$[8].legs[9].departureDateTime").value("2120-01-01T16:45:00")).andExpect(jsonPath("$[8].legs[9].arrivalAirport").value("STN")).andExpect(jsonPath("$[8].legs[9].arrivalDateTime").value("2120-01-01T18:10:00"))
	      .andExpect(jsonPath("$[8].legs[10].departureAirport").value("STN")).andExpect(jsonPath("$[8].legs[10].departureDateTime").value("2120-01-01T20:30:00")).andExpect(jsonPath("$[8].legs[10].arrivalAirport").value("WRO")).andExpect(jsonPath("$[8].legs[10].arrivalDateTime").value("2120-01-01T23:30:00"));
	}
	
	/**
	 * Validate empty parameters
	 */
	@Test
	public void getEmptyValidationError() throws Exception {
		mvc.perform(get(URL_CONTROLLER)
	      .contentType(MediaType.APPLICATION_JSON))
		  //.andDo(print())
	      .andExpect(status().isBadRequest())
	      .andExpect(jsonPath("$.errors", hasSize(4)))
	      .andExpect(jsonPath("$.errors[*].field").value(Matchers.containsInAnyOrder("departure", "departureDateTime", "arrival", "arrivalDateTime")))
	  	  .andExpect(jsonPath("$.errors[*].error").value(Matchers.everyItem(Matchers.isOneOf("must not be empty"))));
	}
	
	/**
	 * Validate date range parameters
	 */
	@Test
	public void getDateRangeValidationError() throws Exception {
		mvc.perform(get(URL_CONTROLLER)
				.param(PARAM_DEPARTURE, "DUB")
				.param(PARAM_ARRIVAL, "WRO")
				.param(PARAM_DEPARTURE_DATE_TIME, "2020-12-31T07:00")
				.param(PARAM_ARRIVAL_DATE_TIME, "2020-01-25T07:00")
	      .contentType(MediaType.APPLICATION_JSON))
		  //.andDo(print())
	      .andExpect(status().isBadRequest())
	      .andExpect(jsonPath("$.errors", hasSize(1)))
	      .andExpect(jsonPath("$.errors[0].error").value("date range should not exceed 7 days"))
	      .andExpect(jsonPath("$.errors[0].field").value("departureDateTime"));
	}
	
	/**
	 * Validate dates format and parameters size
	 */
	@Test
	public void getDateFormatValidationError() throws Exception {
		mvc.perform(get(URL_CONTROLLER)
				.param(PARAM_DEPARTURE, "FFFF")
				.param(PARAM_ARRIVAL, "FFFF")
				.param(PARAM_DEPARTURE_DATE_TIME, "2020-12-31FFFFFF")
				.param(PARAM_ARRIVAL_DATE_TIME, "2050-01-25FFFFFF")
		  .contentType(MediaType.APPLICATION_JSON))
          //.andDo(print())
	      .andExpect(status().isBadRequest())
	      .andExpect(jsonPath("$.errors", hasSize(4)))
	      .andExpect(jsonPath("$.errors[*].field").value(Matchers.containsInAnyOrder("departureDateTime", "arrivalDateTime", "departure", "arrival")))
	  	  .andExpect(jsonPath("$.errors[*].error").value(Matchers.everyItem(Matchers.isOneOf("invalid date format","size must be 3"))));
	 }
}
