package kr.co.lemona.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.service.DefaultBoardService;
import kr.co.lemona.member.model.dto.Member;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("board/{boardCode:2|3}")
@Slf4j
public class DefaultBoardController {

	@Autowired
	private DefaultBoardService service;

	
	/**게시글 리스트 조회
	 * @param boardCode
	 * @param cp
	 * @param paramMap
	 * @param model
	 * @return
	 * @author 민장
	 */
	@GetMapping("")
	public String selectRecipeBoardList(@PathVariable("boardCode") int boardCode,
								@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
								@RequestParam Map<String, Object> paramMap,
								Model model) {
		
		// 조회 서비스 호출 후 결과 반환 받기.
		Map<String, Object> map = null;
		
		if(paramMap.get("key") == null) { // 검색이 아닌 경우
			
			// 게시글 목록 조회 서비스 호출
			map = service.selectBoardList(boardCode, cp);
			
		} else { // 검색인 경우 --> paramMap
			 		
			// boardCode를 paramMap에 추가
			paramMap.put("boardCode", boardCode);
			
			// 검색 서비스 호출
			// map = service.serchList(paramMap, cp);

		}		
		
		// model 에 반환 받은 값 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));

		return "board/boardList";
	}
	
	
	
	/** 상세 조회
	 * 
	 * @param boardCode
	 * @param boardNo
	 * @param model
	 * @param loginMember
	 * @param ra
	 * @param req
	 * @param resp
	 * @return
	 * @author 민장
	 */
	@GetMapping("{boardNo:[0-9]+}") // 자유, 공지(2,3) 요청만 받기
	public String defaultBoardDetail(@PathVariable("boardCode") int boardCode, @PathVariable("boardNo") int boardNo,
			Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember,
			RedirectAttributes ra, HttpServletRequest req, // 요청에 담긴 쿠키 얻어오기
			HttpServletResponse resp // 새로운 쿠키 만들어서 응답하기
	) {

		// 게시글 상세 조회 서비스 호출

		// 1) Map으로 전달할 파라미터 묶기
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);

		// 로그인 상태인 경우에만 memberNo 추가
		if (loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}

		// 2) 서비스 호출
		Board board = service.selectOne(map);

		model.addAttribute("board", board);

		return "board/defaultBoardDetail";
	}

}
