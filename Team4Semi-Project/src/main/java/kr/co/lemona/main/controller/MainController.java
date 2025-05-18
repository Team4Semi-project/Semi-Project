package kr.co.lemona.main.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.lemona.main.model.service.MainService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {
	
	/** 메인페이지 매핑 메서드
	 * @return
	 */
	@RequestMapping("/")
	public String mainPage(Model model, @RequestParam Map<String, Object> paramMap) {

//		// 조회 서비스 호출 후 결과 반환 받기.
//		Map<String, Object> map = null;
//
//		if (paramMap.get("key") == null) { // 검색이 아닌 경우
//
//			// 게시글 목록 조회 서비스 호출
//			map = service.mainRecipeBoardList();
//
//		} else { // 검색인 경우 --> paramMap
//
//			// 검색 서비스 호출
//			// map = service.serchList(paramMap, cp);
//
//		}
//
//		// model 에 반환 받은 값 등록
//		model.addAttribute("boardList", map.get("recipeBoardList"));

		return "common/main";
  }

	public String mainPage() {
		
		return "common/main";
	}
	
	/** 로그인이 되어있지 않을 때, 메인페이지로 리다이렉트하는 메서드
	 * @param ra
	 * @return
	 */
	@GetMapping("loginError")
	public String loginError(RedirectAttributes ra) {
		ra.addFlashAttribute("message","로그인 후 이용해주세요");
		
		return "redirect:/";
	}

	@Autowired
	private MainService service;
	
  
//	@GetMapping("/")
//	public String selectRecipeBoardList(Model model,
//								@RequestParam Map<String, Object> paramMap) {
//		
//		// 조회 서비스 호출 후 결과 반환 받기.
//		Map<String, Object> map = null;
//		
//		if(paramMap.get("key") == null) { // 검색이 아닌 경우
//			
//			// 게시글 목록 조회 서비스 호출
//			map = service.mainRecipeBoardList();
//			
//		} else { // 검색인 경우 --> paramMap
//			 			
//			// 검색 서비스 호출
//			// map = service.serchList(paramMap, cp);
//			
//		}
//		
//		// model 에 반환 받은 값 등록
//		model.addAttribute("boardList", map.get("recipeBoardList"));
//
//		return "common/main";
//	}

}
