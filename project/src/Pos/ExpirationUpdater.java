package Pos;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ExpirationUpdater implements Runnable {
	private Connection conn;

	public ExpirationUpdater(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(60000); // 1분마다 실행
				reduceExpirationTimes();
			} catch (InterruptedException | SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void reduceExpirationTimes() throws SQLException {
		// 데이터베이스 연결이 열려 있는지 확인
		if (conn != null && !conn.isClosed()) {
			Statement stmt = conn.createStatement();

			// 유통기한 감소
			String updateSql = "UPDATE items SET 유통기한 = 유통기한 - 1 WHERE 유통기한 > 0";

			// 유통기한이 0이 되면 구매할 수 없는 품목으로 표시
			String updateOutOfStockSql = "UPDATE items SET 재고 = 0 WHERE 유통기한 <= 0";

			stmt.executeUpdate(updateSql);
			stmt.executeUpdate(updateOutOfStockSql);

			stmt.close();
		} else {
			// 연결이 닫혀 있을 경우 적절한 처리를 수행
			System.out.println("데이터베이스 연결이 닫혀 있습니다.");
		}
	}
}