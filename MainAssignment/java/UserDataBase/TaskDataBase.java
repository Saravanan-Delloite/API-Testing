package UserDataBase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
public class TaskDataBase
{
    public static String Task;
    public static String readOwner,readId;
    public ArrayList addTasks() throws Exception
    {
        ArrayList arrayList;
        arrayList =new ArrayList<>();
        String path = "src\\test\\Resources\\TaskDb.xlsx";
        FileInputStream inputStream = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum()+1;
        for(int i=1;i<rows;i++)
        {
            XSSFRow row =sheet.getRow(i);
            Task=row.getCell(0).getStringCellValue();
            arrayList.add(Task);
        }
        return arrayList;
    }
    public HashMap storeOwnerInDB(String storeOwner,int num) throws Exception
    {
        String path = "src\\test\\Resources\\TaskDb.xlsx";
        FileInputStream inputStream = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet worksheet = workbook.getSheetAt(0);
        FileOutputStream outputStream =new FileOutputStream(path);
        XSSFRow row = null;
        Cell cell ;
        cell = worksheet.getRow(num).getCell(1);
        //cell=row.createCell(1);
        cell.setCellValue(storeOwner);
        inputStream.close();
        workbook.write(outputStream);
        outputStream.close();
        HashMap hashMap = new HashMap<>();
        hashMap.put("Owner", storeOwner);
        return hashMap;
    }
    public ArrayList ReadOwner() throws Exception
    {
        ArrayList arrayList;
        arrayList =new ArrayList<>();
        String path = "src\\test\\resources\\TaskDb.xlsx";
        FileInputStream inputStream = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum()+1;
        for(int i=1;i<rows;i++)
        {
            XSSFRow row =sheet.getRow(i);
            readOwner=row.getCell(1).getStringCellValue();
            readId = row.getCell(2).getStringCellValue();
            arrayList.add(readOwner);
            arrayList.add(readId);
        }
        return arrayList;


    }
    public HashMap storeIdInDB(String storeId,int num) throws Exception
    {
        String path = "src\\test\\Resources\\TaskDb.xlsx";
        FileInputStream inputStream = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet worksheet = workbook.getSheetAt(0);
        FileOutputStream outputStream =new FileOutputStream(path);
        Cell cell ;
        cell = worksheet.getRow(num).createCell(2);
        cell.setCellValue(storeId);
        inputStream.close();

        workbook.write(outputStream);
        outputStream.close();
        HashMap hashMap = new HashMap<>();
        hashMap.put("token", storeId);
        return hashMap;
    }
}
