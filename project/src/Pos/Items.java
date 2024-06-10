package Pos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Items {
	public static void main(String[] args) {
		try {
			// 데이터베이스 연결
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "1234");
			// Connection 객체를 저장하는 필드, DB 연결에 사용
			// conn: Connection 객체를 저장하는 필드, DB 연결에 사용
			System.out.println("구매할 수 있는 물품들의 정보");

			// SQL 쿼리 실행을 위한 Statement 객체 생성
			// res: ResultSet 객체를 저장하는 필드, 쿼리 실행 결과를 반환하는 데 사용
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM items"; // mysql에서 테이블 가져오기

			// 쿼리 실행 및 결과 가져오기
			ResultSet rs = stmt.executeQuery(sql);

			// 결과 출력
			while (rs.next()) {
				// 각 열의 값을 가져옴
				String 제품id = rs.getString("제품id");
				int 가격 = rs.getInt("가격");
				int 재고 = rs.getInt("재고");
				int 유통기한 = rs.getInt("유통기한");

				// 가져온 데이터 출력 또는 필요한 작업 수행
				System.out.println(
						"제품ID : " + 제품id + ", 가격 : " + 가격 + "원" + "," + "재고 : " + 재고 + "개" + ", 유통기한 : " + 유통기한 + "시간");
			}

			// 연결된 자원 닫기
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}
}