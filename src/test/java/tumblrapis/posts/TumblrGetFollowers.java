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

public class TumblrGetFollowers {
	
	@BeforeClass
	public void setup() {
		RestAssured.baseURI = Path.BASE_URI;
		RestAssured.basePath = Path.FOLLOWERS;
	}
	
	@Test
	public void GetAllTumblrPosts() {

		Response response =
		given()
			.auth()
			.oauth(Auth.CONSUMER_KEY, Auth.CONSUMER_SECRET, Auth.ACCESS_TOKEN, Auth.ACCESS_SECRET)
		.when()
			.post(EndPoints.FOLLOWERS_GET)
		.then()
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.extract()
			.response();
		
		int inttotalusers = response.path("response.total_users");
		String totalusers = Integer.toString(inttotalusers);
		System.out.println("There are "+totalusers+" followers.");
		
		String follower1 = response.path("response.users.name[0]");
		Assert.assertEquals(follower1, "moreapiautomationlearning");
		
		String url1 = response.path("response.users.url[0]");
		Assert.assertEquals(url1, "https://moreapiautomationlearning.tumblr.com/");
		
		String follower2 = response.path("response.users.name[1]");
		Assert.assertEquals(follower2,"cheezbot");
		
		String url2 = response.path("response.users.url[1]");
		Assert.assertEquals(url2, "https://cheezbot.tumblr.com/");
		
		System.out.println("Verified followers: "+follower1+" and "+follower2+".");
		
		String responseString = response.asString();
		System.out.println(responseString);
		
	}
	
		
	
}
