package com.dynamoflightservice.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.dynamoflightservice.app.model.Flight;
import com.dynamoflightservice.app.service.FlightDynamoService;

@RestController
@RequestMapping("/flight")
public class DynamoFlightController {

	@Autowired
	private FlightDynamoService flightservice;
	
	@GetMapping("/getall")
	public List<Flight> getAllFlights(@RequestHeader Map<String, String> headers)
	{
		DynamoFlightController.validateToken(headers);
		if(validated)
		return flightservice.getallFlights();
		else
			throw new RuntimeException("Token is not valid");
	}
	
	@PostMapping("/add")
	public String addFlights(@RequestHeader Map<String, String> headers,@RequestBody Flight flight)
	{
		DynamoFlightController.validateToken(headers);
		if(validated) {
		if(flightservice.addFlight(flight))
			return "Flight added successfully";
		else
			return "Error in adding the flight";
		}
		else {
			throw new RuntimeException("Token is not valid");
		}
	}
	
	
	@PutMapping("/update")
	 public String updateFlight(@RequestHeader Map<String, String> headers,@RequestBody Flight flight, @RequestParam String flightName)
	 {
		DynamoFlightController.validateToken(headers);
		if(validated) {
		if(flightservice.updateFlightdata(flight, flightName))
			return "Flight Updated successfully";
		else
			return "Error in updating the flight";
		}else {
			 throw new RuntimeException("Token is not valid");
		}
	 }
	
	@DeleteMapping("/remove/{flightName}")
	public String removeFlight(@RequestHeader Map<String, String> headers,@PathVariable String flightName)
	{
		DynamoFlightController.validateToken(headers);
		if(validated) {
		if(flightservice.deleteFlight(flightName))
			return "Flight removed successfully.";
		else 
			return "Error in removing the flight";			
		}else
			throw new RuntimeException("Token is not valid");
	}
	
	@GetMapping("/seatsavailable/{flightName}")
	public Integer getSeatstatus(@PathVariable String flightName)
	{
		return flightservice.getSeats(flightName);
	}
	
	@GetMapping("/adjustseat/{flightName}/{numberoftickets}")
	public Integer adjustSeats(@PathVariable String flightName, @PathVariable Integer numberoftickets)
	{
		return flightservice.adjustSeatnumbers(flightName, numberoftickets);
	}
	
	
	@GetMapping("/adjustseatsaftercancellation/{flightName}/{numberoftickets}")
	public Integer adjustSeatsAfterBookingCancellation(@PathVariable String flightName, @PathVariable Integer numberoftickets)
	{
		return flightservice.adjustAfterTicketCancellation(flightName, numberoftickets);
	}
	
private static boolean validated=false;
	
	
	public  static void validateToken (Map<String, String> header)
	{
	
		String token="";
		for(String key : header.keySet())
		{
			if(key.equals("authorization"))
				token=header.get(key);
		}
		HttpHeaders httpheader = new HttpHeaders();
		httpheader.set("Authorization", token);
		HttpEntity<Void> requestentity = new HttpEntity<>(httpheader);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Boolean> response = restTemplate.exchange("http://34.216.12.139:9004/validatejwt",HttpMethod.GET, requestentity,boolean.class);
		validated=response.getBody().booleanValue();
	}
}
