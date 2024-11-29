package com.sdet.apitesting.tests;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import com.sdet.apitesting.utils.BaseTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;

public class PostApiRequest extends BaseTest {
	
	@Test
	public void createBooking() {
		
		
		//Prepare request body
		//1) Using a JSON Object
		
		JSONObject booking = new JSONObject();
		JSONObject bookingDates = new JSONObject();
		
		booking.put("firstname", "test1");
		booking.put("lastname", "test1");
		booking.put("totalprice", 1000);
		booking.put("depositpaid", true);
		booking.put("additionalneeds", "breakfast");
		booking.put("bookingdates", bookingDates);
		
		bookingDates.put("checkin", "2024-03-25");
		bookingDates.put("checkout", "2024-03-30");
		
		Response response =
		RestAssured
				.given()
					.contentType(ContentType.JSON)
					.body(booking.toString())
					.baseUri("https://restful-booker.herokuapp.com/booking")
					//.log().all()
				.when()
					.post()
				.then()
					.assertThat()
					//.log().all()
					//.log().ifValidationFails()
					.statusCode(200)
					.body("booking.firstname", Matchers.equalTo("test1"))
					.body("booking.bookingdates.checkin", Matchers.equalTo("2024-03-25"))
				.extract()
					.response();
		
		int bookingId = response.path("bookingid");
		
		RestAssured
				.given()
					.contentType(ContentType.JSON)
					.pathParam("bookingID", bookingId)
					.baseUri("https://restful-booker.herokuapp.com/booking")
				.when()
					.get("{bookingID}")
				.then()
					.assertThat()
					.statusCode(200)
					.body("firstname", Matchers.equalTo("test1"))
					.body("lastname", Matchers.equalTo("test1"));
					
						
					
		
		
		
		
	}

}
