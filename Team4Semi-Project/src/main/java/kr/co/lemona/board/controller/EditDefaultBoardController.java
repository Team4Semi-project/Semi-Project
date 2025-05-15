package kr.co.lemona.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.service.EditDefaultBoardService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("editBoard")
@Slf4j
public class EditDefaultBoardController {
	
	
	private EditDefaultBoardService service;
	
	// ("{boardCode:[0-9]+}/insert")
	/** 게시글 작성
	 * @param inputBoard
	 * @return
	 */
	@PostMapping("2/insert") // 파라미터 : boardCode, loginMember
	public String defaultBoardInsert(@ModelAttribute Board inputBoard) {
		
		// !! 파라미터 받아와서 세팅해야함(boardCode, loginMember.memberNo)
		inputBoard.setBoardCode(2);
		inputBoard.setMemberNo(1);
		
		
		
		return "";
	}
}
