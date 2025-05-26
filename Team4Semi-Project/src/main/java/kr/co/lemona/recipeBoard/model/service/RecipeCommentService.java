package kr.co.lemona.recipeBoard.model.service;

import java.util.List;

import kr.co.lemona.recipeBoard.model.dto.RecipeComment;

public interface RecipeCommentService {

	/** 댓글 수 조회
	 * @param boardNo
	 * @return
	 * @author 민장
	 */
	int count(int boardNo);
	
	/** 댓글목록 조회 서비스
	 * @param boardNo
	 * @return
	 * @author 민장
	 */
	List<RecipeComment> select(int boardNo, int memberNo);

	/** 댓글/답글 등록 서비스
	 * @param comment
	 * @return
	 * @author 민장
	 */
	int insert(RecipeComment comment);

	/** 댓글/답글 삭제 서비스
	 * @param commentNo
	 * @return
	 * @author 민장
	 */
	int delete(int commentNo);

	/** 댓글 수정 서비스
	 * @param comment
	 * @return
	 * @author 민장
	 */
	int update(RecipeComment comment);

	/** 댓글 좋아요 서비스
	 * @param commentNo
	 * @param memberNo
	 * @return
	 */
	int like(int commentNo, int memberNo);

}
