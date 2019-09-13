# Ryanair - Task 2 - Java/Spring - Interconnecting Flights

### Task

Write a Spring MVC based RESTful API application which serves information about possible direct and interconnected flights (maximum 1 stop) based on the data consumed from external APIs.

The application can consume data from the following microservices:

- *<a href="https://api.ryanair.com/core/3/routes" title="Routes API">Routes API</a>*: 
which returns a list of all available routes based on the airport's IATA codes. Please note that only routes with empty connectingAirport should be used (value set to null).

- *<a href="https://api.ryanair.com/timetable/3/schedules/{departure}/{arrival}/years/{year}/months/{month}" title="Schedules API">Schedules API</a>*: 
which returns a list of available flights for a given departure airport IATA code, an arrival airport IATA code, a year and a month.

To build and run the project. Run this command:

```
mvn clean package spring-boot:run
```

To use the API, open a web browser and insert an url (try with future dates :)):

```
http://localhost:8080/searchflight/3/steps/interconnections?departure=DUB&arrival=WRO&departureDateTime=2019-09-01T07:00&arrivalDateTime=2019-09-03T21:00
```

Or open a terminal and run a *curl* command:

```
curl -i -X GET "http://localhost:8080/searchflight/3/steps/interconnections?departure=DUB&arrival=WRO&departureDateTime=2019-09-01T07:00&arrivalDateTime=2019-09-03T21:00" -H  "accept: application/json" && echo ''
```

Request controller is configured at SearchFlightController.class

### Tools

Java 10.0.2 (works with 8)

Spring boot 2.1.6

Httpclient

### Features

Scheduled cache for routes API, to update by configured time

RestTemplates to handle connection and errors/exceptions

Custom exception, error messages and constraint validations

Multithreading, when recovering data from schedules API, ScheduleFilterServiceImpl.getSchedules (maybe more threadable)

Can search between months and years (departureDateTime=2019-12-31T07:00&arrivalDateTime=2020-01-02T21:00)

Inner classes in DTOs

Tests using json files and against real environment

### Classes structure

```
com.ryanair.searchfligth

  - Application.java -> Main class to run the program
  
com.ryanair.searchfligth.config

  - CacheSchedulConfig.java -> Config to update routes cache every configured time (at application.yml)

  - MessageSourceConfig.java -> Config for custom messages

  - RestTemplateClientConfig.java -> Connfig for rest client requests

  - PropertiesConstants.java -> Get values from properties files

com.ryanair.searchfligth.constraint

  - DateRangeValidator.java -> Constraint validator, created with the purpose of validating the date format (else is a generic and strange error)

com.ryanair.searchfligth.controller

  - SearchFlightController -> Get mapping for /interconnections

com.ryanair.searchfligth.dto

  - Request.java -> Input request DTO object

  - Response.java -> Response DTO for request 

  - Route.java -> DTO to map call to routes API

  - Schedule.java -> DTO to map calls to schedules API

com.ryanair.searchfligth.exception

  - ResponseExceptionHandler.java -> Error handler, response homogeneity for validation errors or RunTimeException
  
  - RestTemplateErrorHandler.java -> Throw different errors at client connection depending the response from routes and schedules API
  
  - RestTemplateNotFoundException.java -> Thrown when an object is not founded on restTemplate call
 
com.ryanair.searchfligth.model

  - AvailableRoutes.java -> Internal object to transfer information between services

com.ryanair.searchfligth.restclient

  - RouteApiController.java -> Use of RestTemplate to call route API

  - ScheduleApiController.java -> Use of RestTemplate to call schedule API

com.ryanair.searchfligth.service.impl

  - RouteServiceImpl.java -> Request for route API and call filters

  - ScheduleServiceImpl.java -> Requests for schedules API and call filters
  
com.ryanair.searchfligth.filter

  - RouteFilter.java -> Filters for routes

  - ScheduleFilter.java -> Filters for schedules

com.ryanair.searchfligth.service.integration

  - SearchFileControllerTest.java -> Integration tests using simulated data from files

  - SearchRealControllerTest.java -> Integration test using real data from rest APIs

com.ryanair.searchfligth.service.unit
 
  - RouteApiControllerTest.java -> Unit test for route API

  - ScheduleApiControllerTest.java -> Unit test for schedule API

data

  - ROUTES.json -> File to simulate the rest APIs response from routes

  - (FROM)_(TO)_(YEAR)_(MONTH).json -> Files to simulate the rest APIs response from schedules

  - request.txt -> File only informative. Relations between files and APIs

  - test_result.json -> File only informative. Real calls to APIS and real json response
```

### Observations
In response from schedules API (departureTime":"06:50","arrivalTime":"10:25"):

https://services-api.ryanair.com/timtbl/3/schedules/DUB/WRO/years/2019/months/9

{"month":9,"days":[{"day":1,"flights":[{"carrierCode":"FR","number":"1926","departureTime":"06:50","arrivalTime":"10:25"}]},...]}

Impossible to control if the arrival is the next day of the departure because it uses only same day hours???
