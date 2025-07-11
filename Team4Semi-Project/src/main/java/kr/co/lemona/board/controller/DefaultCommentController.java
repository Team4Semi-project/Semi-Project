package kr.co.lemona.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import kr.co.lemona.board.model.dto.DefaultComment;
import kr.co.lemona.board.model.service.DefaultCommentService;
import kr.co.lemona.member.model.dto.Member;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("defaultComments")
@Slf4j
public class DefaultCommentController {
	
	@Autowired
	private DefaultCommentService service;

	/** 댓글 목록, 수 조회
	 * @param boardNo
	 * @return
	 * @author 민장
	 */
	@GetMapping("")
	public Map<String, Object> select(@RequestParam("boardNo") int boardNo,
			 @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
		
		int memberNo = loginMember != null ? loginMember.getMemberNo() : 0;
		
	    List<DefaultComment> commentList = service.select(boardNo, memberNo);
	    int commentCount = service.count(boardNo);

	    Map<String, Object> map = new HashMap<>();
	    map.put("commentList", commentList);
	    map.put("commentCount", commentCount);

	    return map;
	}
	
	/** 댓글/답글 등록
	 * @param comment
	 * @return
	 * @author 민장
	 */
	@PostMapping("")
	public int insert(@RequestBody DefaultComment comment) {
		return service.insert(comment);
	}
	
	
	/** 댓글/답글 삭제
	 * @param commentNo
	 * @return
	 * @author 민장
	 */
	@DeleteMapping("")
	public int delete(@RequestBody int commentNo) {
		return service.delete(commentNo);
	}
	
	/** 댓글 수정
	 * @param comment
	 * @return
	 * @author 민장
	 */
	@PutMapping("")
	public int update(@RequestBody DefaultComment comment) {
		return service.update(comment);
	}
	
	/** 댓글 좋아요
	 * @param commentNo
	 * @param loginMember
	 * @return
	 */
	@PostMapping("like")
	public int like(@RequestBody Map<String, Object> map, 
			@SessionAttribute(value = "loginMember", required = false) Member loginMember) {
		
		int commentNo = Integer.parseInt(map.get("commentNo").toString());
	    int memberNo = loginMember.getMemberNo();
	    
		return service.like(commentNo, memberNo);
	}
}
