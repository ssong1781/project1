package login.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBHelper {
	
//	private static final String DRIVER ="com.mysql.cj.jdbc.Driver";
//	private static final String URL = "jdbc:mysql://localhost:3306/smart";
//	private static final String USER = "smart";
//	private static final String PASS = "12345";
	
	private DBHelper() {}
	
	private static Connection conn = null;
	public static Connection getConnection() {
		// Connecion 객체가 참조하는 값이 없으면 생성
		if(conn == null) {
			try {
				
				// .properties 파일을 통해 설정 정보 읽어오기
				// Properties 객체 생성
				Properties prop = new Properties();
				
				// .properties 파일 경로 가져오기
				File file = new File("src/login/prop/mysql.properties");
				String path = file.getPath();
//				path = DBHelper.class.getResource("mysql.properties").getPath();
				
				// 읽어온 .properties 파일의 정보를
				// key & value 한쌍으로 Properties 객체에 저장
				prop.load(new FileReader(path));
				
				// key 값으로 DBMS 연결에 사용할 value값 을 가져옴
				String driver = prop.getProperty("driver");
				String url = prop.getProperty("url");
//				String user = prop.getProperty("user");
//				String password = prop.getProperty("password");
				
				// driver Class load
				Class.forName(driver);
				
				// DBMS 연결 정보 가지고옴.				
				//conn = DriverManager.getConnection(url,user,password);
				conn = DriverManager.getConnection(url,prop);
			}catch (IOException e) { 
				System.out.println("read file 오류 : " + e.getMessage());
			}catch (ClassNotFoundException e) {
				System.out.println("Driver 정보를 찾을 수 없음.");
			}catch (SQLException e) {
				System.out.println("DB 정보 오류 : "+e.getMessage());
			}
		}
		return conn;
	}
	
	// 외부자원을 사용하는 class는 자원 해제를 위해 AutoCloseable
	// interface를 구현하고 있음.
	// 가변형 인자열을 통해 불특정 개수의 instances를 전달 받아
	// 자원을 해제함.
	public static void close(AutoCloseable... closers) {
		for(AutoCloseable closer : closers) {
			try {
				if(closer != null) {
					closer.close();
				}
			} catch (Exception e) {}
		}
	}
	
	/*
	public static void close(Statement stmt) {
		try {
			if(stmt != null) stmt.close();
		} catch (SQLException e) {}
	}
	
	public static void close(Connection conn) {
		try {
			if(conn != null) conn.close();
		} catch (SQLException e) {}
	}
	*/
	
}
