package Pos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;
import java.util.Timer;

class Purchase {
	private String productId;
	private int quantity;

	public Purchase(String productId, int quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}

	public String getProductId() {
		return productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Purchase purchase = (Purchase) o;
		return Objects.equals(productId, purchase.productId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(productId);
	}
}

public class number extends Pos {
	protected static final int BASIC_BALANCE = 390500; // 기본 잔고
	protected static int balance = BASIC_BALANCE; // 현재 잔고
	protected static int revenue = 0; // 매출액
	protected static ArrayList<Purchase> purchases = new ArrayList<>();
	protected static Timer expirationTimer = new Timer();

	// 메뉴 표시
	private static void Menu() {
		System.out.println("--메뉴를 선택하세요--");
		System.out.println("[1] 재고 체크");
		System.out.println("[2] 현재 잔고 체크");
		System.out.println("[3] 매출액");
		System.out.println("[4] 유통기한 체크");
		System.out.println("[5] 업무 시작");
		System.out.println("[6] 재고, 잔고, 유통기한 초기화");
		System.out.println("[7] 프로그램 종료");
		System.out.println("-----------------");
		// 사용자 선택 입력 받기
	}

	// DB 연결, 메인 메뉴 출력
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Connection conn = null;
		try {
			// 데이터베이스 연결
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "1234");

			// 유통기한 감소 스레드 시작
			ExpirationUpdater expirationChecker = new ExpirationUpdater(conn);
			Thread expirationThread = new Thread(expirationChecker);
			expirationThread.start();

			boolean continueProgram = true;
			while (continueProgram) {
				Menu(); // 메뉴 출력

				// 사용자 선택 입력 받기
				System.out.print("번호를 선택하세요 : ");
				int choice = scanner.nextInt();
				System.out.println("");
				// 선택에 따라 해당 기능 실행
				switch (choice) {
				case 1:
					check1(conn);
					break;
				case 2:
					check2(conn);
					break;
				case 3:
					check3(conn);
					break;
				case 4:
					check4(conn);
					break;
				case 5:
					start(conn);
					break;
				case 6:
					resetInventoryAndBalance(conn);
					break;
				case 7:
					endProgram(conn);
					continueProgram = false; // 프로그램 종료
					break;
				default:
					System.out.println("잘못된 선택입니다.");
				}
			}
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		} finally {
			// 연결된 자원 닫기
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
					System.out.println("SQLException: " + ex.getMessage());
				}
			}
		}
	}

	// 시각 가져오기
	private static void endProgram(Connection conn) throws SQLException {
		// 현재 시각 가져오기
		Pos pos = new Pos();
		pos.endProgram();
		expirationTimer.cancel(); // 타이머 종료
	}

	// 재고 초기화
	private static void resetInventoryAndBalance(Connection conn) throws SQLException {
		try {
			// SQL 쿼리 실행을 위한 Statement 객체 생성
			Statement stmt = conn.createStatement();

			// 재고와 balance를 초기값으로 재설정하는 쿼리
			String resetSql = "UPDATE items SET 재고 = 10"; // 초기값으로 재고를 10으로 설정
			stmt.executeUpdate(resetSql);
			balance = BASIC_BALANCE;

			String resetExpirationSql = "UPDATE items SET 유통기한 = CASE " + "WHEN 제품id = '우유' THEN 24 "
					+ "WHEN 제품id = '두부' THEN 24 " + "WHEN 제품id = '오뎅' THEN 1 " + "WHEN 제품id = '초콜릿' THEN 5 "
					+ "WHEN 제품id = '삼각김밥' THEN 10 " + "WHEN 제품id = '포카리' THEN 15 " + "WHEN 제품id = '담배' THEN 24 "
					+ "WHEN 제품id = '소주' THEN 24 " + "WHEN 제품id = '라면' THEN 8 " + "WHEN 제품id = '햇반' THEN 10 " + "END";
			stmt.executeUpdate(resetExpirationSql);

			System.out.println("재고와 잔고와 유통기한이 초기값으로 되돌려졌습니다.");
			printMenu(conn);
			// 연결된 자원 닫기
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}

	// 재고 체크 메서드
	private static void check1(Connection conn) throws SQLException {
		try {
			// SQL 쿼리 실행을 위한 Statement 객체 생성
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM items"; // mysql에서 테이블 가져오기

			// 쿼리 실행 및 결과 가져오기
			ResultSet rs = stmt.executeQuery(sql);

			// 결과 출력
			System.out.println("[현재 재고 정보]");
			System.out.println("제품ID\t재고");
			System.out.println("------------");
			while (rs.next()) {
				// 각 열의 값을 가져옴
				String 제품id = rs.getString("제품id");
				int 재고 = rs.getInt("재고");
				// 가져온 데이터 출력
				System.out.println(제품id + "\t" + 재고 + "개");
				System.out.println("------------");
			}

			// 연결된 자원 닫기
			rs.close();
			stmt.close();

			// 메뉴로 돌아가기
			printMenu(conn);
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}

	// 현재 잔고 체크 메서드
	private static void check2(Connection conn) throws SQLException {
		System.out.println("현재 잔고는 [" + balance + "]원 입니다.");
		printMenu(conn);
	}

	// 매출액 체크 메서드
	private static void check3(Connection conn) throws SQLException {
		// 매출액 체크 로직 추가
		// 이 부분은 프로젝트 요구사항에 맞게 작성하세요.
		System.out.println("일일 매출액은 [" + revenue + "]원 입니다.");
		printMenu(conn);
	}

	// 유통기한 체크 메서드
	private static void check4(Connection conn) throws SQLException {
		try {
			// SQL 쿼리 실행을 위한 Statement 객체 생성
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM items"; // mysql에서 테이블 가져오기

			// 쿼리 실행 및 결과 가져오기
			ResultSet rs = stmt.executeQuery(sql);

			// 결과 출력을 위한 ArrayList 생성
			ArrayList<String> productIDs = new ArrayList<>();
			ArrayList<Integer> expirationTimes = new ArrayList<>();

			// 결과를 ArrayList에 저장
			while (rs.next()) {
				String 제품id = rs.getString("제품id");
				int 유통기한 = rs.getInt("유통기한");
				productIDs.add(제품id);
				expirationTimes.add(유통기한);
			}

			// 가져온 데이터 출력
			System.out.println("유통기한 정보");
			for (int i = 0; i < productIDs.size(); i++) {
				System.out.println("제품ID: " + productIDs.get(i) + ", 유통기한: " + expirationTimes.get(i) + "시간");
			}
			System.out.println("");

			// 연결된 자원 닫기
			rs.close();
			stmt.close();
			printMenu(conn);
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}

	// 업무 메뉴 메서드
	private static void start(Connection conn) throws SQLException {
		Scanner scanner = new Scanner(System.in);
		boolean isValidChoice = false;

		System.out.println("----------업무를 시작합니다----------");
		System.out.println("[5]-[1] 물건 판매");
		System.out.println("[5]-[2] 물건 환불");
		System.out.println("[5]-[3] 미성년자 판매 금지 물품");
		System.out.println("[5]-[4] 물품 입고 화면");
		System.out.println("[5]-[5] 물품 재고 및 제품 이름 검색 화면");
		System.out.println("[5]-[6] 메인 메뉴로 돌아가기");
		System.out.println("---------------------------------");

		while (!isValidChoice) {
			System.out.print("번호를 선택하세요 : ");
			int choice2 = scanner.nextInt();

			// 입력 버퍼 비우기
			scanner.nextLine();

			switch (choice2) {
			case 1:
				check5_1(conn);
				isValidChoice = true; // 올바른 선택이므로 반복 종료
				break;
			case 2:
				check5_2(conn);
				isValidChoice = true; // 올바른 선택이므로 반복 종료
				break;
			case 3:
				check5_3(conn);
				isValidChoice = true; // 올바른 선택이므로 반복 종료
				break;
			case 4:
				check5_4(conn);
				isValidChoice = true; // 올바른 선택이므로 반복 종료
				break;
			case 5:
				check5_5(conn);
				isValidChoice = true; // 올바른 선택이므로 반복 종료
				break;
			case 6:
				check5_6(conn);
				isValidChoice = true; // 올바른 선택이므로 반복 종료
				break;
			default:
				System.out.println("잘못된 선택입니다. 다시 선택하세요.");
				System.out.println("---------------------------------");
				System.out.println("[5]-[1] 물건 판매");
				System.out.println("[5]-[2] 물건 환불");
				System.out.println("[5]-[3] 미성년자 판매 금지 물품");
				System.out.println("[5]-[4] 물품 입고 화면");
				System.out.println("[5]-[5] 물품 재고 및 제품 이름 검색 화면");
				System.out.println("---------------------------------");
			}
		}
	}

	// 제품 판매
	private static void check5_1(Connection conn) throws SQLException {
		Scanner scanner = new Scanner(System.in);
		ExpirationUpdater updater = new ExpirationUpdater(conn);
		updater.reduceExpirationTimes();

		try {
			// SQL 쿼리 실행을 위한 Statement 객체 생성
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM items"; // mysql에서 테이블 가져오기

			// 쿼리 실행 및 결과 가져오기
			ResultSet rs = stmt.executeQuery(sql);

			// 결과 출력
			System.out.println("현재 제품 목록:");
			while (rs.next()) {
				// 각 열의 값을 가져옴

				String 제품id = rs.getString("제품id");
				int 가격 = rs.getInt("가격");
				int 재고 = rs.getInt("재고");
				int 유통기한 = rs.getInt("유통기한");
				ArrayList<Integer> expirationTimes = new ArrayList<>();
				expirationTimes.add(유통기한);
				// 가져온 데이터 출력
				System.out.println("제품ID: " + 제품id + ", 가격: " + 가격 + "원, 재고: " + 재고 + "개, 유통기한: " + 유통기한 + "시간");
			}

			// 여러 제품 구매 과정
			while (true) {
				System.out.print("구매할 제품의 ID를 입력하시오: ");
				String productId = scanner.next();

				if (productId.equals("소주") || productId.equals("담배")) {
					check5_3(conn);
				}

				System.out.print("구매할 제품의 수량을 입력하시오: ");
				int quantity = scanner.nextInt();

				System.out.print("카드 번호를 입력해주세요 (16자리): ");
				String cardNumberInput;
				while (true) {
					cardNumberInput = scanner.next();
					if (cardNumberInput.length() == 16) {
						break; // 올바른 길이의 카드 번호가 입력될 때까지 반복
					} else {
						System.out.print("카드 번호를 16자리로 다시 입력해주세요: ");
					}
				}

				// 입력한 카드 번호에 하이픈 추가
				String formattedCardNumber = formatCardNumber(cardNumberInput);

				System.out.println("카드 번호: " + formattedCardNumber);

				// 제품 가격과 재고 확인
				sql = "SELECT 가격, 재고 FROM items WHERE 제품id = '" + productId + "'";
				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					int 가격 = rs.getInt("가격");
					int 재고 = rs.getInt("재고");

					if (quantity > 재고) {
						System.out.println(
								productId + "(이)가 품절되었거나 유통기한이 지나 구매할 수 없습니다.\n다른 제품을 구매하시겠습니까? (1. 예 | 2. 아니오)");
						int answer = scanner.nextInt();
						if (answer == 1) {
							continue; // 다른 제품을 구매하기 위해 반복문을 계속 진행
						} else {
							returnMenu(conn);
							break; // 반복문 탈출
						}
					}

					int totalAmount = 가격 * quantity;

					purchases.add(new Purchase(productId, quantity)); // 구매 내역에 추가

					// 초기 카드 잔액 설정
					int cardBalance = 10000; // 초기 잔액을 10,000원으로 설정

					System.out.println("카드 잔액: " + cardBalance + "원");

					if (cardBalance < totalAmount) {
						System.out.println("카드 잔액이 부족합니다. 구매를 중단합니다.");
						returnMenu(conn);
						break; // 반복문 탈출
					}

					int change = cardBalance - totalAmount;
					balance += totalAmount; // 잔고에 추가
					revenue += totalAmount; // 매출액 증가

					// 재고 업데이트
					int newStock = 재고 - quantity;
					sql = "UPDATE items SET 재고 = " + newStock + " WHERE 제품id = '" + productId + "'";
					stmt.executeUpdate(sql);

					System.out.println("거래가 완료되었습니다.\n거스름돈: " + change + "원");
					System.out.println("잔고: " + balance + "원");
					System.out.println("매출액: " + revenue + "원");
					System.out.println("남은 재고: " + newStock + "개");

					// 다른 제품을 구매할지 묻는 구문
					System.out.println("다른 제품을 구매하겠습니까? (1. 예 | 2. 아니오)");
					int buyAnother = scanner.nextInt();
					if (buyAnother == 1) {
						continue; // 반복문으로 돌아가 다른 제품 구매 과정을 진행
					} else {
						returnMenu(conn);
						break; // 반복문 탈출
					}
				} else {
					System.out.println("해당 제품이 존재하지 않습니다.");
					returnMenu(conn);
					break; // 반복문 탈출
				}
			}
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}

	// 물건 판매에서 카드 번호 입력 받는 거 (4-4-8)
	private static String formatCardNumber(String cardNum) {
		StringBuilder formatted = new StringBuilder();
		formatted.append(cardNum.substring(0, 4)).append("-");
		formatted.append(cardNum.substring(4, 8)).append("-");
		formatted.append(cardNum.substring(8, 12)).append("-");
		formatted.append(cardNum.substring(12, 16));
		return formatted.toString();
	}

	// 제품 환불
	private static void check5_2(Connection conn) throws SQLException {
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.println("환불할 물건의 제품ID와 수량을 입력하세요.");
			System.out.print("제품ID: ");
			String productId = scanner.nextLine();

			System.out.print("수량: ");
			int quantity;
			try {
				quantity = Integer.parseInt(scanner.nextLine());
				if (quantity <= 0) {
					throw new InputMismatchException();
				}
			} catch (InputMismatchException e) {
				System.out.println("유효하지 않은 입력입니다. 양의 정수 수량을 입력하세요.");
				if (askRetry(scanner))
					continue;
				else
					return;
			}

			// 구매 내역에서 제품을 찾기
			Purchase foundPurchase = null;
			for (Purchase purchase : purchases) {
				if (purchase.getProductId().equals(productId)) {
					foundPurchase = purchase;
					break;
				}
			}

			if (foundPurchase == null) {
				System.out.println("구매했던 제품이 아닙니다. 환불이 불가능합니다.");
				if (askRetry(scanner))
					continue;
				else
					return;
			}

			if (foundPurchase.getQuantity() < quantity) {
				System.out.println("환불 수량이 구매 수량을 초과합니다.");
				if (askRetry(scanner))
					continue;
				else
					return;
			}

			// 환불 처리 로직
			try {
				Statement stmt = conn.createStatement();
				String checkStockSql = "SELECT 재고, 가격 FROM items WHERE 제품id = '" + productId + "'";
				ResultSet rs = stmt.executeQuery(checkStockSql);

				if (rs.next()) {
					int stock = rs.getInt("재고");
					int price = rs.getInt("가격");

					// 재고 업데이트
					int newStock = stock + quantity;
					String updateStockSql = "UPDATE items SET 재고 = " + newStock + " WHERE 제품id = '" + productId + "'";
					stmt.executeUpdate(updateStockSql);

					// 잔고 업데이트
					int totalPrice = price * quantity;
					balance -= totalPrice;
					revenue -= totalPrice;

					// 구매 내역 업데이트
					foundPurchase.setQuantity(foundPurchase.getQuantity() - quantity);
					if (foundPurchase.getQuantity() == 0) {
						purchases.remove(foundPurchase);
					}

					System.out.println("환불 완료. 환불 금액: " + totalPrice + "원, 현재 잔고: " + balance + "원");
					// 다른 제품을 환불할지 묻는 구문
					System.out.println("다른 제품도 환불하겠습니까? (1. 예 | 2. 아니오)");
					int refundAnother = scanner.nextInt();
					scanner.nextLine(); // 버퍼 비우기
					if (refundAnother == 1) {
						continue; // 반복문으로 돌아가 다른 제품 환불 과정을 진행
					} else {
						returnMenu(conn);
						break; // 반복문 탈출
					}
				} else {
					System.out.println("존재하지 않는 제품ID입니다.");
				}

				rs.close();
				stmt.close();
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
			}

			break;
		}
	}

	private static boolean askRetry(Scanner scanner) {
		System.out.print("환불을 다시 진행하시겠습니까? (1. 예 | 2. 아니오): ");
		int answer = scanner.nextInt();
		scanner.nextLine(); // 버퍼 정리
		return answer == 1;
	}

	// 미성년자 판매 금지 물품 체크
	private static void check5_3(Connection conn) throws SQLException {
		// 미성년자 판매 금지 물품 체크 로직 추가
		Scanner scanner = new Scanner(System.in);
		try {
			System.out.println("미성년자일 경우 구매가 불가능한 상품입니다.");
			System.out.println("생년월일을 입력해주세요 (YYYYMMDD) : ");
			int age = scanner.nextInt(); // 생년월일을 숫자로 입력받음

			if (age <= 20050101) {
				System.out.println("성인 확인이 되었습니다. 구매를 진행할 수 있습니다.");
			} else {
				System.out.println("미성년자입니다. 구매가 불가능합니다.");
				returnMenu(conn);
				// 미성년자 판매가 불가능한 상품에 대한 로직 추가
			}
		} catch (InputMismatchException ex) {
			System.out.println("잘못된 입력 형식입니다. 생년월일을 다시 입력해주세요.");
			check5_3(conn);
		}
	}

	// 제품 입고 화면
	private static void check5_4(Connection conn) throws SQLException {
		Scanner scanner = new Scanner(System.in);

		try {
			System.out.print("입고할 제품ID를 입력하시오 : ");
			String productId = scanner.next();

			System.out.print("입고할 수량을 입력하시오 : ");
			int quantity = scanner.nextInt();

			System.out.print("제품의 가격을 입력하시오 : ");
			int productPrice = scanner.nextInt();

			System.out.print("유통기한(시간)을 입력하시오 : ");
			int expiration = scanner.nextInt();

			Statement stmt = conn.createStatement();
			String checkExistQuery = "SELECT * FROM items WHERE 제품id = '" + productId + "'";
			ResultSet existResult = stmt.executeQuery(checkExistQuery);

			if (existResult.next()) {
				int currentStock = existResult.getInt("재고");
				int updatedStock = currentStock + quantity;
				int updatedExpiration = expiration;

				String updateQuery = "UPDATE items SET 재고 = " + updatedStock + ", 유통기한 = " + updatedExpiration
						+ " WHERE 제품id = '" + productId + "'";
				stmt.executeUpdate(updateQuery);

				System.out.println(
						productId + " 제품의 재고가 " + updatedStock + "개로, 유통기한이 " + updatedExpiration + "시간으로 업데이트되었습니다.");
				returnMenu(conn);
			} else {
				String insertQuery = "INSERT INTO items (제품id, 재고, 가격, 유통기한) VALUES ('" + productId + "', " + quantity
						+ ", " + productPrice + ", " + expiration + ")";
				stmt.executeUpdate(insertQuery);

				System.out.println(productId + " 제품이 " + quantity + "개로 추가되었습니다. 유통기한은 " + expiration + "시간입니다.");
				returnMenu(conn);
			}
		} catch (SQLException e) {
			System.out.println("SQL 오류: " + e.getMessage());
		}
	}

	// 제품 검색 확인
	private static void check5_5(Connection conn) throws SQLException {
		Scanner scanner = new Scanner(System.in);
		boolean continueSearching = true;

		while (continueSearching) {
			System.out.print("검색할 제품의 ID를 입력하세요 : ");
			String productId = scanner.next();

			try {
				// SQL 쿼리 실행을 위한 Statement 객체 생성
				Statement stmt = conn.createStatement();

				// 해당 제품 ID에 대한 정보를 검색하는 쿼리
				String sql = "SELECT * FROM items WHERE 제품id = '" + productId + "'";
				ResultSet rs = stmt.executeQuery(sql);

				// 검색된 결과 출력
				if (rs.next()) {
					System.out.println("[검색 결과]");
					System.out.println("제품ID: " + rs.getString("제품id"));
					System.out.println("가격: " + rs.getInt("가격") + "원");
					System.out.println("재고: " + rs.getInt("재고") + "개");
					System.out.println("유통기한: " + rs.getInt("유통기한") + "시간");
					// 필요한 정보 추가 출력
				} else {
					System.out.println("해당 제품이 존재하지 않습니다.");
					returnMenu(conn);
					continueSearching = false; // 검색 종료
				}

				// 연결된 자원 닫기
				rs.close();
				stmt.close();
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
			}

			if (!continueSearching) {
				break; // 검색 종료시 반복문 탈출
			}

			// 다른 제품도 검색해볼지 묻기
			System.out.println("다른 제품도 검색해보시겠습니까? (1: 예 | 2: 아니오)");
			int choice = scanner.nextInt();
			if (choice != 1) {
				returnMenu(conn); // 메인 메뉴로 이동
				break;
			}
		}
	}

	// 메인 메뉴로 이동
	private static void check5_6(Connection conn) throws SQLException {
		printMenu(conn);
	}

	// 메인 메뉴로 이동
	private static void printMenu(Connection conn) throws SQLException {
		Scanner scanner = new Scanner(System.in);
		try {
			int choice;
			do {
				System.out.println("메인 메뉴로 돌아가시겠습니까? (1: 예 | 2: 아니오)");
				while (!scanner.hasNextInt()) {
					System.out.println("잘못된 입력입니다. 다시 입력하세요.");
					scanner.next(); // 잘못된 입력값을 버퍼에서 제거
				}
				choice = scanner.nextInt();
				if (choice == 1) {
					// 메뉴로 돌아가기
					return; // 메인 루프로 돌아감
				} else if (choice == 2) {
					// 프로그램 종료
					System.out.println("프로그램을 종료합니다.");
					System.exit(0);
				} else {
					System.out.println("잘못된 선택입니다. 다시 입력하세요.");
				}
			} while (choice != 1 && choice != 2); // 유효한 선택이 입력될 때까지 반복
		} catch (InputMismatchException e) {
			System.out.println("올바른 선택이 아닙니다. 프로그램을 종료합니다.");
			System.exit(0);
		}
	}

	// 업무 메뉴로 이동
	private static void returnMenu(Connection conn) throws SQLException {
		Scanner scanner = new Scanner(System.in);
		try {
			int choice;
			do {
				System.out.println("업무 메뉴로 돌아가시겠습니까? (1: 예 | 2: 아니오)");
				while (!scanner.hasNextInt()) {
					System.out.println("잘못된 입력입니다. 다시 입력하세요.");
					scanner.next(); // 잘못된 입력값을 버퍼에서 제거
				}
				choice = scanner.nextInt();
				if (choice == 1) {
					// 업무 메뉴로 돌아가기
					start(conn);
					return;
				} else if (choice == 2) {
					// 메인 메뉴로 돌아가기
					printMenu(conn);
					return;
				} else {
					System.out.println("잘못된 선택입니다. 다시 입력하세요.");
				}
			} while (choice != 1 && choice != 2); // 유효한 선택이 입력될 때까지 반복
		} catch (InputMismatchException e) {
			System.out.println("올바른 선택이 아닙니다. 프로그램을 종료합니다.");
			System.exit(0);
		}
	}
}