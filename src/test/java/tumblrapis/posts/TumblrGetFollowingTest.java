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
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import resources.ExtentTestManager;
import tumblrapis.common.RestUtilities;
import tumblrapis.constants.EndPoints;
import tumblrapis.constants.Path;

public class TumblrGetFollowingTest {
	
	private static Logger log = LogManager.getLogger(TumblrGetFollowingTest.class);
    static StringWriter requestWriter;
    static PrintStream requestCapture;
    static ExtentTest test;
	static Response response;
	
	RequestSpecification reqSpec;
	ResponseSpecification resSpec;
	   
	@BeforeClass
	public void setup() throws FileNotFoundException {
		reqSpec = RestUtilities.getRequestSpecification();
		reqSpec.basePath(Path.FOLLOWING);
		reqSpec.log().all();
		resSpec = RestUtilities.getResponseSpecification();
		
		//Prints out request header and response body to separate file for just this test
		PrintStream fileOutPutStream = new PrintStream(new File("log4jlogs/TumblrGetFollowingTest.txt"));
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
			.spec(reqSpec)
		.when()
			.post(EndPoints.FOLLOWING_GET)
		.then()
			.spec(resSpec)
			.statusCode(200).and()
			.contentType(ContentType.JSON).and()
			.body("meta.msg", equalTo("OK"))
			.body("response.total_blogs", equalTo(2))
			.body("response.blogs.name[0]", equalTo("moreapiautomationlearning"))
			.body("response.blogs.url[0]", equalTo("https://moreapiautomationlearning.tumblr.com/"))
			.body("response.blogs.name[1]", equalTo("staff"))
			.body("response.blogs.url[1]", equalTo("https://staff.tumblr.com/"))
			.log().all()
			.extract()
			.response();
		
		int inttotalblogs = response.path("response.total_blogs");
		String totalblogs = Integer.toString(inttotalblogs);
		log.info("There are "+totalblogs+" blogs you are following.");
		
		String following1 = response.path("response.blogs.name[0]");
		Assert.assertEquals(following1, "moreapiautomationlearning");
		
		String url1 = response.path("response.blogs.url[0]");
		Assert.assertEquals(url1, "https://moreapiautomationlearning.tumblr.com/");
		
		String following2 = response.path("response.blogs.name[1]");
		Assert.assertEquals(following2,"staff");
		
		String url2 = response.path("response.blogs.url[1]");
		Assert.assertEquals(url2, "https://staff.tumblr.com/");
		
		
		log.info("Verified you are following: "+following1+" and "+following2+".");
		
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
