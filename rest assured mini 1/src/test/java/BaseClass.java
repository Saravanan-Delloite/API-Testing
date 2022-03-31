import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
public class BaseClass
{
    @Test
    public void get_call()
    {
        given().baseUri("https://jsonplaceholder.typicode.com").
                header("Content-Type","application/json").
                when().get("/posts").
                then().body("id",hasItem(40),"userId",hasItem(4))
                .statusCode(200);
    }
    @Test
    public void put_call()
    {
        File JsonData=new File("C:\\Users\\ksaravanakumar\\postmanmini\\src\\main\\resources\\putdata.json");
        given().
                baseUri("https://reqres.in/api/users").
                body(JsonData).
                header("Content-Type","application/json").
                when().
                put("/users").
                then().
                statusCode(200).body("name",equalTo("Arun")).body("job",equalTo("Manager"));
    }
}
