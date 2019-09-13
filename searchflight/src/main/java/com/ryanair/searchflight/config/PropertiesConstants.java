package com.ryanair.searchflight.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author sergiopf
 */
@ConfigurationProperties(prefix = "client.url")
@Configuration
public class PropertiesConstants {

	    private String routes;
	    private String schedules;
	    
		public String getRoutes() {
			return routes;
		}
		public void setRoutes(String routes) {
			this.routes = routes;
		}
		public String getSchedules() {
			return schedules;
		}
		public void setSchedules(String schedules) {
			this.schedules = schedules;
		}
	
}
