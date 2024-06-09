package Pos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionTest {
	public static void main(String[] args) {
		Connection conn = null;
		try {
			// 데이터베이스 연결
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "1234");
			if (conn != null) {
				System.out.println("데이터베이스 연결 성공");
			}
		} catch (SQLException ex) {
			// 연결 실패한 경우 예외 메시지 출력
			ex.printStackTrace();
			System.out.println("데이터베이스 연결 실패");
		} finally {
			// 연결 종료
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
