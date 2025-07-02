package kr.co.lemona.recipeBoard.controller;

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

import kr.co.lemona.member.model.dto.Member;
import kr.co.lemona.recipeBoard.model.dto.RecipeComment;
import kr.co.lemona.recipeBoard.model.service.RecipeCommentService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("recipeComments")
@Slf4j
public class RecipeCommentController {
	
	@Autowired
	private RecipeCommentService service;

	/** 댓글 목록, 수 조회
	 * @param boardNo
	 * @return
	 * @author miae
	 */
	@GetMapping("")
	public Map<String, Object> select(@RequestParam("boardNo") int boardNo,
			 @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
		
		int memberNo = loginMember != null ? loginMember.getMemberNo() : 0;
		
	    List<RecipeComment> commentList = service.select(boardNo, memberNo);
	    int commentCount = service.count(boardNo);
	    
	    Map<String, Object> map = new HashMap<>();
	    map.put("commentList", commentList);
	    map.put("commentCount", commentCount);

	    return map;
	}
	
	/** 댓글/답글 등록
	 * @param comment
	 * @return
	 * @author miae
	 */
	@PostMapping("")
	public int insert(@RequestBody RecipeComment comment) {
		return service.insert(comment);
	}
	
	
	/** 댓글/답글 삭제
	 * @param commentNo
	 * @return
	 * @author miae
	 */
	@DeleteMapping("")
	public int delete(@RequestBody int commentNo) {
		return service.delete(commentNo);
	}
	
	/** 댓글 수정
	 * @param comment
	 * @return
	 * @author miae
	 */
	@PutMapping("")
	public int update(@RequestBody RecipeComment comment) {
		return service.update(comment);
	}
	
	/** 댓글 좋아요
	 * @param commentNo
	 * @param loginMember
	 * @return
	 * @author miae
	 */
	@PostMapping("like")
	public int like(@RequestBody Map<String, Object> map, 
			@SessionAttribute(value = "loginMember", required = false) Member loginMember) {
		
		int commentNo = Integer.parseInt(map.get("commentNo").toString());
	    int memberNo = loginMember.getMemberNo();
	    
		return service.like(commentNo, memberNo);
	}
}
