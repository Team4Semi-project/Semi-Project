package kr.co.lemona.main.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.main.model.service.MainService;
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
			@RequestParam(value = "key", required = false) String key) {

		// 조회 서비스 호출 후 결과 반환 받기
		Map<String, Object> map = null;

		// 검색인 경우 --> paramMap = {"key"="tc", "query"="맞긴해"}

		// 전체 게시글 통합 검색 서비스 호출
		map = service.AllsearchList(paramMap, cp);

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
						board.setBoardTitle(title.replace(query, "<span style='background-color:yellow; font-weight:bold; color:red;'>" + query + "</span>"));
					}
				}

				// 작성자 강조
				if ("w".equals(searchKey)) {
					String nickname = board.getMemberNickname();
					if (nickname != null && nickname.contains(query)) {
						board.setMemberNickname(
								nickname.replace(query, "<span style='background-color:yellow; font-weight:bold; color:red;'>" + query + "</span>"));
					}
				}

			}

		}

		// model에 반환 받은 값 등록
		model.addAttribute("searchAllBoardList", map.get("searchAllBoardList"));
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("categoryNo", categoryNo);
		model.addAttribute("key", key);

		return "common/search";
	}

}
