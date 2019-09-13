package com.ryanair.searchflight.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Schedule implements Serializable,Cloneable{

	private static final long serialVersionUID = -687991492884005033L;
	
	private List<Year> years = new ArrayList<>();
	public List<Year> getYears() {
		return years;
	}
	public void setYears(List<Year> years) {
		this.years = years;
	}

	public static class Year implements Serializable{
		
		private static final long serialVersionUID = -687123492884005033L;
		
		private int year;
		public int getYear() {
			return this.year;
		}
		public void setYear(int year) {
			this.year = year;
		}
		
		private List<Month> months = new ArrayList<>();
		public List<Month> getMonths() {
			return months;
		}
		public void setMonths(List<Month> months) {
			this.months = months;
		}
	
		public static class Month implements Serializable{
			
			private static final long serialVersionUID = -687123456764001243L;
			
			private int month;
			public int getMonth() {
				return this.month;
			}
			public void setMonth(int month) {
				this.month = month;
			}
			
			private List<Days> days = new ArrayList<>();
			public List<Days> getDays() {
				return this.days;
			}
			public void setDays(List<Days> days) {
				this.days = days;
			}
		
			public static class Days implements Serializable{
				
				private static final long serialVersionUID = -344323456764054453L;
				
				private int day;
				public int getDay() {
					return this.day;
				}
				public void setDay(int day) {
					this.day = day;
				}
		
				private List<Flight> flights;
				public List<Flight> getFlights() {
					return this.flights;
				}
				public void setFlights(List<Flight> flights) {
					this.flights = flights;
				}
		
				public static class Flight implements Serializable{
					
					private static final long serialVersionUID = -344323877864054453L;
					
					private String departureTime;
					private String arrivalTime;
		
					public String getDepartureTime() {
						return this.departureTime;
					}
					public void setDepartureTime(String departureTime) {
						this.departureTime = departureTime;
					}
					public String getArrivalTime() {
						return this.arrivalTime;
					}
					public void setArrivalTime(String arrivalTime) {
						this.arrivalTime = arrivalTime;
					}
				}
			}
		}
	}

}
