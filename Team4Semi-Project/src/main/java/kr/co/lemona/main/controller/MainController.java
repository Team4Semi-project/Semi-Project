package kr.co.lemona.main.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.main.model.service.MainService;
import kr.co.lemona.member.model.dto.Member;
import kr.co.lemona.recipeBoard.model.dto.BoardStep;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {

	@Autowired
	private MainService service;

	/**
	 * 메인화면에 출력될 인기 게시글, 최근 레시피 게시글 4개 조회
	 * 인기 게시글 : 코지뷰 / 최근 레시피 게시글 : 아젠다뷰
	 * 
	 * @param categoryNo
	 * @param cp
	 * @param model
	 * @param paramMap
	 * @return
	 * @author jihyun
	 */
	@RequestMapping("/")
	public String mainPage(@RequestParam(value = "categoryNo", defaultValue = "0") int categoryNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			@RequestParam Map<String, Object> paramMap) {

		// 조회 서비스 호출 후 결과 반환 받기
		Map<String, Object> popularMap = null;
		Map<String, Object> map = null;

		// 검색이 아닌 전체 조회할 경우
		if (paramMap.get("key") == null) {
			
			// 최근 인기 게시글 목록 조회 서비스 호출
			popularMap = service.selectPopularBoardList(cp);

			// 최근 레시피 게시글 목록 조회 서비스 호출
			map = service.selectRecipeBoardList(categoryNo, cp);

		}

		// model에 반환 받은 값 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("popularBoardList", popularMap.get("popularBoardList"));
		model.addAttribute("recipeBoardList", map.get("recipeBoardList"));
		model.addAttribute("categoryNo", categoryNo);
		return "common/main";
	}

	/**
	 * 로그인이 되어있지 않을 때, 메인페이지로 리다이렉트
	 * 
	 * @param ra
	 * @return
	 * @author jaeho
	 */
	@GetMapping("loginError")
	public String loginError(RedirectAttributes ra) {
		ra.addFlashAttribute("message", "로그인 후 이용해주세요");

		return "redirect:/";
	}

	/** 전체 게시글 통합 검색
	 * @param categoryNo
	 * @param cp
	 * @param model
	 * @param paramMap
	 * @return
	 * @author jihyun
	 */
	@GetMapping("search")
	public String searchPage(@RequestParam(value = "categoryNo", defaultValue = "0") int categoryNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			@RequestParam Map<String, Object> paramMap,
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "sort", required = false, defaultValue = "latest") String sort) {

		// 조회 서비스 호출 후 결과 반환 받기
		Map<String, Object> map = null;

		// 검색인 경우 --> paramMap = {"key"="tc", "query"="맞긴해"}

		// 전체 게시글 통합 검색 서비스 호출
		map = service.AllsearchList(paramMap, cp, sort);

		// 검색어 강조 처리
		String query = (String) paramMap.get("querys");
		String searchKey = (String) paramMap.get("key");

		if (query != null && !query.trim().isEmpty()) {
			List<Board> boardList = (List<Board>) map.get("searchAllBoardList");

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

		// model에 반환 받은 값 등록
		model.addAttribute("searchAllBoardList", map.get("searchAllBoardList"));
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("categoryNo", categoryNo);
		model.addAttribute("key", key);
		model.addAttribute("sort", sort);

		return "common/search";
  }
	
	
//	/**
//	 * 레시피/인기 게시글 검색 시 상세 조회
//	 * 
//	 * @param boardNo     : 게시글 번호
//	 * @param category    : categoryNo 또는 "popular" 들어올 수 있음.
//	 * @param model
//	 * @param loginMember : 현재 로그인한 사용자가 있을 경우 사용
//	 * @param ra
//	 * @param req
//	 * @param resp
//	 * @return
//	 * @author jihyun
//	 */
//	@GetMapping("search/1/{category}/{boardNo:[0-9]+}")
//	public String recipeBoardDetail(@PathVariable("boardNo") int boardNo, @PathVariable("category") String category,
//			@RequestParam(value = "sort", required = false, defaultValue = "latest") String sort,
//			@RequestParam(value = "key", required = false) String key,
//			 @RequestParam Map<String, Object> paramMap,
//			Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember,
//			RedirectAttributes ra, HttpServletRequest req, // 요청에 담긴 쿠기 얻어오기
//			HttpServletResponse resp // 새로운 쿠기 만들어서 응답하기
//	) {
//
//		// 게시글 상세 조회 서비스 호출
//
//		// 1) Map 으로 전달할 파라미터 묶기
//		Map<String, Integer> map = new HashMap<>();
//		// caterogy에 인기글(popular) 이 들어올 경우 categoryNo를 0으로 하고
//		// 그렇지 않을 경우에는 int형으로 변환 후 categoryNo값 넘겨줌
//		if (!category.equals("popular")) {
//			int categoryNo = 0;
//			categoryNo = Integer.parseInt(category);
//			map.put("categoryNo", categoryNo);
//		} else {
//			map.put("popular", 1);
//			map.put("categoryNo", 0);
//		}
//		map.put("boardNo", boardNo);
//		
//		// 이전글/다음글을 위한 sort 전달
//		int sortNo = 0;
//		switch (sort) {
//		case "latest" : sortNo = 1; break;
//		case "oldest" : sortNo = 2; break;
//		case "popular" : sortNo = 3; break;
//		case "views" : sortNo = 4; break;
//		default:
//			sortNo = 1;
//		}
//		
//		map.put("sort", sortNo);
//
//		// 로그인 상태인 경우에만 memberNo 추가
//		if (loginMember != null) {
//			map.put("loginMember", loginMember.getMemberNo());
//		}
//
//		// 2) 서비스 호출
//		Map<String, Object> recipeMap = service.selectOneRecipe(map, paramMap);
//
//		String path = null;
//
//		// 조회 결과가 없는 경우
//		if (recipeMap == null) {
//			path = "redirect:/board/1/" + category; // 목록 재요청
//			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
//
//		} else {
//
//			// 조회 결과가 있는 경우
//
//			RecipeBoard recipeBoard = (RecipeBoard) recipeMap.get("recipeBoard");
//			List<BoardStep> boardStepList = (List<BoardStep>) recipeMap.get("boardStepList");
//			int prevBoardNo = (int) recipeMap.get("prevBoardNo");
//			int nextBoardNo = (int) recipeMap.get("nextBoardNo");
//
//			/*--------------------- 쿠키를 이용한 조회수 증가 시작 ------------------------*/
//
//			// 비회원 또는 로그인한 회원의 글이 아닌 경우( == 글쓴이를 뺀 다른사람)
//			if (loginMember == null || loginMember.getMemberNo() != recipeBoard.getMemberNo()) {
//
//				// 요청에 담겨있는 모든 쿠키 얻어오기
//				Cookie[] cookies = req.getCookies();
//
//				Cookie c = null;
//				if (cookies != null) {
//					for (Cookie temp : cookies) {
//						// 요청에 담긴 쿠키에 "readBoardNo"가 존재할 때
//						if (temp.getName().equals("readBoardNo")) {
//							// readBoardNo 존재
//							// -> 이 클라이언트가 어떤 게시글을 이미 읽은 이력이 있다
//							c = temp;
//							break;
//						}
//					}
//				}
//				int result = 0; // 조회수 증가 결과를 저장할 변수
//
//				// "readBoardNo" 가 쿠키에 없을 때
//				if (c == null) {
//					// 새 쿠키 생성("readBoardNo", [게시글번호])
//					c = new Cookie("readBoardNo", "[" + boardNo + "]");
//					result = service.updateReadCount(boardNo);
//
//				} else {
//					// "readBoardNo" 가 쿠키에 있을 때
//					// "readBoardNo" : [2][30][400]
//
//					// 현재 게시글을 처음 읽는 경우
//					if (c.getValue().indexOf("[" + boardNo + "]") == -1) {
//						// 해당 글 번호를 쿠키에 누적 + 서비스 호출
//						c.setValue(c.getValue() + "[" + boardNo + "]");
//						result = service.updateReadCount(boardNo);
//					}
//				}
//
//				// 조회 수 증가 성공 / 조회 성공 시
//				if (result > 0) {
//					// 먼저 조회된 board 의 readCount 값을
//					// result 값으로 다시 세팅
//					recipeBoard.setReadCount(result);
//
//					// 쿠기가 적용될 경로
//					c.setPath("/"); // "/" 이하 경로 요청시 쿠키를 서버로 전달
//
//					// 쿠키 수명 지정
//					// 현재 시간을 얻어오기
//					LocalDateTime now = LocalDateTime.now();
//
//					// 다음날 자정 지정
//					LocalDateTime nextDayMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
//
//					// 현재시간부터 다음 날 자정까지 남은 시간 계산(초단위)
//					long seconds = Duration.between(now, nextDayMidnight).getSeconds();
//
//					// 쿠키 수명 설정
//					c.setMaxAge((int) seconds);
//
//					resp.addCookie(c); // 응답 객체를 이용해서 클라이언트에게 쿠키 전달
//				}
//			}
//
//			/*--------------------- 쿠키를 이용한 조회수 증가 끝 ------------------------*/
//
//			if (recipeBoard != null && boardStepList != null) {
//				path = "board/searchRecipeDetail"; // recipeBoardDetail.html로 forward
//
//				// board - 게시글 일반 내용 + imageList + commentList
//				model.addAttribute("board", recipeBoard);
//				model.addAttribute("boardStepList", boardStepList);
//				model.addAttribute("prevBoardNo", prevBoardNo);
//				model.addAttribute("nextBoardNo", nextBoardNo);
//				model.addAttribute("sort", sort);
//				model.addAttribute("key", key);
//
//				// 조회된 이미지 목록이 있을 경우
//				/*
//				 * if( !recipeBoard.getImageList().isEmpty() ) {
//				 * 
//				 * BoardImg thumbnail = null;
//				 * 
//				 * // imgList의 0번 인덱스 == 가장 빠른 순서(imgOrder) // 만약 이미지 목록의 첫번째 행의 imgOrder가 0 ==
//				 * 썸네일인 경우 if(recipeBoard.getImageList().get(0).getImgOrder() == 0) {
//				 * 
//				 * thumbnail = recipeBoard.getImageList().get(0);
//				 * 
//				 * }
//				 * 
//				 * model.addAttribute("thumbnail", thumbnail); model.addAttribute("start",
//				 * thumbnail != null ? 1 : 0); // start : 썸네일이 있다면 1, 없다면 0을 저장 }
//				 */
//			}
//		}
//		return path;
//	}
	
	
	
}
