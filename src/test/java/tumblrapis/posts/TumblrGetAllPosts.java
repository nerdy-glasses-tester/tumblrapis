package tumblrapis.posts;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import tumblrapis.constants.Auth;
import tumblrapis.constants.EndPoints;
import tumblrapis.constants.Path;

public class TumblrGetAllPosts {

   static String id = "";
   static String API_KEY = Auth.API_KEY;
   static String firstposttext = "Edited, Hello this is my first post from postman!";
	
	@BeforeClass
	public void setup() {
		RestAssured.baseURI = Path.BASE_URI;
		RestAssured.basePath = Path.POSTS;
	}
	
	@Test
	public void GetAllTumblrPosts() {

		Response response =
		given()
			.auth()
			.oauth(Auth.CONSUMER_KEY, Auth.CONSUMER_SECRET, Auth.ACCESS_TOKEN, Auth.ACCESS_SECRET)
			.pathParam("API_KEY",API_KEY)
		.when()
			.post(EndPoints.POSTS_GETALL)
		.then()
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.extract()
			.response();
		
		int inttotalposts = response.path("response.blog.total_posts");
		String totalposts = Integer.toString(inttotalposts);
		System.out.println("There are "+totalposts+" posts.");
		
		inttotalposts = inttotalposts-1;
		Long longid = response.path("response.posts["+inttotalposts+"].id");
		id = Long.toString(longid);
		System.out.println("The first post id is " + id);

		String summary = response.path("response.posts["+inttotalposts+"].summary");
		System.out.println("The first post contains the following text: "+summary);
		
		Assert.assertEquals(summary, firstposttext);
		
		String responseString = response.asString();
		System.out.println(responseString);
		
	}
	
		
	
}
