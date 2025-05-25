package kr.co.lemona.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.dto.DefaultComment;

@Mapper
public interface DefaultBoardMapper {

	/** 게시글 리스트 조회
	 * @param boardCode
	 * @return
	 * @author 민장
	 */
	int getListCount(int boardCode);

	/** 게시글 조회
	 * @param boardCode
	 * @param rowBounds
	 * @return
	 * @author 민장
	 */
	List<Board> selectBoardList(int boardCode, RowBounds rowBounds);

	/** 게시글 상세 조회
	 * @param map
	 * @return
	 * @author 민장
	 */
	Board selectOne(Map<String, Integer> map);

	/** 해당 게시판 검색 결과 갯수 조회
	 * @param paramMap
	 * @return
	 * @author jihyun
	 */
	int getSearchCount(Map<String, Object> paramMap);

	/** 해당 게시판 검색 결과 목록 조회
	 * @param paramMap
	 * @param rowBounds
	 * @return
	 */
	List<Board> selectSearchList(Map<String, Object> paramMap, RowBounds rowBounds);

	/** 이전 글 받아오기
	 * @param map
	 * @return
	 */
	Board selectPrevBoard(Map<String, Integer> map);

	/** 다음 글 받아오기
	 * @param map
	 * @return
	 */
	Board selectNextBoard(Map<String, Integer> map);
	
	/** 레시피 게시글 조회수 증가
	 * @param boardNo
	 * @return
	 */
	int updateReadCount(int boardNo);
	
	/** 현재 게시글 조회수 조회
	 * @param boardNo
	 * @return
	 * @author miae
	 */
	int selectReadCount(int boardNo);

	/** 댓글 목록 + 좋아요 조회
	 * @param commentMap
	 * @return
	 * @author 민장
	 */
	List<DefaultComment> selectCommentList(Map<String, Object> commentMap);
}
