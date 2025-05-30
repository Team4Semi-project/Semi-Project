package kr.co.lemona.myPage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.member.model.dto.Member;
import kr.co.lemona.myPage.model.service.MyPageService;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;
import lombok.extern.slf4j.Slf4j;

/*
 * @SessionAttributes 의 역할
 *  - Model에 추가된 속성 중 key값이 일치하는 속성을 session scope로 변경
 *  - SessionStatus 이용 시 session에 등록된 완료할 대상을 찾는 용도
 * 
 * @SessionAttribute 의 역할 (매개변수에 쓰는 것)
 * - Session에 존재하는 값을 Key로 얻어오는 역할 
 * 
 * */

@SessionAttributes({ "loginMember" })
@Controller
@RequestMapping("mypage")
@Slf4j
public class MyPageController {

	@Autowired
	private MyPageService service;

	// 프로필 이미지 변경 화면 이동
	@GetMapping("profile") // /myPage/profile GET 요청 매핑
	public String profile() {
		return "myPage/myPage-profile";
	}

	// 비밀번호 변경 화면 이동
	@GetMapping("changePw") // /myPage/changePw GET 요청 매핑
	public String changePw() {
		return "myPage/myPage-changePw";
	}

	// 회원 탈퇴 화면 이동
	@GetMapping("secession") // /myPage/secession GET 요청 매핑
	public String secession() {
		return "myPage/myPage-secession";
	}


	@GetMapping("userProfile")
	public String selectMemberInfo(@SessionAttribute("loginMember") Member loginMember,
			@RequestParam(value = "type", required = false, defaultValue = "recipe") String type,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			Model model, RedirectAttributes ra) {
		
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();
		String path = null;
		String message = null;
		int memberNo = 0;

		
		if (loginMember == null) {
			message = "로그인 멤버 없으면 나중에 처리할게용.";
			path = "redirect:/";
		} else {
			
			memberNo = loginMember.getMemberNo();
		}
		
		inputMap.put("memberNo", memberNo);
		inputMap.put("cp", cp);
		
		
		if(type.equals("recipe")) {
			// 사용자 정보, 사용자가 쓴 레시피 게시글
			resultMap = service.selectMemberInfo(inputMap);
			
		} else {
			// 사용자 정보, 사용자가 쓴 게시글
			resultMap = service.selectMemberBoardList(inputMap);
		}
		
		
		if(resultMap == null) {
			// 결과가 null 일 경우 메시지 보내기
			message = "사용자 조회 에러발생. 관리자에게 문의해주세요.";
			path = "redirect:/";
		} else {
			
			// map 에서 값 꺼내오기
			Member member = (Member) resultMap.get("member");

			log.info("조회가 되긴해~~~~~~~~~~~~~~~~~~");
			model.addAttribute("member", member);
			
			if(type.equals("recipe")) {
				// 레시피 게시판 글 model에 담아서 보냄
				List<RecipeBoard> recipeBoardList = (List<RecipeBoard>) resultMap.get("recipeBoardList");
				model.addAttribute("boardList", recipeBoardList);
				model.addAttribute("pagination", resultMap.get("pagination"));
				
			} else {
				// 일반 게시판 글 model에 담아서 보냄
				List<Board> boardList = (List<Board>) resultMap.get("boardList");
				model.addAttribute("boardList", boardList);
				model.addAttribute("pagination", resultMap.get("pagination"));
				
			}
		}
		path = "mypage/myPage-userProfile";
		ra.addFlashAttribute("message", message);
		return path; 
	}
	
	/**
	 * 회원 정보 수정
	 * 
	 * @param inputMember   : 커맨드 객체(@ModelAttribute가 생략된 상태) 제출된 수정된 회원 닉네임, 전화번호,
	 *                      주소
	 * @param loginMember   : 로그인한 회원 정보 (회원 번호 사용할 예정)
	 * @param memberAddress : 주소만 따로 받은 String[] 구분자 ^^^ 변경 예정
	 * @param ra            :
	 * @return
	 */
	@PostMapping("info")
	public String updateInfo(Member inputMember, @SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra) {

		// inputMember에 로그인한 회원 번호 추가
		inputMember.setMemberNo(loginMember.getMemberNo());
		// inputMember : 회원 번호, 회원 닉네임, 전화번호, 주소

		// 회원 정보 수정 서비스 호출
		int result = service.updateInfo(inputMember);

		String message = null;

		if (result > 0) { // 회원 정보 수정 성공

			// loginMember 새로 세팅
			// 우리가 방금 바꾼 값으로 세팅

			// loginMember는 세션에 저장된 로그인한 회원 정보가
			// 저장된 객체를 참조하고있다!

			// -> loginMember를 수정하면
			// 세션에 저장된 로그인한 회원 정보가 수정된다
			// == 세션 데이터와 DB 데이터를 동기화

			loginMember.setMemberNickname(inputMember.getMemberNickname());

			message = "회원 정보 수정 성공!!";

		} else {

			message = "회원 정보 수정 실패..";
		}

		ra.addFlashAttribute("message", message);

		return "redirect:info";
	}



	

	@PostMapping("profile")
	public String profile(@RequestParam("profileImg") MultipartFile profileImg,
			@SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra) throws Exception {

		// 업로드된 파일 정보를 DB에 INSERT 후 결과 행의 갯수 반환 받을 예정
		int result = service.profile(profileImg, loginMember);

		String message = null;

		if (result > 0)
			message = "변경 성공!";
		else
			message = "변경 실패";

		ra.addFlashAttribute("message", message);

		return "redirect:profile";
	}
	
	// 마이페이지 에서 톱니바퀴 누르면 회원 정보 수정 페이지로 ㅇ
	@GetMapping("/editProfile")
    public String editProfile() {
        return "myPage/editProfile"; // templates/myPage/editProfile.html을 렌더링
    }

}
