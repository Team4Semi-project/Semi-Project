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

/** 레시피 게시판 컨트롤러
 * @author 미애, 재호
 */
@Controller
@Slf4j
@RequestMapping("board/1")
public class RecipeBoardController {
	
	@Autowired
	private RecipeBoardService service;
	
	
	/** 레시피 게시판/카테고리 별 페이지 요청 메서드
	 * @param categoryNo 
	 * @param cp		 // 페이지내이션
	 * @param model		 // 리스트 전달 객체
	 * @param paramMap   // 검색어 + cp
	 * @return
	 * @author 재호
	 */
	@GetMapping("{categoryNo:[1-9]+}")
	public String recipeBoardCategory(@PathVariable("categoryNo") int categoryNo,
									  @RequestParam(value="cp", required=false, defaultValue="1") int cp,
									  Model model,
									  @RequestParam Map<String, Object> paramMap) {
		
		// 조회 서비스 호출후 결과 반환
		Map<String, Object> map = service.selectRecipeBoardList(categoryNo, cp);
		
		if(paramMap.get("key") == null) { // 검색이 아닐때
			
		} else { // 검색일 때
			
		}
		
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("recipeBoardList", map.get("recipeBoardList"));
		
		return "board/recipeBoardList";
	}
	
}