package kr.co.lemona.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.service.DefaultBoardService;
import kr.co.lemona.member.model.dto.Member;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("board")
@Slf4j
public class DefaultBoardController {

	@Autowired
	private DefaultBoardService service;
	
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}")
	public String defaultBoardDetail(@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			Model model,
			// @SessionAttribute(value="loginMember", required = false) Member loginMember,
			RedirectAttributes ra,
			HttpServletRequest req,  // 요청에 담긴 쿠키 얻어오기
			HttpServletResponse resp // 새로운 쿠키 만들어서 응답하기
			) {
		
		// --------------- 세션 구현 안돼서 테스트용 데이터 삽입 ---------------
		Member loginMember = Member.builder()
				.memberNo(2)
				.memberId("user2")
	            .memberName("이순신")
	            .memberNickname("순신이")
	            .memberEmail("user2@example.com")
	            .memberDelFl("N")
	            .build();
		
		req.getSession().setAttribute("loginMember", loginMember);
		// ---------------------------------------------------------------------
		
		// 게시글 상세 조회 서비스 호출
		
		// 1) Map으로 전달할 파라미터 묶기
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		
		// 로그인 상태인 경우에만 memberNo 추가
		if(loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}
		
		// 2) 서비스 호출
		Board board = service.selectOne(map);
		
		return "";
	}
	
}
