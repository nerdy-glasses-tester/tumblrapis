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

public class TumblrGetFollowing {
	
	@BeforeClass
	public void setup() {
		RestAssured.baseURI = Path.BASE_URI;
		RestAssured.basePath = Path.FOLLOWING;
	}
	
	@Test
	public void GetAllTumblrPosts() {

		Response response =
		given()
			.auth()
			.oauth(Auth.CONSUMER_KEY, Auth.CONSUMER_SECRET, Auth.ACCESS_TOKEN, Auth.ACCESS_SECRET)
		.when()
			.post(EndPoints.FOLLOWING_GET)
		.then()
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.extract()
			.response();
		
		int inttotalblogs = response.path("response.total_blogs");
		String totalblogs = Integer.toString(inttotalblogs);
		System.out.println("There are "+totalblogs+" blogs you are following.");
		
		String following1 = response.path("response.blogs.name[0]");
		Assert.assertEquals(following1, "moreapiautomationlearning");
		
		String url1 = response.path("response.blogs.url[0]");
		Assert.assertEquals(url1, "https://moreapiautomationlearning.tumblr.com/");
		
		String following2 = response.path("response.blogs.name[1]");
		Assert.assertEquals(following2,"staff");
		
		String url2 = response.path("response.blogs.url[1]");
		Assert.assertEquals(url2, "https://staff.tumblr.com/");
		
		
		System.out.println("Verified you are following: "+following1+" and "+following2+".");
		
		String responseString = response.asString();
		System.out.println(responseString);
		
	}
	
		
	
}
