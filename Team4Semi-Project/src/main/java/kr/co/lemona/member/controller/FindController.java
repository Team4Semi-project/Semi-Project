package kr.co.lemona.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.lemona.member.model.dto.Member;
import kr.co.lemona.member.model.service.MemberService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("find")
@Slf4j
public class FindController {

	@Autowired
	private MemberService memberService; // MemberService에 아이디 찾기 메서드 추가해야 함

	@ResponseBody
	@PostMapping("findId")
	public Map<String, Object> findId(@RequestBody Map<String, String> paramMap) {

		String foundId = memberService.findIdByNameAndEmail(paramMap);

		Map<String, Object> result = new HashMap<>();
		result.put("foundId", foundId);

		return result;
	}

	// 비밀번호 찾기 페이지에서 비밀번호 찾기 완료
	@ResponseBody
	@PostMapping("findPw")
	public Map<String, Object> findPw(@RequestBody Member member, RedirectAttributes ra, Model model) {

		Map<String, Object> result = new HashMap<>();
		System.out.println();
		Map<String, String> foundUser = memberService.findUserByIdNameEmail(member);
		System.out.println(foundUser);
		if (foundUser != null) {
			ra.addFlashAttribute("message", "비밀번호 찾기 성공");
			result.put("sucess", true);
		} else {
			ra.addFlashAttribute("message", "비밀번호 찾기 실패");
			result.put("sucess", false);
		}
		model.addAttribute("url", "reset/findpw-update");
		model.addAttribute("memberId", member.getMemberId());
		return result;
	}

	@GetMapping("findpw-update")
	public String findPwUpdate(@RequestParam("memberId") String memberId, Model model) {
		model.addAttribute("memberId", memberId);
		return "reset/findpw-update";
	}

	// 비밀번호 재설정 
	@PostMapping("updatepassword")  //  
	public String changePw(@RequestParam Map<String, String> paramMap, @RequestParam("userId") String userId,
							RedirectAttributes ra) {
		// paramMap = {currentPw=asd123, newPw=pass02!, newPwConfirm=pass02!}
		log.info("명하 paramMap : " + paramMap.toString());
		log.info("userId 들어오는지 확인 : " + userId);
		String newPw = paramMap.get("newPw");
		// 로그인한 회원 번호
		
		
		String hashedPw = new BCryptPasswordEncoder().encode(newPw); // 스프링 시큐리티 사용할 경우
		int result = memberService.updatePassword(userId, hashedPw);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			// 변경 성공 시
			message = "비밀번호가 변경되었습니다!";
			path = "/login";
			
		} else {
			// 변경 실패 시
			message = "현재 비밀번호가 일치하지 않습니다";
			path = "/find/findpw-update";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}
/*	@PostMapping("/updatepassword")
	public String updatePassword(@RequestParam Map<String, String> params, RedirectAttributes ra, @RequestParam("userId") String memberId) {
	    
	    String memberPw = params.get("newPw");
	    log.info("memberId : " + memberId);
	    Map<String, Object> result = new HashMap<>();
	    
	    String message = null;
	    String path = null;

	    if ( (memberId == null || memberPw == null) || memberId.isEmpty() || memberPw.isEmpty()) {
	    	
	    	message = "잘못된 요청입니다.";
	    	path = "redirect:/findpw-update" ;
	    }
	   
	    // 비밀번호 암호화 처리 (선택)
	    String hashedPw = new BCryptPasswordEncoder().encode(memberPw); // 스프링 시큐리티 사용할 경우

	    int updated = memberService.updatePassword(memberId, hashedPw);
	    if (updated > 0) {
	        message = "비밀번호 변경이 완료되었습니다. 다시 로그인해주세요";
	        path = "redirect:/login";
	    } else {
	    	path = "redirect:/findpw-update";
	        message = "비밀번호 변경에 실패했습니다.";
	    }
	    ra.addFlashAttribute("message", message);
	    return path;
	    */
	    
	    
		/*
		 * String memberId = params.get("memberId"); String memberPw =
		 * params.get("newPw"); log.info("memberId : " + memberId); Map<String, Object>
		 * result = new HashMap<>();
		 * 
		 * if ( (memberId == null || memberPw == null) || memberId.isEmpty() ||
		 * memberPw.isEmpty()) { result.put("success", false); result.put("message",
		 * "잘못된 요청입니다."); return result; }
		 * 
		 * // 비밀번호 암호화 처리 (선택) String hashedPw = new
		 * BCryptPasswordEncoder().encode(memberPw); // 스프링 시큐리티 사용할 경우
		 * 
		 * int updated = memberService.updatePassword(memberId, hashedPw); if (updated >
		 * 0) { result.put("success", true); } else { result.put("success", false);
		 * result.put("message", "비밀번호 변경에 실패했습니다."); }
		 * log.info("test1111111111111111111111111 : " + result); return result;
		 */
	//}

}
