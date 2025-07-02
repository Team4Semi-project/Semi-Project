package kr.co.lemona.member.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.co.lemona.member.model.service.MemberService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // Bean 등록
public class MemberDeleteScheduling {
	
	@Autowired
	private MemberService service;

	@Scheduled(cron = "0 0 0 1 * *")
	public void scheduling() {
		log.info("memeber delete scheduling");
		
		int result = service.deleteMembers();
		
		log.info("{} members deleted",result);
	}

}
