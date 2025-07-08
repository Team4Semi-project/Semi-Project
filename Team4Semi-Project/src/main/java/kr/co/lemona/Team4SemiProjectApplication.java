package kr.co.lemona;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) // Spring Security 의 자동 설정 중 로그인 페이지 이용 안함
public class Team4SemiProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(Team4SemiProjectApplication.class, args);
	}

}