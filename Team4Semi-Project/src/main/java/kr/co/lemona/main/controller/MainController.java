package kr.co.lemona.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
	
	/** 메인페이지 매핑 메서드
	 * @return
	 */
	@RequestMapping("/")
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

}
