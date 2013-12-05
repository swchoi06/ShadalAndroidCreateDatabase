import java.beans.Statement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class DataParser {
	public static Connection conn;
	public static XSSFWorkbook workbook;
	public static int id_counter = 0;

	public static void setDatabase() throws Exception{
		Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:Shadal");
	}
	public static void createDatabase() throws Exception{
		java.sql.Statement stat = conn.createStatement();

        stat.executeUpdate("drop table if exists restaurants;");
        stat.executeUpdate("create table restaurants (id INTEGER PRIMARY KEY, name TEXT, category TEXT, "
        		+ "openingHours TEXT, closingHours TEXT, phoneNumber TEXT);");
        
        java.sql.Statement stat2 = conn.createStatement();
        stat.executeUpdate("drop table if exists menus;");
        stat.executeUpdate("create table menus (id INTEGER PRIMARY KEY, menu TEXT, section TEXT, "
        		+ "price INT, restaurant_id INT);");
	}
	public static void addResToDatabase(XSSFSheet sheet) throws Exception{
		System.out.println(sheet.getRow(1).getCell(2).getStringCellValue());
		PreparedStatement prepRes = conn.prepareStatement("insert into restaurants values (?, ?, ?, ?, ?, ?);");

        prepRes.setString(2, sheet.getRow(1).getCell(2).getStringCellValue());
        prepRes.setString(3, sheet.getRow(2).getCell(2).getStringCellValue());
        prepRes.setString(4, Integer.toString((int)(sheet.getRow(3).getCell(2).getNumericCellValue())));
        prepRes.setString(5, Integer.toString((int)(sheet.getRow(3).getCell(3).getNumericCellValue())));
        prepRes.setString(6, sheet.getRow(4).getCell(2).getStringCellValue());
        prepRes.addBatch();

        conn.setAutoCommit(false);
        prepRes.executeBatch();
        conn.setAutoCommit(true);
        
        id_counter++;
        
        int menu_count = (int)sheet.getRow(6).getCell(2).getNumericCellValue();
        
        for(int i=0; i<menu_count; i++){
    		PreparedStatement prepMenu = conn.prepareStatement("insert into menus values (?, ?, ?, ?, ?);");
    		prepMenu.setString(2, sheet.getRow(8+i).getCell(3).getStringCellValue());
    		prepMenu.setString(3, sheet.getRow(8+i).getCell(0).getStringCellValue());
    		prepMenu.setInt(4, (int)(sheet.getRow(8+i).getCell(8).getNumericCellValue()));
    		prepMenu.setInt(5, id_counter);
    		prepMenu.addBatch();
    		
            conn.setAutoCommit(false);
            prepMenu.executeBatch();
            conn.setAutoCommit(true);
        }
	}
	public static void main(String[ ] args) throws Exception
	 {
        setDatabase();
		createDatabase();
		
        // set workbook
		FileInputStream file = new FileInputStream(new File("/Users/Sukwon/Desktop/SNU.xlsx"));
		workbook = new XSSFWorkbook(file);

		int numberOfSheet = workbook.getNumberOfSheets();
//		numberOfSheet = 5;
		System.out.println(numberOfSheet);
		for(int i = 0; i< numberOfSheet-1; i++){
			XSSFSheet sheet = workbook.getSheetAt(i);
			addResToDatabase(sheet);
		}
		
		java.sql.Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery("select * from restaurants;");
        while (rs.next()) {
        	System.out.println("id = " + rs.getInt("id"));
            System.out.println("name = " + rs.getString("name"));
            System.out.println("phoneNumber = " + rs.getString("phoneNumber"));
            System.out.println("category = " + rs.getString("category"));
            System.out.println("openingHours = " + rs.getString("openingHours"));
            System.out.println("closingHours = " + rs.getString("closingHours"));
        }
        rs.close();
/*
        
        prepRes.setString(2, "옹골찬 도시락");
        prepRes.setString(3, "02-123-1234");
        prepRes.setString(4, "한식/분");
        prepRes.setString(5, "11");
        prepRes.setString(6, "23");
        prepRes.addBatch();
        
        prepRes.setString(2, "영구스피");
        prepRes.setString(3, "02-122-1234");
        prepRes.setString(4, "피자");
        prepRes.setString(5, "10");
        prepRes.setString(6, "20");
        prepRes.addBatch();

        conn.setAutoCommit(false);
        prepRes.executeBatch();
        conn.setAutoCommit(true);
        
        ResultSet rs = stat.executeQuery("select * from restaurants;");
        while (rs.next()) {
        	System.out.println("id = " + rs.getInt("id"));
            System.out.println("name = " + rs.getString("name"));
            System.out.println("phoneNumber = " + rs.getString("phoneNumber"));
            System.out.println("category = " + rs.getString("category"));
            System.out.println("openingHours = " + rs.getString("openingHours"));
            System.out.println("closingHours = " + rs.getString("closingHours"));
        }
        rs.close();
        conn.close();*/
	 }
}
