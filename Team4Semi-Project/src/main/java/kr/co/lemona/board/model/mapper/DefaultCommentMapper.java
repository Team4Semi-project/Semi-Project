package kr.co.lemona.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.lemona.board.model.dto.DefaultComment;

@Mapper
public interface DefaultCommentMapper {
	
	/** 댓글 수 조회
	 * @param boardNo
	 * @return
	 * @author 민장
	 */
	int count(int boardNo);
	
	/** 댓글 목록 조회
	 * @param boardNo
	 * @return
	 * @author 민장
	 */
	List<DefaultComment> select(int boardNo);

	/** 댓글/답글 등록
	 * @param comment
	 * @return
	 * @author 민장
	 */
	int insert(DefaultComment comment);

	/** 댓글/답글 삭제
	 * @param commentNo
	 * @return
	 * @author 민장
	 */
	int delete(int commentNo);

	/** 댓글 수정
	 * @param comment
	 * @return
	 * @author 민장
	 */
	int update(DefaultComment comment);
}
