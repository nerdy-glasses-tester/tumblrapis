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

public class TumblrGetFollowersTest {
	
	private static Logger log = LogManager.getLogger(TumblrGetFollowersTest.class);
    static StringWriter requestWriter;
    static PrintStream requestCapture;
    static ExtentTest test;
    static Response response;
	
	@BeforeClass
	public void setup() throws FileNotFoundException {
		RestAssured.baseURI = Path.BASE_URI;
		RestAssured.basePath = Path.FOLLOWERS;
		
		//Prints out request header and response body to separate file for just this test
		PrintStream fileOutPutStream = new PrintStream(new File("log4jlogs/TumblrGetFollowersTest.txt"));
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
			.log().all()
		.when()
			.post(EndPoints.FOLLOWERS_GET)
		.then()
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.log().all()
			.extract()
			.response();
		
		int inttotalusers = response.path("response.total_users");
		String totalusers = Integer.toString(inttotalusers);
		log.info("There are "+totalusers+" followers.");
		
		String follower1 = response.path("response.users.name[0]");
		Assert.assertEquals(follower1, "moreapiautomationlearning");
		
		String url1 = response.path("response.users.url[0]");
		Assert.assertEquals(url1, "https://moreapiautomationlearning.tumblr.com/");
		
		String follower2 = response.path("response.users.name[1]");
		Assert.assertEquals(follower2,"cheezbot");
		
		String url2 = response.path("response.users.url[1]");
		Assert.assertEquals(url2, "https://cheezbot.tumblr.com/");
		
		log.info("Verified followers: "+follower1+" and "+follower2+".");
		
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
