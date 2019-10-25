package tumblrapis.posts;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Content;
import models.PostTextModel;
import tumblrapis.constants.Auth;
import tumblrapis.constants.EndPoints;
import tumblrapis.constants.Path;


public class TumblrPostandDelete {

   static String id = "";
   static String originaltext = "Hello this post will be deleted via RestAssured api automation testing!";
	
	@BeforeClass
	public void setup() {
		RestAssured.baseURI = Path.BASE_URI;
	}
	
	@Test
	public void TumblrPost() {
		/***
		{
		    "content": [
		        {
		            "type": "text",
		            "text": "Hello this post will be deleted via RestAssured api automation testing!"
		        }
		    ]
		}
		***/
		RestAssured.basePath = Path.POSTS;
	
        Content content = new Content();
        content.setAdditionalProperty("type", "text");
		content.setAdditionalProperty("text", originaltext);
        
		List<Content> ct = new ArrayList<Content>();
		ct.add(content);
		
		PostTextModel  contenttext = new PostTextModel();
		contenttext.setContent(ct);
		
		Response response =
		given()
			.auth()
			.oauth(Auth.CONSUMER_KEY, Auth.CONSUMER_SECRET, Auth.ACCESS_TOKEN, Auth.ACCESS_SECRET)
			.contentType(ContentType.JSON)
			.body(contenttext)
		.when()
			.post()
		.then()
			.statusCode(201).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("Created"))
			.extract()
			.response();
		
		id = response.path("response.id");
		System.out.println("The post id is" + id);

		String responseString = response.asString();
		System.out.println(responseString);
		
	}
	
	@Test(dependsOnMethods={"TumblrPost"})
	public void verifyTumblrPost() {
		
		RestAssured.basePath = Path.POSTS;
		
		Response response =
		given()
			.auth()
			.oauth(Auth.CONSUMER_KEY, Auth.CONSUMER_SECRET, Auth.ACCESS_TOKEN, Auth.ACCESS_SECRET)
			.pathParam("id", id)
		.when()
			.get(EndPoints.POSTS_SPECIFIC)
		.then()
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))	
			.extract()
			.response();
		
		Long verifyid = response.path("response.id");
		String newverifyid = Long.toString(verifyid);
		Assert.assertEquals(newverifyid, id);
		
		String posttext = response.path("response.summary");
		System.out.println("The post's text is: " + posttext);
		Assert.assertEquals(posttext, originaltext);
        
		String responseString = response.asString();
		System.out.println(responseString);

	}
	
	
	@Test(dependsOnMethods={"verifyTumblrPost"})
	public void DeleteTumblrPost() {
		
		RestAssured.basePath = Path.POST;
		
		Response response =
		given()
			.auth()
			.oauth(Auth.CONSUMER_KEY, Auth.CONSUMER_SECRET, Auth.ACCESS_TOKEN, Auth.ACCESS_SECRET)
			.pathParam("id", id)
		.when()
			.delete(EndPoints.POST_DELETE)
		.then()
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.extract()
			.response();
	
			String responseString = response.asString();
			System.out.println(responseString);
	}


	@Test(dependsOnMethods={"DeleteTumblrPost"})
	public void verfiyDeleteTumblrPost() {
		
		RestAssured.basePath = Path.POSTS;
		
		Response response =
		given()
			.auth()
			.oauth(Auth.CONSUMER_KEY, Auth.CONSUMER_SECRET, Auth.ACCESS_TOKEN, Auth.ACCESS_SECRET)
			.pathParam("id", id)
		.when()
			.get(EndPoints.POSTS_SPECIFIC)
		.then()
			.statusCode(404).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("Not Found"))	
			.extract()
			.response();
		
		String responseString = response.asString();
		System.out.println(responseString);

	}
	
	
}
