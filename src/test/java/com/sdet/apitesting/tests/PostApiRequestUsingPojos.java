package com.sdet.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdet.apitesting.pojos.Booking;
import com.sdet.apitesting.pojos.BookingDates;
import com.sdet.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

public class PostApiRequestUsingPojos {
	
	@Test
	public void postApiRequest() {
		
		try {
			
			String jsonSchema = FileUtils.readFileToString(new File(FileNameConstants.JSON_SCHEMA),"UTF-8");
			
			BookingDates bookingdate = new BookingDates("2024-03-25","2024-03-30");
			Booking booking = new Booking("Test1","Test2","BREAKFAST",1000,true,bookingdate);
			
			//serialization
			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);
			System.out.println("Serialization data: " + requestBody);
			
			//de-serialization
			Booking bookingDetails = objectMapper.readValue(requestBody, Booking.class);
			System.out.println(bookingDetails.getFirstname());
			System.out.println(bookingDetails.getLastname());
			
			System.out.println(bookingDetails.getBookingdates().getCheckin());
			System.out.println(bookingDetails.getBookingdates().getCheckout());
			
			Response response =
			RestAssured
					.given()
						.contentType(ContentType.JSON)
						.body(requestBody)
						.baseUri("https://restful-booker.herokuapp.com/booking")
					.when()
						.post()
					.then()
						.assertThat()
						.statusCode(200)
					.extract()
						.response();
			
			int bookingId = response.path("bookingid");
			
			//System.out.println(jsonSchema);
			
			RestAssured
					.given()
						.contentType(ContentType.JSON)
						.baseUri("https://restful-booker.herokuapp.com/booking")
					.when()
						.get("/{bookingId}",bookingId)
					.then()
						.assertThat()
						.statusCode(200)
						.body(JsonSchemaValidator.matchesJsonSchema(jsonSchema));
			
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
