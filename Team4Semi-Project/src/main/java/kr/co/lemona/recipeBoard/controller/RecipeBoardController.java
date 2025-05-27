package kr.co.lemona.recipeBoard.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.lemona.member.model.dto.Member;
import kr.co.lemona.recipeBoard.model.dto.BoardStep;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;
import kr.co.lemona.recipeBoard.model.service.RecipeBoardService;
import lombok.extern.slf4j.Slf4j;

/**
 * 레시피 게시판 컨트롤러
 * 
 * @author 미애, 재호
 */
@Controller
@Slf4j
@RequestMapping("board/{boardCode:1}")
public class RecipeBoardController {

	@Autowired
	private RecipeBoardService service;

	/**
	 * 레시피 게시판 조회하여 리스트 가져오는 메서드
	 * 
	 * @param categoryNo : 레시피 게시판 카테고리 번호, 전체 누를경우 0이 기본값
	 * @param cp         : 현재 페이지
	 * @param model      : 서버에서 나온 결과값 화면단으로 전달
	 * @param paramMap   : 검색기능 개발 시 사용 예정
	 * @return
	 * @author miae, jihyun(search)
	 */
	@GetMapping("{categoryNo}")
	public String selectRecipeBoardList(@PathVariable("categoryNo") String categoryNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@RequestParam(value = "sort", required = false, defaultValue = "latest") String sort,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember, Model model,
			@RequestParam(value = "key", required = false) String key, @RequestParam Map<String, Object> paramMap) {

		// 조회 대상의 정보를 담는 맵 객체
		Map<String, Object> inputMap = new HashMap<>();

		// 로그인 중이라면 로그인 계정 정보 삽입
		if (loginMember != null) {
			inputMap.put("memberNo", loginMember.getMemberNo());
		}
		inputMap.put("cp", cp);
		inputMap.put("categoryNo", categoryNo);
		inputMap.put("sort", sort);	// 정렬

		// 조회 서비스 호출 후 결과 반환 받기.
		Map<String, Object> map = null;

		if (paramMap.get("key") == null) { // 검색이 아닌 경우

			// 게시글 목록 조회 서비스 호출
			map = service.selectRecipeBoardList(inputMap);

		} else { // 검색인 경우 --> paramMap

			// 검색 서비스 호출
			if (categoryNo.equals("popular")) {
				map = service.searchPopularList(paramMap, cp, sort);
				log.debug("popularmap : "+map);
				
				// 검색어 강조 처리
				String query = (String) paramMap.get("queryb");
				String searchKey = (String) paramMap.get("key");

				if (query != null && !query.trim().isEmpty()) {
					List<RecipeBoard> boardList = (List<RecipeBoard>) map.get("recipeBoardList");

					for (RecipeBoard board : boardList) {
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
				
				
			} else {
				map = service.searchList(paramMap, inputMap);
				log.debug("map : "+map);

				// 검색어 강조 처리
				String query = (String) paramMap.get("queryb");
				String searchKey = (String) paramMap.get("key");

				if (query != null && !query.trim().isEmpty()) {
					List<RecipeBoard> boardList = (List<RecipeBoard>) map.get("recipeBoardList");

					for (RecipeBoard board : boardList) {
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
		}

		// model 에 반환 받은 값 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("recipeBoardList"));
		model.addAttribute("categoryNo", categoryNo);
		model.addAttribute("key", key);
		model.addAttribute("sort", sort);

		return "board/recipeBoardList";
	}

	/**
	 * 레시피 게시글 상세 조회
	 * 
	 * @param boardNo     : 게시글 번호
	 * @param category    : categoryNo 또는 "popular" 들어올 수 있음.
	 * @param model
	 * @param loginMember : 현재 로그인한 사용자가 있을 경우 사용
	 * @param ra
	 * @param req
	 * @param resp
	 * @return
	 * @author miae
	 */
	@GetMapping("{category}/{boardNo:[0-9]+}")
	public String recipeBoardDetail(@PathVariable("boardNo") int boardNo, @PathVariable("category") String category,
			@RequestParam(value = "sort", required = false, defaultValue = "latest") String sort,
			Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember,
			RedirectAttributes ra, HttpServletRequest req, // 요청에 담긴 쿠기 얻어오기
			HttpServletResponse resp // 새로운 쿠기 만들어서 응답하기
	) {

		// 1) Map 으로 전달할 파라미터 묶기
		Map<String, Integer> map = new HashMap<>();

		// caterogy에 인기글(popular) 이 들어올 경우 categoryNo를 0으로 하고
		// 그렇지 않을 경우에는 int형으로 변환 후 categoryNo값 넘겨줌
		if (!category.equals("popular")) {
			int categoryNo = 0;
			categoryNo = Integer.parseInt(category);
			map.put("categoryNo", categoryNo);
		} else {
			map.put("popular", 1);
			map.put("categoryNo", 0);
		}
		map.put("boardNo", boardNo);
		
		// 이전글/다음글을 위한 sort 전달
		int sortNo = 0;
		switch (sort) {
		case "latest" : sortNo = 1; break;
		case "oldest" : sortNo = 2; break;
		case "popular" : sortNo = 3; break;
		case "views" : sortNo = 4; break;
		default:
			sortNo = 1;
		}
		
		map.put("sort", sortNo);

		// 로그인 상태인 경우에만 memberNo 추가
		if (loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}

		// 2) 서비스 호출
		Map<String, Object> recipeMap = service.selectOneRecipe(map);

		String path = null;

		// 조회 결과가 없는 경우
		if (recipeMap == null) {
			path = "redirect:/board/1/" + category; // 목록 재요청
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");

		} else {

			// 조회 결과가 있는 경우

			RecipeBoard recipeBoard = (RecipeBoard) recipeMap.get("recipeBoard");
			List<BoardStep> boardStepList = (List<BoardStep>) recipeMap.get("boardStepList");
			int prevBoardNo = (int) recipeMap.get("prevBoardNo");
			int nextBoardNo = (int) recipeMap.get("nextBoardNo");

			/*--------------------- 쿠키를 이용한 조회수 증가 시작 ------------------------*/

			// 비회원 또는 로그인한 회원의 글이 아닌 경우( == 글쓴이를 뺀 다른사람)
			if (loginMember == null || loginMember.getMemberNo() != recipeBoard.getMemberNo()) {

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
					recipeBoard.setReadCount(result);

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

			if (recipeBoard != null && boardStepList != null) {
				path = "board/recipeBoardDetail"; // recipeBoardDetail.html로 forward

				// board - 게시글 일반 내용 + imageList + commentList
				model.addAttribute("board", recipeBoard);
				model.addAttribute("boardStepList", boardStepList);
				model.addAttribute("prevBoardNo", prevBoardNo);
				model.addAttribute("nextBoardNo", nextBoardNo);
				model.addAttribute("loginMember", loginMember);

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
	 * 레시피 게시글 작성 화면 전환
	 * 
	 * @return
	 * @author 재호
	 */
	@GetMapping("insert")
	public String insertRecipeBoard() {
		return "board/recipeBoardWrite";
	}

	/**
	 * 레시피 게시글 작성
	 * 
	 * @param inputBoard
	 * @param loginMember
	 * @param images
	 * @param ra
	 * @return
	 * @author 재호
	 */
	@PostMapping("insert")
	@ResponseBody
	public String insertRecipeBoard(RecipeBoard inputBoard,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember,
			@RequestParam(value = "images", required = false) List<MultipartFile> images,
			@RequestParam("stepContents") List<String> inputStepContent,
			@RequestParam(value = "thumbnailNo", required = false) int thumbnailNo,
			@RequestParam(value = "hashTags", required = false) List<String> hashTagList, RedirectAttributes ra,
			@RequestParam("imageExists") List<Boolean> imageExists)
			throws Exception {

		// 1. 로그인한 회원번호 세팅
		inputBoard.setMemberNo(1);
		inputBoard.setHashTagList(hashTagList);
		// memberNo title categoryNo hashTagList
		
		// 빈 배열을 포함한 imageList
		List<MultipartFile> imageList = new ArrayList<>();
		if (images != null && imageExists != null) {
		    int index = 0;

		    for (int i = 0; i < imageExists.size(); i++) {
		        if (imageExists.get(i)) {
		            if (index < images.size()) {
		                imageList.add(images.get(index++));
		            } else {
		                imageList.add(null); // 오류 방지용 fallback
		            }
		        } else {
		            imageList.add(null);
		        }
		    }
		}

		// 2. 서비스 메서드 호출 후 결과 반환 받기
		int boardNo = service.insertRecipeBoard(inputBoard, imageList, inputStepContent, thumbnailNo);

		// 3. 서비스 결과에 따라 message, 리다이렉트 경로 지정
		String message = null;
		String path = null;

		if (boardNo > 0) {
			message = "레시피가 등록되었습니다.";
			path = "/board/1/0/" + boardNo;
		} else {
			message = "레시피 등록에 실패했습니다.";
			path = "/board/1/insert";
		}

		ra.addFlashAttribute("message", message);

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

	/**
	 * @param category    : 카테고리 확인(레시피 / 인기 레시피)
	 * @param boardNo     : 글 번호
	 * @param loginMember
	 * @param model
	 * @param ra
	 * @param req
	 * @param resp
	 * @return
	 * @author miae
	 */
	@GetMapping("{category}/{boardNo:[0-9]+}/delete")
	public String deleteRecipeBoard(@PathVariable("category") String category, @PathVariable("boardNo") int boardNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember, Model model,
			RedirectAttributes ra) {

		int result = service.deleteRecipeBoard(boardNo);

		String path = null;
		String message = null;

		if (result > 0) {
			path = "/board/1/" + category;
			message = "레시피가 삭제되었어요.";
		} else {
			path = "/board/1/" + category + boardNo + "?cp=" + cp;
			message = "삭제 실패! 관리자에게 문의하세요.";
		}

		ra.addFlashAttribute("message", message);

		return "redirect:" + path;
	}

	/** 업데이트 페이지 출력
	 * @param boardNo
	 * @param model
	 * @return
	 * @author 재호
	 */
	@GetMapping("{category}/{boardNo:[1-9]+}/update")
	public String updateRecipeBoard(@PathVariable("category") Object category, @PathVariable("boardNo" ) int boardNo,
			Model model,
			RedirectAttributes ra,
			@RequestParam(value="sort", required = false, defaultValue = "lastest") String sort) {

		// 1) Map 으로 전달할 파라미터 묶기
		Map<String, Integer> map = new HashMap<>();

		// caterogy에 인기글(popular) 이 들어올 경우 categoryNo를 0으로 하고
		// 그렇지 않을 경우에는 int형으로 변환 후 categoryNo값 넘겨줌
		if (!((String)category).equals("popular")) {
			int categoryNo = 0;
			categoryNo = Integer.parseInt((String)category);
			map.put("categoryNo", categoryNo);
		} else {
			map.put("popular", 1);
			map.put("categoryNo", 0);
		}
		map.put("boardNo", boardNo);

		// 이전글/다음글을 위한 sort 전달
		int sortNo = 0;
		switch (sort) {
		case "latest" : sortNo = 1; break;
		case "oldest" : sortNo = 2; break;
		case "popular" : sortNo = 3; break;
		case "views" : sortNo = 4; break;
		default:
			sortNo = 1;
		}
		
		map.put("sort", sortNo);
		
		// 2) 서비스 호출
		Map<String, Object> recipeMap = service.selectOneRecipe(map);

		// 조회 결과가 없는 경우
		if (recipeMap == null) {
			
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
			return String.format("redirect:/board/1/%s/%d", category, boardNo);

		} else {

			// 조회 결과가 있는 경우

			RecipeBoard recipeBoard = (RecipeBoard) recipeMap.get("recipeBoard");
			List<BoardStep> boardStepList = (List<BoardStep>) recipeMap.get("boardStepList");

			if (recipeBoard != null && boardStepList != null) {
				// board - 게시글 일반 내용 + imageList + commentList
				model.addAttribute("board", recipeBoard);
				model.addAttribute("boardStepList", boardStepList);
			}
		}
		return "board/recipeBoardUpdate";
	}
	
	@PostMapping("update")
	@ResponseBody
	public String updateRecipeBoard(RecipeBoard inputBoard,
	        @RequestParam(value = "images", required = false) List<MultipartFile> images,
	        @RequestParam("stepContents") List<String> inputStepContent,
	        @RequestParam(value = "thumbnailNo", required = false) Integer thumbnailNo,
	        @RequestParam(value = "hashTags", required = false) List<String> hashTagList,
	        RedirectAttributes ra,
	        @RequestParam("boardNo") int boardNo,
	        @RequestParam("categoryNo") String category,
	        @RequestParam("stepNoList") List<Integer> stepNoList) throws Exception {

	    inputBoard.setHashTagList(hashTagList);
	    inputBoard.setBoardNo(boardNo);
	    
	    log.debug("====stepNoList==== : {}",stepNoList);
	    log.debug("======images====== : {}",images);

	    int result = service.updateRecipeBoard(inputBoard, images, inputStepContent, thumbnailNo, stepNoList);

	    String message;
	    String path;

	    if (result > 0) {
	        message = "레시피가 수정되었습니다.";
	        path = String.format("/board/1/%s/%d", category, boardNo);
	    } else {
	        message = "레시피 수정에 실패했습니다.";
	        path = String.format("/board/1/%s/%d/update", category, boardNo);
	    }

	    ra.addFlashAttribute("message", message);
	    return path;
	}

}
