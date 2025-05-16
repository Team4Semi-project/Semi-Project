package kr.co.lemona.recipeBoard.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.lemona.recipeBoard.model.service.RecipeBoardService;
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
	
  
	/** 레시피 게시판 조회하여 리스트 가져오는 메서드
	 * @param categoryNo : 레시피 게시판 카테고리 번호, 전체 누를경우 0이 기본값
	 * @param cp : 현재 페이지
	 * @param model : 서버에서 나온 결과값 화면단으로 전달
	 * @param paramMap : 검색기능 개발 시 사용 예정
	 * @return
	 * @author miae
	 */
	@GetMapping("")
	public String selectRecipeBoardList(@RequestParam(value = "categoryNo", defaultValue = "0") int categoryNo,
								@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
								Model model,
								@RequestParam Map<String, Object> paramMap) {
		
		// 조회 서비스 호출 후 결과 반환 받기.
		Map<String, Object> map = null;
		
		if(paramMap.get("key") == null) { // 검색이 아닌 경우
			
			// 게시글 목록 조회 서비스 호출
			map = service.selectRecipeBoardList(categoryNo, cp);
			
		} else { // 검색인 경우 --> paramMap
			 			
			// 검색 서비스 호출, 추후 개발 예정
			// map = service.serchList(paramMap, cp);
			
		}		
		
		// model 에 반환 받은 값 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("recipeBoardList"));

		return "board/boardList";
	}
	
}
