package kr.co.lemona.board.model.service;

import java.util.List;

import kr.co.lemona.board.model.dto.DefaultComment;

public interface DefaultCommentService {

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
	List<DefaultComment> select(int boardNo);

	/** 댓글/답글 등록 서비스
	 * @param comment
	 * @return
	 * @author 민장
	 */
	int insert(DefaultComment comment);

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
	int update(DefaultComment comment);

}
