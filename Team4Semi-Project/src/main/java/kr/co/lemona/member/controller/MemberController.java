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

	@Autowired // 의존성 주입
	private MemberService service; 

	@PostMapping("login") // Post 방식 로그인
	public String login(Member inputMember, RedirectAttributes ra, Model model,
			@RequestParam(value = "saveId", required = false) String saveId, HttpServletResponse resp) {


		// 서비스 계층에서 로그인 시도 (이메일로 사용자 조회 후 비밀번호 비교)
		Member loginMember = service.login(inputMember);

		if (loginMember == null) {
			// 로그인 실패 시: 리다이렉트 후 메시지 전달
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");

		} else {
			// 로그인 성공 시: 로그인한 회원 정보를 세션에 저장
			model.addAttribute("loginMember", loginMember);

		    // 쿠키 생성 (이메일 저장용)

			Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail());
			cookie.setPath("/"); // 전체 경로 

			if (saveId != null) { 
				cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
			} else {
				cookie.setMaxAge(0); // 즉시 삭제
			}

			resp.addCookie(cookie);
		}

		return "redirect:/"; // 메인 페이지로 리다이렉트
	}

	// 로그아웃 처리
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		status.setComplete();
		return "redirect:/"; // 메인 페이지로 리다이렉트
	}


	// 회원가입 페이지로 이동
	@GetMapping("register")
	public String registerPage() {
		return "register/register"; // templates/register/register.html을 가리킴
	}


	// 이메일 중복체크
	@ResponseBody
	@GetMapping("checkEmail")
	public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		return service.checkEmail(memberEmail);
	}

	// 닉네임 중복체크
	@ResponseBody
	@PostMapping("checkNickname")
	public int checkNickname(@RequestParam("memberNickname") String memberNickname) {
		return service.checkNickname(memberNickname);
	}

	// 아이디 중복체크
	@ResponseBody
	@PostMapping("checkId")
	public int checkId(@RequestParam("memberId") String memberId) {
		return service.checkId(memberId);
	}


	// 회원가입
	@PostMapping("register")
	@ResponseBody
	public String register(Member inputMember, RedirectAttributes ra) {

		// 회원가입 서비스 호출
		int result = service.register(inputMember);

		String path = null; // 리다이렉트 경로 저장
		String message = null; // 메시지 보여줌

		if (result > 0) { // 성공 시
			message = inputMember.getMemberNickname() + "님의 가입을 환영합니다!";
			path = "/"; // 메인 페이지로 이동

		} else { // 실패 시
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
