package kr.co.lemona.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.service.DefaultBoardService;
import kr.co.lemona.board.model.service.EditDefaultBoardService;
import kr.co.lemona.common.util.Utility;
import kr.co.lemona.member.model.dto.Member;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("editBoard")
@PropertySource("classpath:/config.properties")
@Slf4j
public class EditDefaultBoardController {

	/* [이미지 저장을 위한 경로지정] */
	// 브라우저에서 접근 가능한 URL 경로
	@Value("${my.board.web-path}")
	private String webPath;

	// 서버 내부 실제 저장되는 경로
	@Value("${my.board.folder-path}")
	private String folderPath;

	// --------------------------------------------------------------

	@Autowired
	private EditDefaultBoardService service;
	
	@Autowired
	private DefaultBoardService boardService;

	/** 게시글 작성
	 * 
	 * @param inputBoard
	 * @param ra
	 * @return
	 * @throws Exception
	 */
	@PostMapping("{boardCode:2}/insert") // 파라미터 : loginMember
	public String defaultBoardInsert(@PathVariable("boardCode") int boardCode, 
			@ModelAttribute Board inputBoard, 
			// @SessionAttribute("loginMember") Member loginMember,
			HttpServletRequest req, // 세션 로그인 구현 후 필요 없음
			Model model, RedirectAttributes ra)
			throws Exception {

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
		
		inputBoard.setBoardCode(boardCode);
		inputBoard.setMemberNo(loginMember.getMemberNo());

		// insert하고 게시글 번호 저장 : 작성한 글의 Detail 페이지로 보내기 위함
		int boardNo = service.defaultBoardInsert(inputBoard);
		
		if (boardNo > 0) {
			ra.addFlashAttribute("message", "게시글이 작성되었습니다");
			return "redirect:" + "/board/" + boardCode + "/" + boardNo; // /board/1/2002
		} else {
			model.addAttribute("message", "게시글 작성 실패");
			model.addAttribute("board", inputBoard); // 입력값 그대로 보냄
			return "board/defaultBoardWrite"; // 기존 작성 페이지로 forward
		}
	}

	/** 게시글 수정 화면 전환
	 * 
	 * @param boarCode    : 게시판 종류 번호
	 * @param boardNo     : 게시글 번호
	 * @param loginMember : 현재 로그인한 회원 객체(로그인한 회원이 작성한 글이 맞는지 검사하는 용도)
	 * @param model
	 * @param ra
	 * @return
	 */
	@GetMapping("{boardCode:2}/{boardNo:[0-9]+}/update")
	public String boardUpdate(@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			@SessionAttribute("loginMember") Member loginMember, 
			Model model, RedirectAttributes ra) {

		// 수정 화면에 출력한 기존의 제목/내용/이미지 조회
		// -> 게시글 상세 조회
		// BoardService.selectOne의 매개변수
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);

		// BoardService.selectOne(map) 호출
		Board board = boardService.selectOne(map);

		String message = null;
		String path = null;

		if (board == null) {
			message = "해당 게시글이 존재하지 않습니다";
			path = "redirect:/"; // 메인페이지로 리다이렉트

			ra.addFlashAttribute("message", message);

		} else if (board.getMemberNo() != loginMember.getMemberNo()) {
			message = "자신이 작성한 글만 수정 가능합니다!";

			// 해당 글 상세조회 리다이렉트 (/board/1/2000)
			path = String.format("redirect:/board/%d/%d", boardCode, boardNo);

			ra.addFlashAttribute("message", message);

		} else {

			path = "board/defaultBoardWrite"; // templates/board/defaultBoardUpdate.html로 forward
			model.addAttribute("board", board);
		}

		return path;
	}
	
	/** 게시글 삭제
	 * @param boardCode
	 * @param boardNo
	 * @param cp
	 * @param ra
	 * @param loginMember
	 * @return
	 */
	@GetMapping("{boardCode:2}/{boardNo:[0-9]+}/delete")
	public String boardDelete(@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			RedirectAttributes ra,
			@SessionAttribute("loginMember") Member loginMember) {
			
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		map.put("memberNo", loginMember.getMemberNo());
		
		int result = service.boardDelete(map);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			
			message = "삭제 되었습니다";
			path = String.format("/board/%d?cp=%d", boardCode, cp);
			// /board/1?cp=2
					
		} else {
			message = "삭제 실패";
			path = String.format("/board/%d/%d?cp=%d", boardCode, boardNo, cp);
			// /board/1/2000?cp=2
		}

		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}

	/** 이미지 서버에 업로드
	 * 
	 * @param imageFile
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@PostMapping("defaultUploadImage")
	@ResponseBody
	public String uploadImage(@RequestParam("image") MultipartFile imageFile, HttpServletRequest request)
			throws IOException {

		File dir = new File(folderPath); // 파일 저장할 디렉토리 객체 생성
		if (!dir.exists())
			dir.mkdirs(); // 폴드 없으면 생성

		// 저장할 파일명을 UUID로 생성하여 중복 방지 + 원래 파일 이름을 붙임
		// Utility.fileRename(originalName);
		String fileName = Utility.fileRename(imageFile.getOriginalFilename());

		// 최종적으로 저장할 파일 경로 객체 생성
		File dest = new File(folderPath, fileName);

		// 업로드된 이미지 파일을 지정한 경로로 저장
		imageFile.transferTo(dest);

		// 클라이언트(브라우저)에 돌려줄 이미지 URL 경로 반환
		return request.getContextPath() + webPath + fileName;
	}
}
