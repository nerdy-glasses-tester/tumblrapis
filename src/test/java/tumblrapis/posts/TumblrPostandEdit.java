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

public class TumblrPostandEdit {

   static String id = "";
   static String originaltext = "Hello this is my second post from RestAssured api automation testing!";
   static String editedtext = "Edited, Hello this is my second post from RestAssured api automation testing!";
	
	@BeforeClass
	public void setup() {
		RestAssured.baseURI = Path.BASE_URI;
		RestAssured.basePath = Path.POSTS;
	}
	
	@Test
	public void TumblrPost() {
		/***
		{
		    "content": [
		        {
		            "type": "text",
		            "text": "Hello this is my second post from RestAssured api automation testing!"
		        }
		    ]
		}
		***/
		
	
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
	public void EditTumblrPost() {
		
		Content content = new Content();
        content.setAdditionalProperty("type", "text");
		content.setAdditionalProperty("text", editedtext);
        
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
			.pathParam("id", id)
		.when()
			.put(EndPoints.POSTS_EDIT)
		.then()
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.extract()
			.response();
		
		String responseString = response.asString();
		System.out.println(responseString);
		
	}


	@Test(dependsOnMethods={"EditTumblrPost"})
	public void verfiyEditTumblrPost() {
		
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
		
		String edittext = response.path("response.summary");
		System.out.println("After editing, the post's text is: " + edittext);
		Assert.assertEquals(edittext, editedtext);
		
		String responseString = response.asString();
		System.out.println(responseString);

	}
	
	
}
