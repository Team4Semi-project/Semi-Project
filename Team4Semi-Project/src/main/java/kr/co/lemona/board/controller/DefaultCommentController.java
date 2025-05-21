package kr.co.lemona.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.lemona.board.model.dto.DefaultComment;
import kr.co.lemona.board.model.service.DefaultCommentService;

@RestController
@RequestMapping("defaultComments")
public class DefaultCommentController {
	
	@Autowired
	private DefaultCommentService service;

	/** 댓글 목록 조회
	 * @param boardNo
	 * @return
	 */
//	@GetMapping("")
//	public List<DefaultComment> select(@RequestParam("boardNo") int boardNo) {
//		// List<Comment> (Java의 자료형 List)
//		// HttpMessageConvert가
//		// List -> JSON(문자열) 로 변환해서 응답 -> JS
//		return service.select(boardNo);
//	}
	
}
