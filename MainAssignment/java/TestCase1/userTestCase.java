package TestCase1;
import UserDataBase.UserDatabase;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.HashMap;

public class userTestCase
{
    String createToken;
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    static ExtentTest test;
    static ExtentReports extent;
    static Logger logger = LogManager.getLogger(UserDatabase.class);

    @BeforeClass
    public static ExtentHtmlReporter getHtmlReporter() throws Exception
    {
        ExtentHtmlReporter htmlReporter=new ExtentHtmlReporter("src\\test\\Resources\\extentReports.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        test = extent.createTest("MyFirstTest", "Sample description");
        return htmlReporter;
    }
    @BeforeClass
    public void setUp()
    {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri("https://api-nodejs-todolist.herokuapp.com").
                addHeader("Content-Type","application/json");
        requestSpecification = RestAssured.with().spec(requestSpecBuilder.build());
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder().
                expectContentType(ContentType.JSON);
        responseSpecification=responseSpecBuilder.build();

    }

    @Test(priority = 1)
    public void registerUserTest() throws Exception {

        HashMap hashmap;
        UserDatabase user =new UserDatabase();
        hashmap = user.registerUser();
        System.out.println(hashmap);
        Response response = requestSpecification.
                body(hashmap).
                when().
                post("/user/register").
                then().
                spec(responseSpecification).
                extract().
                response();
        Assert.assertEquals(response.statusCode(),201);
        System.out.println(createToken);
    }
    @Test(priority = 2)
    public void loginUserTest() throws Exception
    {
        UserDatabase user =new UserDatabase();
        HashMap hashmap;
        hashmap=user.registerUser();
        HashMap Hash = new HashMap<>();
        Hash.put("email",hashmap.get("email"));
        Hash.put("password",hashmap.get("password"));
        Response response = requestSpecification.
                body(hashmap).
                when().
                post("/user/login").
                then().spec(responseSpecification).
                extract().
                response();
        Assert.assertEquals(response.statusCode(),200);
        JSONObject object=new JSONObject(response.asString());
        createToken=object.getString("token");
        user.registerToken(createToken);
    }
    @Test(priority = 3)
    public void validToken() throws Exception
    {
        UserDatabase user =new UserDatabase();
        HashMap hashMap;
        hashMap = user.registerUser();
        String token = user.token;
        Response response = requestSpecification.auth().oauth2(token).
                when().get("/user/me").
                then().spec(responseSpecification).
                extract().
                response();
        JSONObject object = new JSONObject(response.asString());
        String CheckUserName = object.getString("name");
        String CheckUserEmail = object.getString("email");
        String CheckUSerAge = String.valueOf(object.getInt("age"));
        Assert.assertEquals(CheckUserName,hashMap.get("name"));
        Assert.assertEquals(CheckUserEmail,hashMap.get("email"));
        Assert.assertEquals(CheckUSerAge,hashMap.get("age"));
    }
    @Test(priority = 4)
    public void callPagination1() throws Exception
    {
        UserDatabase userDatabase=new UserDatabase();
        int check1=userDatabase.paginationCheck("/task?limit=2");
        Assert.assertEquals(check1,2);
        test.pass("Pagination 1 ran for 2");
        logger.info("Pagination 1 successfully ran for 2");
    }
    @Test(priority = 5)
    public void callPagination2() throws Exception
    {
        UserDatabase userDatabase=new UserDatabase();
        int check2=userDatabase.paginationCheck("/task?limit=5");
        Assert.assertEquals(check2,5);
        test.pass("Pagination 1 ran for 5");
        logger.info("Pagination  ran successfully for 5");
    }
    @Test(priority = 6)
    public void callPagination3() throws Exception
    {
        UserDatabase userDatabase=new UserDatabase();
        int check3=userDatabase.paginationCheck("/task?limit=10");
        Assert.assertEquals(check3,10);
        test.pass("Pagination  ran for 10");
        logger.info("Pagination  ran successfully for 10");
    }
    @AfterClass
    public void end()
    {
        extent.flush();
    }
}
