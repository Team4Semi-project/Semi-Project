package kr.co.lemona.board.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.lemona.board.model.service.RecipeBoardService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("board/1")
public class RecipeBoardController {
	
	@Autowired
	private RecipeBoardService service;
	
	@GetMapping("{categoryNo:[0-9]+}")
	public String selectRecipeBoardList(@PathVariable("categoryNo") int categoryNo,
								@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
								Model model,
								@RequestParam Map<String, Object> paramMap) {
		
		// 조회 서비스 호출 후 결과 반환 받기.
		log.debug("===================> categoryNo : " + categoryNo);
		Map<String, Object> map = null;
		
		// 조건에 따라 서비스 메서드 분기처리 하기 위해 map은 선언만 함!

		
		// 검색이 아닌 경우 --? paramMap 은 {}
		if(paramMap.get("key") == null) {
			// 게시글 목록 조회 서비스 호출
			map = service.selectRecipeBoardList(categoryNo, cp);
			
		} else {
			// 검색인 경우 --> paramMap 
			//paramMap.put("boardCode", boardCode);
			// --> paramMap = {"query"="짱구", "key"="tc", "boardCode"=1}
			
			// 검색 서비스 호출
			//map = service.serchList(paramMap, cp);
			
		}		
		
		
		
		
		// model 에 반환 받은 값 등록
		//model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		
		// forward : src/main/resources/templates/board/boardList.html
		return "board/boardList";
	}
	
}
