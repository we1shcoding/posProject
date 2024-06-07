package Pos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Pos {
	protected static final int MINUTE_MONEY = 9800; // 분당 알바비
	protected static int totalmoney = 0; // 총 알바비
	protected static final int BASIC_BALANCE = 390500; // 기본 잔고
	protected static int balance = BASIC_BALANCE; // 현재 잔고
	protected static Timer timer = new Timer();
	protected static String LoginTime; // 로그인 시간
	protected static LocalDateTime startTime; // 출근 시간

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("----------금일 알바생 로그인----------");
		System.out.print("사용자 이름을 입력하시오 : ");
		String userName = sc.nextLine();
		System.out.println("입력한 사용자 이름: " + userName);

		String userPin = "";
		while (true) {
			System.out.print("비밀번호 4자리를 입력하시오 : ");
			userPin = sc.nextLine();
			if (userPin.equals("1234")) {
				System.out.println("입력한 비밀번호 : " + userPin);

				System.out.println(userName + " 알바생이 출근하였습니다.");

				// 출근 시간 기록
				startTime = LocalDateTime.now();
				LoginTime = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            System.out.println("로그인 성공 시간 : " + LoginTime);

				// 기본 잔고 출력
				System.out.println("기본 잔고 : " + balance + "원");

				// 알바비 계산 시작
				startWageCalculation();

				break;
			} else {
				System.out.println("올바른 비밀번호가 아닙니다. 다시 입력해주세요.");
			}
		}

		// 프로그램 종료를 위한 입력 대기
		System.out.println("아르바이트를 시작합니다.");
		System.out.println("----------------------------------\n");
	}

	protected static void startWageCalculation() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				totalmoney += MINUTE_MONEY;
			}
		};
		timer.scheduleAtFixedRate(task, 0, 60000); // 1분마다 실행
	}

	public static void endProgram() {
		// 현재 시각 가져오기
		LocalDateTime endTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedEndTime = endTime.format(formatter);

		// 일한 시간 계산
		long elapsedTimeInMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
		long hours = elapsedTimeInMinutes / 60;
		long minutes = elapsedTimeInMinutes % 60;
		long seconds = ChronoUnit.SECONDS.between(startTime, endTime) % 60;

		// 총 매출액과 오늘 수당 계산
		int totalmoney = (int) (elapsedTimeInMinutes * MINUTE_MONEY);

		int revenue = 0; // 총 매출액을 계산하는 로직을 추가해야 합니다.

		// 결과 출력
		System.out.println("프로그램이 종료되었습니다.");
		System.out.println("총 매출액 : " + number.revenue + "원");
		System.out.println("오늘 수당 : " + totalmoney + "원");
		System.out.println("로그인 성공 시간 : " + LoginTime);
		System.out.println("일한 시간 : " + hours + "시간 " + minutes + "분 " + seconds + "초");
	}
}