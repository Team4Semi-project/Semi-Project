package kr.co.lemona.recipeBoard.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.lemona.recipeBoard.model.dto.RecipeComment;

@Mapper
public interface RecipeCommentMapper {
	
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
	List<RecipeComment> select(int boardNo);

	/** 댓글/답글 등록
	 * @param comment
	 * @return
	 * @author 민장
	 */
	int insert(RecipeComment comment);

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
	int update(RecipeComment comment);

	/** 좋아요 여부 확인
	 * @param commentNo
	 * @param memberNo
	 * @return
	 * @author 민장
	 */
	int checkLike(Map<String, Object> map);

	/** 좋아요 추가
	 * @param commentNo
	 * @param memberNo
	 * @author 민장
	 */
	int insertLike(Map<String, Object> map);
	
	/** 좋아요 취소
	 * @param commentNo
	 * @param memberNo
	 * @author 민장
	 * @return 
	 */
	int deleteLike(Map<String, Object> map);

	/** 좋아요 개수 조회
	 * @param commentNo
	 * @return
	 */
	int countLike(int commentNo);
}
