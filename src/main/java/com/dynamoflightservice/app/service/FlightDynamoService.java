package com.dynamoflightservice.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.dynamoflightservice.app.model.Flight;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

@Service
public class FlightDynamoService {

	
	@Autowired
	private DynamoDbEnhancedClient enhancedClient;
	
	 @Autowired
	 private DynamoDbTable<Flight> flightTable;
	 
	 
	 public List<Flight> getallFlights()
	 {
		 return flightTable.scan().items().stream().collect(Collectors.toList());
	 }
	 
	 public boolean  addFlight(Flight flight)
	 {
		 flightTable.putItem(flight);
		 return true;
	 }
	 
	 public boolean updateFlightdata(Flight flight, String flightName)
	 {
		 Flight flight1 = flightTable.scan().items().stream().filter(a -> a.getFlightName().equals(flightName)).findFirst().get();
		 flight1.setFlightseatCapacity(flight.getFlightseatCapacity());
		 flight1.setAirlineName(flight.getAirlineName());
		 flightTable.putItem(flight1);
		 return true;
	 }
	 
	 public boolean deleteFlight(String flightName)
	 {
		 Flight flight= flightTable.scan().items().stream().filter(flight1 -> flight1.getFlightName().equals(flightName)).findFirst().get();
		 flightTable.deleteItem(flight);
		 return true;
		 
	 
	 }
	 
	 public Integer getSeats(String flightName)
	 {
		 return flightTable.scan().items()
				 .stream()
				 .filter(a -> a.getFlightName().equals(flightName))
				 .findFirst().get().getFlightseatCapacity();
	 }
	 
	 public Integer adjustSeatnumbers(String flightName, int numberoftickets)
	 {
		
		 Flight f = flightTable.scan().items()
				 		.stream()
				 		.filter(a -> a.getFlightName().equals(flightName))
				 		.findFirst().get();
		 if(f.getFlightseatCapacity()>= numberoftickets)
		 {
			 f.setFlightseatCapacity(f.getFlightseatCapacity() - numberoftickets);
			 flightTable.putItem(f);
			 return f.getFlightseatCapacity();
		 }
		 else {
			 throw new RuntimeException("Error in adjusting the seats");
		 }
		
	 }
	 public Integer adjustAfterTicketCancellation(String flightName, int numberoftickets)
	 {
		 Flight f = flightTable.scan().items()
			 		.stream()
			 		.filter(a -> a.getFlightName().equals(flightName))
			 		.findFirst().get();
		 f.setFlightseatCapacity(f.getFlightseatCapacity()+numberoftickets);
		 flightTable.putItem(f);
		 return f.getFlightseatCapacity();
	 }
}
