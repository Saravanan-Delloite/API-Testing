package UserDataBase;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class UserDatabase
{
    String UserName = null, UserEmail = null, UserPassword = null,UserAge = null;
    public String token;



    public HashMap registerUser() throws Exception
    {
        String path = "src\\test\\Resources\\Book1.xlsx";
        FileInputStream inputStream = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        DataFormatter dataFormatter = new DataFormatter();
        XSSFSheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum() + 1;
        for (int i = 1; i < rows; i++)
        {
            XSSFRow row = sheet.getRow(i);
            UserName = row.getCell(0).getStringCellValue();
            UserEmail = dataFormatter.formatCellValue(sheet.getRow(i).getCell(1));
            UserPassword = dataFormatter.formatCellValue(sheet.getRow(i).getCell(2));
            UserAge = dataFormatter.formatCellValue(sheet.getRow(i).getCell(3));
            token = dataFormatter.formatCellValue(sheet.getRow(i).getCell(4));
        }
        HashMap hashMap = new HashMap();
        hashMap.put("name", UserName);
        hashMap.put("email", UserEmail);
        hashMap.put("password", UserPassword);
        hashMap.put("age", UserAge);
        hashMap.put("token",token);
        return hashMap;
    }
    public HashMap registerToken(String RegisterToken) throws Exception
    {
        String path = "src\\test\\Resources\\Book1.xlsx";
        FileInputStream inputStream = new FileInputStream(path);
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet worksheet = wb.getSheetAt(0);
        Cell cell = null;
        cell = worksheet.getRow(1).getCell(4);
        cell.setCellValue(RegisterToken);
        inputStream.close();
        FileOutputStream outputStream =new FileOutputStream(path);
        wb.write(outputStream);
        outputStream.close();
        HashMap hashMap = new HashMap<>();
        hashMap.put("token", RegisterToken);
        return hashMap;
    }
    public int paginationCheck(String Uri) throws Exception
    {
        UserDatabase userDetails =new UserDatabase();
        HashMap hashMap;
        hashMap=userDetails.registerUser();
        String authToken= (String) hashMap.get("token");
        //System.out.println(authToken);
        Response response =
            given().
                    baseUri("https://api-nodejs-todolist.herokuapp.com").header("Content-Type", "application/json").auth().oauth2(authToken).
                    when().
                    get(Uri).
                    then().
                    extract().response();
        //System.out.println(response.asString());
        JSONObject jsonObject=new JSONObject(response.asString());
        //System.out.println(jsonObject.get("count"));
        return (int) jsonObject.get("count");
    }
}

