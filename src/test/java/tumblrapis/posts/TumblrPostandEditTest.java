package tumblrapis.posts;

import org.testng.annotations.Test;
import com.cedarsoftware.util.io.JsonWriter;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.Content;
import models.PostTextModel;
import resources.ExtentTestManager;
import tumblrapis.common.RestUtilities;
import tumblrapis.constants.EndPoints;
import tumblrapis.constants.Path;


public class TumblrPostandEditTest {

   static String id = "";
   static String originaltext = "Hello this is my second post from RestAssured api automation testing!";
   static String editedtext = "Edited, Hello this is my second post from RestAssured api automation testing!";
   private static Logger log = LogManager.getLogger(TumblrPostandEditTest.class);
   
   static StringWriter requestWriter;
   static PrintStream requestCapture;
   static ExtentTest test;
   static Response response;

   RequestSpecification reqSpec;
   ResponseSpecification resSpec;
   
   
	@BeforeClass
	public void setup() throws FileNotFoundException {
		reqSpec = RestUtilities.getRequestSpecification();
		reqSpec.basePath(Path.POSTS);
		//reqSpec.pathParam("id", id);
		reqSpec.log().all();
		resSpec = RestUtilities.getResponseSpecification();
		
		//Prints out request header and response body to separate file for just this test
		PrintStream fileOutPutStream = new PrintStream(new File("log4jlogs/TumblrPostandEditTest.txt"));
		RestAssured.config = RestAssured.config().logConfig(new LogConfig().defaultStream(fileOutPutStream));
		//Prints to one log4jlogs with all tests
		//PrintStream outputStream = new PrintStream(IoBuilder.forLogger(log).buildOutputStream());
		//RestAssured.config = RestAssured.config().logConfig(new LogConfig().defaultStream(outputStream));

	}
	
	@SuppressWarnings("deprecation")
	@BeforeMethod
	public void methodsetup() {
		
      requestWriter = new StringWriter();
      requestCapture = new PrintStream(new WriterOutputStream(requestWriter));

	}
	
	@Test
	public void TumblrPostandEditTest_PostTest() {
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

		String methodName = new Object() {}
	      .getClass()
	      .getEnclosingMethod()
	      .getName();
		
		test = ExtentTestManager.startTest(methodName, "Starting test");	
		
		log.info("Running TumblrPostTest");
        Content content = new Content();
        content.setAdditionalProperty("type", "text");
		content.setAdditionalProperty("text", originaltext);
        
		List<Content> ct = new ArrayList<Content>();
		ct.add(content);
		
		PostTextModel  contenttext = new PostTextModel();
		contenttext.setContent(ct);
		
		response =
		given()
	    	.filter(new RequestLoggingFilter(requestCapture))
	    	.spec(reqSpec)
			.contentType(ContentType.JSON)
			.body(contenttext)
		.when()
			.post()
		.then()
			.spec(resSpec)
			.statusCode(201).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("Created"))
			.log().all()
			.extract()
			.response();
		
		
		String responseString = JsonWriter.formatJson(response.asString());
		log.info("The body response is "+responseString);
		
		JsonPath js = new JsonPath(responseString);
		id=js.get("response.id");
		log.info("The post id is " + id);
		

	}
	

	@Test(dependsOnMethods={"TumblrPostandEditTest_PostTest"})
	public void TumblrPostandEditTest_verifyPostTest() {
		
		String methodName = new Object() {}
	      .getClass()
	      .getEnclosingMethod()
	      .getName();
		
		test = ExtentTestManager.startTest(methodName, "Starting test");	
		
		log.info("Running verifyTumblrPostTest");
		response =
		given()
    		.filter(new RequestLoggingFilter(requestCapture))
    		.spec(RestUtilities.createPathParam(reqSpec,"id", id))
		.when()
			.get(EndPoints.POSTS_SPECIFIC)
		.then()
			.spec(resSpec)
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.body("response.id", equalTo(Long.parseLong(id)))
			.body("response.summary",  equalTo(originaltext))
			.log().all()
			.extract()
			.response();
		
		String responseString = JsonWriter.formatJson(response.asString());
		log.info("The body response is "+responseString);
		
		JsonPath js = new JsonPath(responseString);
		
		Long verifyid = js.get("response.id");
		String newverifyid = Long.toString(verifyid);
		Assert.assertEquals(newverifyid, id);
		
		String posttext = js.get("response.summary");
		Assert.assertEquals(posttext, originaltext);

	}
	
	

	@Test(dependsOnMethods={"TumblrPostandEditTest_verifyPostTest"})
	public void TumblrPostandEditTest_EditPostTest() {
		
		String methodName = new Object() {}
	      .getClass()
	      .getEnclosingMethod()
	      .getName();
		
		test = ExtentTestManager.startTest(methodName, "Starting test");	
		
		log.info("Running EditTumblrPostTest");
		Content content = new Content();
        content.setAdditionalProperty("type", "text");
		content.setAdditionalProperty("text", editedtext);
        
		List<Content> ct = new ArrayList<Content>();
		ct.add(content);
		
		PostTextModel  contenttext = new PostTextModel();
		contenttext.setContent(ct);
		
		response =
		given()
			.filter(new RequestLoggingFilter(requestCapture))
			.spec(RestUtilities.createPathParam(reqSpec,"id", id))
			.contentType(ContentType.JSON)
			.body(contenttext)
		.when()
			.put(EndPoints.POSTS_EDIT)
		.then()
			.spec(resSpec)
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.log().all()
			.extract()
			.response();
		

		String responseString = JsonWriter.formatJson(response.asString());
		log.info("The body response is "+responseString);

	}



	@Test(dependsOnMethods={"TumblrPostandEditTest_EditPostTest"})
	public void TumblrPostandEditTest_verfiyEditPostTest() {
		
		String methodName = new Object() {}
	      .getClass()
	      .getEnclosingMethod()
	      .getName();
		
		test = ExtentTestManager.startTest(methodName, "Starting test");	
        
		log.info("Running verifyEditTumblrPostTest");
		response =
		given()
			.filter(new RequestLoggingFilter(requestCapture))
			.spec(RestUtilities.createPathParam(reqSpec,"id", id))
		.when()
			.get(EndPoints.POSTS_SPECIFIC)
		.then()
			.spec(resSpec)
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.body("response.id", equalTo(Long.parseLong(id)))
			.body("response.summary", equalTo(editedtext))
			.log().all()
			.extract()
			.response();
		
		String responseString = JsonWriter.formatJson(response.asString());
		log.info("The body response is "+responseString);
		
		JsonPath js = new JsonPath(responseString);
		Long verifyid = js.get("response.id");
		String newverifyid = Long.toString(verifyid);
		Assert.assertEquals(newverifyid, id);
		
		String edittext = js.get("response.summary");
		log.info("After editing, the post's text is: " + edittext);
		Assert.assertEquals(edittext, editedtext);
		
	}
	
	@AfterMethod
	public void tearDown() {
		requestCapture.flush();

	    test.log(LogStatus.INFO, "<pre>" + "Request : " + requestWriter.toString() + "</pre>");
	    test.log(LogStatus.INFO, "<pre>" + "Response: " + JsonWriter.formatJson(response.asString()) + "</pre>");  
		
	}
	
	
}
