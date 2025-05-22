package kr.co.lemona.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.lemona.member.model.dto.Member;
import kr.co.lemona.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@SessionAttributes({ "loginMember" })
@Controller
@RequestMapping("member")
@Slf4j
public class MemberController {

	@Autowired
	private MemberService service;

	@PostMapping("login")
	public String login(Member inputMember, RedirectAttributes ra, Model model,
			@RequestParam(value = "saveId", required = false) String saveId, HttpServletResponse resp) {

		Member loginMember = service.login(inputMember);

		if (loginMember == null) {
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");

		} else {
			model.addAttribute("loginMember", loginMember);

			Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail());
			cookie.setPath("/");

			if (saveId != null) {
				cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
			} else {
				cookie.setMaxAge(0); // 즉시 삭제
			}

			resp.addCookie(cookie);
		}

		return "redirect:/";
	}

	@GetMapping("logout")
	public String logout(SessionStatus status) {
		status.setComplete();
		return "redirect:/";
	}

	@GetMapping("register")
	public String signupPage() {
		return "register/register"; // templates/register/register.html을 가리킴
	}

	@ResponseBody
	@GetMapping("checkEmail")
	public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		return service.checkEmail(memberEmail);
	}

	@ResponseBody
	@PostMapping("checkNickname")
	public int checkNickname(@RequestParam("memberNickname") String memberNickname) {
		return service.checkNickname(memberNickname);
	}

	@ResponseBody
	@PostMapping("checkId")
	public int checkId(@RequestParam("memberId") String memberId) {
		return service.checkId(memberId);
	}

	@PostMapping("register")
	@ResponseBody
	public String register(Member inputMember,
						 RedirectAttributes ra) {

		// log.debug("inputMember: " + inputMember);
		// log.debug("memberAddress: " + memberAddress.toString());

		// 회원가입 서비스 호출
		int result = service.register(inputMember);

		String path = null;
		String message = null;

		if (result > 0) { // 성공 시
			message = inputMember.getMemberNickname() + "님의 가입을 환영합니다!";
			path = "/";

		} else { // 실패
			message = "회원 가입 실패..";
			path = "register";
		}

		ra.addFlashAttribute("message", message);

		return path;
		// 성공 -> redirect:/
		// 실패 -> redirect:register (상대경로)
		// 현재 주소 /register (GET 방식 요청)
	}

}
