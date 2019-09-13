package com.ryanair.searchflight.unit;


import java.io.File;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.ryanair.searchflight.config.PropertiesConstants;
import com.ryanair.searchflight.dto.Route;
import com.ryanair.searchflight.restclient.RouteApiController;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
public class RouteApiControllerTest {
	
	private static final String  URL_ROUTES = "https://FFFF/routes";
	
	@Mock
	private PropertiesConstants propertiesConstants;
	
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private RouteApiController routesApiController;
	
	/**
	 * Check that if the cache is down it is restarted, in this case using the json files
	 * It can be tested with no files (real URL) to notice if there are new records
	 * @throws Exception
	 */
	@Test
	public void testRoutesFromFiles() throws Exception{
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		CollectionType typeReference = TypeFactory.defaultInstance().constructCollectionType(List.class, Route.class);
		List<Route> routes = mapper.readValue(new File("src\\test\\java\\data\\ROUTES.json"), typeReference);
		ResponseEntity<List<Route>> response = new ResponseEntity<>(routes, HttpStatus.OK);
		
		Mockito.when(propertiesConstants.getRoutes()).thenReturn(URL_ROUTES);
		Mockito.when(restTemplate.exchange(eq(URL_ROUTES), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class))).thenReturn(response);

		List<Route> listRoutes = routesApiController.getRoutes();
		assertThat(listRoutes, hasSize(4795));
	}

}
