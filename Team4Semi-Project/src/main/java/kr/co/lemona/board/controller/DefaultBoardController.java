package kr.co.lemona.board.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
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

	/**
	 * 게시글 리스트 조회
	 * 
	 * @param boardCode
	 * @param cp
	 * @param paramMap
	 * @param model
	 * @return
	 * @author 민장, 지현(검색기능)
	 */
	@GetMapping("")
	public String selectBoardList(@PathVariable("boardCode") int boardCode,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@RequestParam Map<String, Object> paramMap, Model model, HttpServletRequest req,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember,
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "sort", required = false, defaultValue = "latest") String sort) {
		
		// 조회 대상의 정보를 담는 맵 객체
		Map<String, Object> inputMap = new HashMap<>();

		// 로그인 중이라면 로그인 계정 정보 삽입
		if (loginMember != null) {
			inputMap.put("memberNo", loginMember.getMemberNo());
		}
		
		inputMap.put("boardCode", boardCode);
		inputMap.put("cp", cp);
		inputMap.put("sort", sort); // 정렬

		// 조회 서비스 호출 후 결과 반환 받기.
		Map<String, Object> map = null;

		if (paramMap.get("key") == null) { // 검색이 아닌 경우

			// 게시글 목록 조회 서비스 호출
			map = service.selectBoardList(inputMap);

		} else { // 검색인 경우 --> paramMap

			// boardCode를 paramMap에 추가
			paramMap.put("boardCode", boardCode);
			paramMap.put("sort", sort); // 정렬

			// 검색 서비스 호출
			map = service.serchList(paramMap, cp);

			// 검색어 강조 처리
			String query = (String) paramMap.get("queryb");
			String searchKey = (String) paramMap.get("key");

			if (query != null && !query.trim().isEmpty()) {
				List<Board> boardList = (List<Board>) map.get("boardList");

				for (Board board : boardList) {
					// 제목 강조
					if ("t".equals(searchKey) || "tc".equals(searchKey)) {
						String title = board.getBoardTitle();
						if (title != null && title.contains(query)) {
							board.setBoardTitle(title.replace(query,
									"<span style='background-color:yellow; font-weight:bold; color:red;'>" + query
											+ "</span>"));
						}
					}

					// 작성자 강조
					if ("w".equals(searchKey)) {
						String nickname = board.getMemberNickname();
						if (nickname != null && nickname.contains(query)) {
							board.setMemberNickname(nickname.replace(query,
									"<span style='background-color:yellow; font-weight:bold; color:red;'>" + query
											+ "</span>"));
						}
					}

				}

			}

		}
		// --------------- 세션 구현 안돼서 테스트용 데이터 삽입 ---------------
		loginMember = Member.builder().memberNo(2).memberId("user2").memberName("이순신").memberNickname("순신이")
				.memberEmail("user2@example.com").memberDelFl("N").build();
//		loginMember = Member.builder().memberNo(1).memberId("user1").memberName("홍길동").memberNickname("길동이")
//				.memberEmail("user1@example.com").memberDelFl("N").build();

		req.getSession().setAttribute("loginMember", loginMember);
		// ---------------------------------------------------------------------
		// model 에 반환 받은 값 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("key", key);
		model.addAttribute("sort", sort);

		return "board/boardList";
	}

	/**
	 * 상세 조회
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

		// 로그인 상태인 경우 memberNo 추가
		if (loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		} else {
			map.put("memberNo", 0);
		}

		// 2) 서비스 호출
		Map<String, Object> boardMap = service.selectOne(map);

		String path = null;

		// 조회 결과가 없는 경우
		if (boardMap == null) {
			path = "redirect:/board/" + boardCode; // 목록 재요청
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");

		} else {

			// 조회 결과가 있는 경우

			Board board = (Board) boardMap.get("board");
			int prevBoardNo = (int) boardMap.get("prevBoardNo");
			int nextBoardNo = (int) boardMap.get("nextBoardNo");
			/*--------------------- 쿠키를 이용한 조회수 증가 시작 ------------------------*/

			// 비회원 또는 로그인한 회원의 글이 아닌 경우( == 글쓴이를 뺀 다른사람)
			if (loginMember == null || loginMember.getMemberNo() != board.getMemberNo()) {

				// 요청에 담겨있는 모든 쿠키 얻어오기
				Cookie[] cookies = req.getCookies();

				Cookie c = null;
				if (cookies != null) {
					for (Cookie temp : cookies) {
						// 요청에 담긴 쿠키에 "readBoardNo"가 존재할 때
						if (temp.getName().equals("readBoardNo")) {
							// readBoardNo 존재
							// -> 이 클라이언트가 어떤 게시글을 이미 읽은 이력이 있다
							c = temp;
							break;
						}
					}
				}
				int result = 0; // 조회수 증가 결과를 저장할 변수

				// "readBoardNo" 가 쿠키에 없을 때
				if (c == null) {
					// 새 쿠키 생성("readBoardNo", [게시글번호])
					c = new Cookie("readBoardNo", "[" + boardNo + "]");
					result = service.updateReadCount(boardNo);

				} else {
					// "readBoardNo" 가 쿠키에 있을 때
					// "readBoardNo" : [2][30][400]

					// 현재 게시글을 처음 읽는 경우
					if (c.getValue().indexOf("[" + boardNo + "]") == -1) {
						// 해당 글 번호를 쿠키에 누적 + 서비스 호출
						c.setValue(c.getValue() + "[" + boardNo + "]");
						result = service.updateReadCount(boardNo);
					}
				}

				// 조회 수 증가 성공 / 조회 성공 시
				if (result > 0) {
					// 먼저 조회된 board 의 readCount 값을
					// result 값으로 다시 세팅
					board.setReadCount(result);

					// 쿠기가 적용될 경로
					c.setPath("/"); // "/" 이하 경로 요청시 쿠키를 서버로 전달

					// 쿠키 수명 지정
					// 현재 시간을 얻어오기
					LocalDateTime now = LocalDateTime.now();

					// 다음날 자정 지정
					LocalDateTime nextDayMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

					// 현재시간부터 다음 날 자정까지 남은 시간 계산(초단위)
					long seconds = Duration.between(now, nextDayMidnight).getSeconds();

					// 쿠키 수명 설정
					c.setMaxAge((int) seconds);

					resp.addCookie(c); // 응답 객체를 이용해서 클라이언트에게 쿠키 전달
				}
			}

			/*--------------------- 쿠키를 이용한 조회수 증가 끝 ------------------------*/

			if (board != null) {
				path = "board/defaultBoardDetail"; // defaultBoardDetail.html로 forward

				// board - 게시글 일반 내용 + imageList + commentList
				model.addAttribute("board", board);
				model.addAttribute("prevBoardNo", prevBoardNo);
				model.addAttribute("nextBoardNo", nextBoardNo);

				// 조회된 이미지 목록이 있을 경우
				/*
				 * if( !recipeBoard.getImageList().isEmpty() ) {
				 * 
				 * BoardImg thumbnail = null;
				 * 
				 * // imgList의 0번 인덱스 == 가장 빠른 순서(imgOrder) // 만약 이미지 목록의 첫번째 행의 imgOrder가 0 ==
				 * 썸네일인 경우 if(recipeBoard.getImageList().get(0).getImgOrder() == 0) {
				 * 
				 * thumbnail = recipeBoard.getImageList().get(0);
				 * 
				 * }
				 * 
				 * model.addAttribute("thumbnail", thumbnail); model.addAttribute("start",
				 * thumbnail != null ? 1 : 0); // start : 썸네일이 있다면 1, 없다면 0을 저장 }
				 */
			}
		}
		return path;
	}

	/**
	 * 좋아요 기능
	 * 
	 * @return
	 * @author 재호
	 */
	@ResponseBody
	@PostMapping("like")
	public int updateLikeCount(@RequestBody Map<String, Integer> map) {

		// count = {-1:좋아요 적용 실패, 0:좋아요 X, 1:좋아요}
		int count = service.updateLikeCount(map);

		log.debug("count : {}", count);

		return count;
	}

}
