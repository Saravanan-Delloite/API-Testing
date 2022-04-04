package TestCase1;
import UserDataBase.TaskDataBase;
import UserDataBase.UserDatabase;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
public class Tasks
{
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
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
    @Test
    public void addTaskTest() throws Exception
    {
        UserDatabase user= new UserDatabase();
        TaskDataBase tasksDB = new TaskDataBase();
        ArrayList list;
        HashMap hashMap;
        hashMap=user.registerUser();
        String checkToken = (String) hashMap.get("token");// storing the login token from the xl sheet(user details)
        list =tasksDB.addTasks();// calling the task
        for(int i=0;i< list.size();i++)
        {
            HashMap hash = new HashMap<>();
            hash.put("description", list.get(i));// adding task one by one
            Response response = requestSpecification.body(hash).auth().oauth2(checkToken).request(Method.POST, "/task");// post the task with auth
            System.out.println(response.getStatusCode());// status code is generate as 201
            JSONObject object = new JSONObject(response.asString());// store the response in json object
            JSONObject getObject = object.getJSONObject("data");
            String storeOwnerInDB = (String) getObject.get("owner");
            tasksDB.storeOwnerInDB(storeOwnerInDB, i+1);
            String storeIdInDB = (String) getObject.get("_id");
            tasksDB.storeIdInDB(storeIdInDB,i+1);
        }
    }
    @Test
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
        for(int i=0;i< list.size();i++) {
            HashMap hash = new HashMap<>();
            hash.put("description", list.get(i));// adding task one by one
            Response response = requestSpecification.body(hash).auth().oauth2(token).request(Method.GET, "/task");// get  each task with auth
            JSONObject jsonObject = new JSONObject(response.asString());// store each response in Json object
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            JSONObject getObject = jsonArray.getJSONObject(i);
            String owner = (String) getObject.get("owner");
            Assert.assertEquals(owner,readOwnerFromDB.get(i*2));
            String id = (String) getObject.get("_id");
        }
    }
}
