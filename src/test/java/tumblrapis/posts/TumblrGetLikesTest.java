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
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import resources.ExtentTestManager;
import tumblrapis.constants.Auth;
import tumblrapis.constants.EndPoints;
import tumblrapis.constants.Path;

public class TumblrGetLikesTest {
	
    static String API_KEY = Auth.API_KEY;
    private static Logger log = LogManager.getLogger(TumblrGetLikesTest.class);
    
    static StringWriter requestWriter;
    static PrintStream requestCapture;
    static ExtentTest test;
    static Response response;
	
	@BeforeClass
	public void setup() throws FileNotFoundException {
		RestAssured.baseURI = Path.BASE_URI;
		RestAssured.basePath = Path.LIKES;
		
		//Prints out request header and response body to separate file for just this test
		PrintStream fileOutPutStream = new PrintStream(new File("log4jlogs/TumblrGetLikesTest.txt"));
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
			.auth()
			.oauth(Auth.CONSUMER_KEY, Auth.CONSUMER_SECRET, Auth.ACCESS_TOKEN, Auth.ACCESS_SECRET)
			.pathParam("API_KEY", API_KEY)
			.log().all()
		.when()
			.post(EndPoints.LIKES_GET)
		.then()
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.log().all()
			.extract()
			.response();
		
		int inttotallikes = response.path("response.liked_count");
		String totallikes = Integer.toString(inttotallikes);
		log.info("There are "+totallikes+" total likes.");
		
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
		
		String responseString = JsonWriter.formatJson(response.asString());
		log.info("The response string is "+responseString);
		
		
	}
	
		
	@AfterMethod
	public void tearDown() {
		requestCapture.flush();

	    test.log(LogStatus.INFO, "<pre>" + "Request : " + requestWriter.toString() + "</pre>");
	    test.log(LogStatus.INFO, "<pre>" + "Response: " + JsonWriter.formatJson(response.asString()) + "</pre>");  
		
	}
	
}
