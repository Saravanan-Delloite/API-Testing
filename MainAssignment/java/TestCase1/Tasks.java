package TestCase1;
import UserDataBase.TaskDataBase;
import UserDataBase.UserDatabase;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.HashMap;
public class Tasks
{
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    static ExtentTest test;
    static ExtentReports extent;
    @BeforeClass
    public static ExtentHtmlReporter getHtmlReporter() throws Exception
    {
        ExtentHtmlReporter htmlReporter=new ExtentHtmlReporter("src\\test\\Resources\\extentReports2.html");
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
    public void addTaskTest() throws Exception
    {
        UserDatabase user= new UserDatabase();
        TaskDataBase tasksDB = new TaskDataBase();
        ArrayList list;
        HashMap hashMap;
        hashMap=user.registerUser();
        String checkToken = (String) hashMap.get("token");// storing the login token from the xl sheet(user details)
        list =tasksDB.addTasks();// calling the task
        try {
            for (int i = 0; i < list.size(); i++) {
                HashMap hash = new HashMap<>();
                hash.put("description", list.get(i));// adding task one by one
                Response response = requestSpecification.body(hash).auth().oauth2(checkToken).request(Method.POST, "/task");// post the task with auth
                //System.out.println(response.getStatusCode());// status code is generate as 201
                JSONObject object = new JSONObject(response.asString());// store the response in json object
                JSONObject getObject = object.getJSONObject("data");
                String storeOwnerInDB = (String) getObject.get("owner");
                tasksDB.storeOwnerInDB(storeOwnerInDB, i + 1);
                String storeIdInDB = (String) getObject.get("_id");
                tasksDB.storeIdInDB(storeIdInDB, i + 1);
            }
            test.pass("Tasks are added Successfully");
        }
        catch (Exception e)
        {
            test.fail("Tasks are not added");
        }
    }
    @Test(priority = 2)
    public void CheckTask() throws Exception
    {
        UserDatabase user =new UserDatabase();
        TaskDataBase tasksDB = new TaskDataBase();
        HashMap hashmap;
        ArrayList list,readOwnerFromDB,readIdFromDB;
        hashmap= user.registerUser();
        String token = (String) hashmap.get("token");
        list =tasksDB.addTasks();// calling the task
        readOwnerFromDB=tasksDB.ReadOwner();
        try
        {
            for (int i = 0; i < list.size(); i++) {
                HashMap hash = new HashMap<>();
                hash.put("description", list.get(i));// adding task one by one
                Response response = requestSpecification.body(hash).auth().oauth2(token).request(Method.GET, "/task");// get  each task with auth
                JSONObject jsonObject = new JSONObject(response.asString());// store each response in Json object
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                JSONObject getObject = jsonArray.getJSONObject(i);
                String owner = (String) getObject.get("owner");
                Assert.assertEquals(owner, readOwnerFromDB.get(i * 2));
                String id = (String) getObject.get("_id");

            }
            test.pass("Tasks are verified");
        }
        catch (Exception e)
        {
            test.fail("Task are not verified");
        }
    }
    @Test(priority = 3)
    public void callPagination1() throws Exception
    {
        UserDatabase userDatabase=new UserDatabase();
        int check1=userDatabase.paginationCheck("/task?limit=2");
        Assert.assertEquals(check1,2);
        test.pass("Pagination ran successfully for limit : 2");
    }
    @Test(priority = 4)
    public void callPagination2() throws Exception
    {
        UserDatabase userDatabase=new UserDatabase();
        int check2=userDatabase.paginationCheck("/task?limit=5");
        Assert.assertEquals(check2,5);
        test.pass("Pagination ran successfully for limit : 5");
        //logger.info("paggination passed succesfully");
    }
    @Test(priority = 5)
    public void callPagination3() throws Exception
    {
        UserDatabase userDatabase=new UserDatabase();
        int check3=userDatabase.paginationCheck("/task?limit=10");
        Assert.assertEquals(check3,10);
        test.pass("Pagination ran successfully for limit : 10");
    }
    @AfterClass
    public void end()
    {
        extent.flush();
    }
}
