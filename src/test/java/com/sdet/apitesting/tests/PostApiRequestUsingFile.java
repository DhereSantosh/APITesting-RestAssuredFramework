package com.sdet.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.sdet.apitesting.utils.BaseTest;
import com.sdet.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public class PostApiRequestUsingFile extends BaseTest {
	
	@Test
	public void postApiRequest() {
		
		try {
			String postApiRequestBody = FileUtils.readFileToString(new File(FileNameConstants.POST_API_REQUEST_BODY),"UTF-8");
			Response response =
			RestAssured
					.given()
						.contentType(ContentType.JSON)
						.body(postApiRequestBody)
						.baseUri("https://restful-booker.herokuapp.com/booking")
					.when()
						.post()
					.then()
						.assertThat()
						.statusCode(200)
					.extract()
						.response();
			
			JSONArray jsonarrayFirstname = JsonPath.read(response.body().asString(),"$.booking..firstname");
			String actualFirstName = (String) jsonarrayFirstname.get(0); 
		Assert.assertEquals(actualFirstName, "test1");
		
			JSONArray jsonarrayLastname = JsonPath.read(response.body().asString(),"$.booking..lastname");
			String actualLastName = (String) jsonarrayLastname.get(0); 
		Assert.assertEquals(actualLastName, "test1");
		
			JSONArray jsonarrayCheckin = JsonPath.read(response.body().asString(),"$.booking.bookingdates..checkin");
			String actualCheckin = (String) jsonarrayCheckin.get(0); 
		Assert.assertEquals(actualCheckin, "2024-03-03");
		
		int bookingId = JsonPath.read(response.body().asString(),"$.bookingid");
		
		RestAssured 
				.given()
					.contentType(ContentType.JSON)
					.baseUri("https://restful-booker.herokuapp.com/booking")
				.when()
					.get("/{bookingId}",bookingId)
				.then()
					.assertThat()
					.statusCode(200);
		
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
