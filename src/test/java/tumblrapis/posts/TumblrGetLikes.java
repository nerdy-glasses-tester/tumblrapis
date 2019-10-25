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

public class TumblrGetLikes {
	
    static String API_KEY = Auth.API_KEY;
	
	@BeforeClass
	public void setup() {
		RestAssured.baseURI = Path.BASE_URI;
		RestAssured.basePath = Path.LIKES;
	}
	
	@Test
	public void GetAllTumblrPosts() {

		Response response =
		given()
			.auth()
			.oauth(Auth.CONSUMER_KEY, Auth.CONSUMER_SECRET, Auth.ACCESS_TOKEN, Auth.ACCESS_SECRET)
			.pathParam("API_KEY", API_KEY)
		.when()
			.post(EndPoints.LIKES_GET)
		.then()
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.extract()
			.response();
		
		int inttotallikes = response.path("response.liked_count");
		String totallikes = Integer.toString(inttotallikes);
		System.out.println("There are "+totallikes+" total likes.");
		
		String blognamelike1 = response.path("response.liked_posts.blog.name[0]");
		Assert.assertEquals(blognamelike1, "moreapiautomationlearning");
		
		String blognamelike2 = response.path("response.liked_posts.blog.name[1]");
		Assert.assertEquals(blognamelike2, "moreapiautomationlearning");
		
		String postsummarylike1 = response.path("response.liked_posts.summary[0]");
		Assert.assertEquals(postsummarylike1, "Second Post");
		
		String postsummarylike2 = response.path("response.liked_posts.summary[1]");
		Assert.assertEquals(postsummarylike2, "First Post");
		
		String postbodylike1 = response.path("response.liked_posts.body[0]");
		boolean value1 = postbodylike1.contains("Hey this is my second post");
		Assert.assertEquals(value1, true);
		
		String postbodylike2 = response.path("response.liked_posts.body[1]");
		boolean value2 = postbodylike2.contains("Hey this is my first post");
		Assert.assertEquals(value2, true);
		
		String responseString = response.asString();
		System.out.println(responseString);
		
	}
	
		
	
}
