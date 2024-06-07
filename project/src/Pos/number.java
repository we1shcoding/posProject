package Pos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

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
}

public class number extends Pos {
	protected static final int BASIC_BALANCE = 390500; // 기본 잔고
	protected static int balance = BASIC_BALANCE; // 현재 잔고
	protected static int revenue = 0; // 매출액
	protected static ArrayList<Purchase> purchases = new ArrayList<>();

	// 메뉴 표시
	private static void Menu() {
		System.out.println("--메뉴를 선택하세요--");
		System.out.println("[1] 재고 체크");
		System.out.println("[2] 현재 잔고 체크");
		System.out.println("[3] 매출액");
		System.out.println("[4] 유통기한 체크");
		System.out.println("[5] 업무 시작");
		System.out.println("[6] 재고, 잔고 초기화");
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

			System.out.println("재고와 잔고가 초기값으로 되돌려졌습니다.");
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
				// 가져온 데이터 출력
				System.out.println("제품ID: " + 제품id + ", 가격: " + 가격 + "원, 재고: " + 재고 + "개, 유통기한: " + 유통기한 + "시간");

			}

			// 여러 제품 구매 과정
			while (true) {
				System.out.print("구매할 제품의 ID를 입력하시오 :");
				String productId = scanner.next();

				if (productId.equals("소주") || productId.equals("담배")) {
					check5_3(conn);
				}

				System.out.print("구매할 제품의 수량을 입력하시오 :");
				int quantity = scanner.nextInt();

				System.out.print("카드 번호를 입력해주세요 (16자리) : ");
				String cardNumberInput;
				while (true) {
					cardNumberInput = scanner.next();
					if (cardNumberInput.length() == 16) {
						break; // 올바른 길이의 카드 번호가 입력될 때까지 반복
					} else {
						System.out.print("카드 번호를 16자리로 다시 입력해주세요 :");
					}
				}

				// 입력한 카드 번호에 하이픈 추가
				String formattedCardNumber = formatCardNumber(cardNumberInput);

				System.out.println("카드 번호 : " + formattedCardNumber);

				// 제품 가격과 재고 확인
				sql = "SELECT 가격, 재고 FROM items WHERE 제품id = '" + productId + "'";
				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					int price = rs.getInt("가격");
					int stock = rs.getInt("재고");

					if (quantity > stock) {
						System.out.println(productId + "(이)가 품절되어 구매할 수 없습니다. 다른 제품을 구매하시겠습니까? (1. 예 | 2. 아니오)");
						int answer = scanner.nextInt();
						if (answer == 1) {
							continue; // 다른 제품을 구매하기 위해 반복문을 계속 진행
						} else {
							returnMenu(conn);
							break; // 반복문 탈출
						}
					}

					int totalAmount = price * quantity;

					purchases.add(new Purchase(productId, quantity)); // 구매 내역에 추가

					// 초기 카드 잔액 설정
					int cardBalance = 10000; // 초기 잔액을 10,000원으로 설정

					System.out.println("카드 잔액 : " + cardBalance + "원");

					if (cardBalance < totalAmount) {
						System.out.println("카드 잔액이 부족합니다. 구매를 중단합니다.");
						returnMenu(conn);
						break; // 반복문 탈출
					}

					int change = cardBalance - totalAmount;
					balance += totalAmount; // 잔고에 추가
					revenue += totalAmount; // 매출액 증가

					// 재고 업데이트
					int newStock = stock - quantity;
					sql = "UPDATE items SET 재고 = " + newStock + " WHERE 제품id = '" + productId + "'";
					stmt.executeUpdate(sql);

					System.out.println("거래가 완료되었습니다.\n거스름돈 : " + change + "원");
					System.out.println("잔고 : " + balance + "원");
					System.out.println("매출액 : " + revenue + "원");
					System.out.println("남은 재고 : " + newStock + "개");

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
		formatted.append(cardNum.substring(8, 16)).append("");
		return formatted.toString();
	}

	// 제품 환불
	private static void check5_2(Connection conn) throws SQLException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("환불할 제품의 ID를 입력하시오 :");
		String productId = scanner.next();

		System.out.println("환불할 제품의 수량을 입력하시오 :");
		int quantity = scanner.nextInt();

		try {
			// SQL 쿼리 실행을 위한 Statement 객체 생성
			Statement stmt = conn.createStatement();

			// 해당 제품의 가격과 재고를 가져오기 위한 쿼리
			String sql = "SELECT 가격, 재고 FROM items WHERE 제품id = '" + productId + "'";
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) {
				int price = rs.getInt("가격");
				int stock = rs.getInt("재고");

				if (quantity > stock) {
					System.out.println("환불이 불가능합니다. 구입했던 개수보다 많은 수를 입력했습니다.");
					returnMenu(conn);
					return;
				}

				// 환불할 금액 계산
				int refundAmount = price * quantity;

				// 현재 잔고에 환불할 금액을 추가
				balance += refundAmount;
				System.out.println("환불 금액 : " + refundAmount + "원");
				System.out.println("현재 잔고 : " + balance + "원");

				// 데이터베이스에서 재고를 업데이트
				int newStock = stock + quantity; // 환불되는 제품 수량만큼 재고를 증가시킴
				String updateSql = "UPDATE items SET 재고 = " + newStock + " WHERE 제품id = '" + productId + "'";
				stmt.executeUpdate(updateSql);

				System.out.println("재고가 업데이트 되었습니다. 새로운 재고 : " + newStock + "개");

				// 메뉴로 돌아가기
				returnMenu(conn);
			} else {
				System.out.println("해당 제품이 존재하지 않습니다.");
			}

			// 연결된 자원 닫기
			rs.close();
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
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
		// 물품 입고 화면 로직 추가, 입고 입력 시 중복 확인 검사 필요
		// 물품이 입고되면 데이터에서 검색이 되어야 함
		// 물품이 판매 되거나 삭제되면 데이터에서 삭제되어야 함. 검색해서 없음을 확인
		// 이 부분은 프로젝트 요구사항에 맞게 작성하세요.
		Scanner scanner = new Scanner(System.in);

		try {
			// 제품ID 입력
			System.out.print("입고할 제품ID를 입력하시오 : ");
			String productId = scanner.next();

			// 입고 수량 입력
			System.out.print("입고할 수량을 입력하시오 : ");
			int quantity = scanner.nextInt();

			// SQL 쿼리 실행을 위한 Statement 객체 생성
			Statement stmt = conn.createStatement();

			// 해당 제품ID가 이미 테이블에 존재하는지 확인하는 쿼리
			String checkExistQuery = "SELECT * FROM items WHERE 제품id = '" + productId + "'";
			ResultSet existResult = stmt.executeQuery(checkExistQuery);

			if (existResult.next()) {
				// 제품이 이미 존재하는 경우, 수량을 업데이트
				int currentStock = existResult.getInt("재고");
				int updatedStock = currentStock + quantity;

				// 제품 수량 업데이트 쿼리
				String updateQuery = "UPDATE items SET 재고 = " + updatedStock + " WHERE 제품id = '" + productId + "'";
				stmt.executeUpdate(updateQuery);

				System.out.println(productId + " 제품의 재고가 " + updatedStock + "개로 업데이트되었습니다.");
				returnMenu(conn);
			} else {
				// 제품이 존재하지 않는 경우, 새로 추가
				String insertQuery = "INSERT INTO items (제품id, 재고) VALUES ('" + productId + "', " + quantity + ")";
				stmt.executeUpdate(insertQuery);

				System.out.println(productId + " 제품이 " + quantity + "개로 추가되었습니다.");
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