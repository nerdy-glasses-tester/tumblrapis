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
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.StringWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import resources.ExtentTestManager;
import tumblrapis.common.RestUtilities;
import tumblrapis.constants.Auth;
import tumblrapis.constants.EndPoints;
import tumblrapis.constants.Path;


public class TumblrGetAllPostsTest{

   static String id = "";
   static String API_KEY = Auth.API_KEY;
   static String firstposttext = "Edited, Hello this is my first post from postman!";
	
   private static Logger log = LogManager.getLogger(TumblrGetAllPostsTest.class);
   
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
		//reqSpec.pathParam("API_KEY", API_KEY);
		reqSpec.log().all();
		resSpec = RestUtilities.getResponseSpecification();
		
		//Prints out request header and response body to separate file for just this test
		PrintStream fileOutPutStream = new PrintStream(new File("log4jlogs/TumblrGetAllPostsTest.txt"));
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
	public void GetAllTumblrPostsTest() {
		
		String methodName = new Object() {}
	      .getClass()
	      .getEnclosingMethod()
	      .getName();
		
		test = ExtentTestManager.startTest(methodName, "Starting test");	
		
		log.info("Running GetAllTumblrPostsTest");
		response =
		given()
		    .filter(new RequestLoggingFilter(requestCapture))
			.spec(RestUtilities.createPathParam(reqSpec,"API_KEY",API_KEY))
			//.spec(reqSpec)
		.when()
			.post(EndPoints.POSTS_GETALL)
		.then()
			.spec(resSpec)
			.statusCode(200)
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.body("response.blog.total_posts", greaterThanOrEqualTo(2))
			.body("response.posts[-1].summary", equalTo("Edited, Hello this is my first post from postman!"))
			.log().all()
			.extract()
			.response();
		
		
		//Validate the total posts is greater or equal to 2 and also validate the first post's summary.
		
		int inttotalposts = response.path("response.blog.total_posts");
		String totalposts = Integer.toString(inttotalposts);
		log.info("There are "+totalposts+" posts.");
		
		inttotalposts = inttotalposts-1;
		Long longid = response.path("response.posts["+inttotalposts+"].id");
		id = Long.toString(longid);
		log.info("The first post id is " + id);

		String summary = response.path("response.posts["+inttotalposts+"].summary");
		log.info("The first post contains the following text: "+summary);
		
		Assert.assertEquals(summary, firstposttext);
		
		String responseString = JsonWriter.formatJson(response.asString());
		log.info("The response is "+responseString);
		

	}

	@AfterMethod
	public void tearDown() {
		requestCapture.flush();

	    test.log(LogStatus.INFO, "<pre>" + "Request : " + requestWriter.toString() + "</pre>");
	    test.log(LogStatus.INFO, "<pre>" + "Response: " + JsonWriter.formatJson(response.asString()) + "</pre>");  
		
	}
	
}
