package com.gn.mvc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedularConfig {
	
	@Value("${scheduler.enable}")
	private boolean isSchedulerEnabled;
	
	// 1. fixedRate(이전 작업 시작 시점)
//	@Scheduled(fixedRate=5000)
//	public void runTask1() {
//		System.out.println("5초마다 실행!! : "+System.currentTimeMillis());
//	}
	
	// 2. fixedDelay(이전 작업 종료 시점)
//	@Scheduled(fixedDelay=3000)
//	public void runTask2() {
//		System.out.println("3초마다 실행!! : "+System.currentTimeMillis());
//	}
	
	// 3. initialDelay
//	@Scheduled(fixedRate=5000, initialDelay=1000*60*60)
//	public void runTask3() {
//		System.out.println("프로그램 시작 후 1시간 지나면 5초마다 실행 : "+System.currentTimeMillis());
//	}
	
	// 4. cron식
	@Scheduled(cron="0 50 15 * * *")
	public void runTask4() {
		if(!isSchedulerEnabled) {
			return;
		}
		
		System.out.println("매일 3시 50분에 실행을 하고 싶습니다.");
	}
	
}
