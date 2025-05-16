package kr.co.lemona.board.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.service.EditDefaultBoardService;
import kr.co.lemona.common.util.Utility;
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

	/**
	 * 게시글 작성
	 * 
	 * @param inputBoard
	 * @param ra
	 * @return
	 * @throws Exception
	 */
	@PostMapping("2/insert") // ("{boardCode:[0-9]+}/insert") / 파라미터 : boardCode, loginMember
	public String defaultBoardInsert(@ModelAttribute Board inputBoard, Model model, RedirectAttributes ra)
			throws Exception {

		// !! 파라미터 받아와서 세팅해야함(boardCode, loginMember.memberNo)
		// inputBoard에 게시글 종류(자유게시판), 회원번호 저장
		int boardCode = 2;
		inputBoard.setBoardCode(boardCode);
		inputBoard.setMemberNo(2);

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

	/**
	 * 이미지 서버에 업로드
	 * 
	 * @param imageFile
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@PostMapping("uploadImage")
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
