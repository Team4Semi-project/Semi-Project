package kr.co.lemona.recipeBoard.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.co.lemona.recipeBoard.model.service.RecipeBoardService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // Bean 등록
public class PopularBoardManageScheduling {
	
	@Autowired
	private RecipeBoardService service;

	@Scheduled(cron = "30 * * * * *")
	public void scheduling() {
		log.info("now scheduling");
		
		int result1 = service.updatePopularStateToY();
		int result2 = service.updatePopularStateToY();
		
		log.info("{} popular board state updated",result1+result2);
	}
}
