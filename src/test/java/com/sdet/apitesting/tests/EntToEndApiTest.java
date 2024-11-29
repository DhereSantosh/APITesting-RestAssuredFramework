package com.sdet.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.sdet.apitesting.utils.BaseTest;
import com.sdet.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public class EntToEndApiTest extends BaseTest {
	
	@Test
	public void e2eApiRequest() {
		
		try {
			String postApiRequestBody = FileUtils.readFileToString(new File(FileNameConstants.POST_API_REQUEST_BODY),"UTF-8");
			
			String tokenApiRequestBody = FileUtils.readFileToString(new File(FileNameConstants.TOKEN_API_REQUEST_BODY),"UTF-8");
			
			String putApiRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PUT_API_REQUEST_BODY),"UTF-8");
			
			String patchApiRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PATCH_API_REQUEST_BODY),"UTF-8");
			
			//post api call
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
		
		
		int bookingId = JsonPath.read(response.body().asString(),"$.bookingid");
		
		// get api call
		
		RestAssured 
				.given()
					.contentType(ContentType.JSON)
					.baseUri("https://restful-booker.herokuapp.com/booking")
				.when()
					.get("/{bookingId}",bookingId)
				.then()
					.assertThat()
					.statusCode(200);
		
		//Token Generation
		Response tokenApiResponse = 
		RestAssured
				.given()
					.contentType(ContentType.JSON)
					.body(tokenApiRequestBody)
					.baseUri("https://restful-booker.herokuapp.com/auth")
				.when()
					.post()
				.then()
					.assertThat()
					.statusCode(200)
				.extract()
					.response();
				
		String token = JsonPath.read(tokenApiResponse.body().asString(),"$.token");
		
		//put api call
		RestAssured 
		.given()
			.contentType(ContentType.JSON)
			.body(putApiRequestBody)
			.header("Cookie","token="+token)
			.baseUri("https://restful-booker.herokuapp.com/booking/")
		.when()
			.put("{bookingId}",bookingId)
		.then()
			.assertThat()
			.statusCode(200)
			.body("firstname",Matchers.equalTo("test1"))
			.body("lastname",Matchers.equalTo("test1"));
		
		//patch api call
		RestAssured 
		.given()
			.contentType(ContentType.JSON)
			.body(patchApiRequestBody)
			.header("Cookie","token="+token)
			.baseUri("https://restful-booker.herokuapp.com/booking/")
		.when()
			.patch("{bookingId}",bookingId)
		.then()
			.assertThat()
			.statusCode(200)
			.body("firstname",Matchers.equalTo("Test2791"));
		
		//delete api call
		RestAssured 
		.given()
			.contentType(ContentType.JSON)
			.header("Cookie","token="+token)
			.baseUri("https://restful-booker.herokuapp.com/booking/")
		.when()
			.delete("{bookingId}",bookingId)
		.then()
			.assertThat()
			.statusCode(201);
		
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
