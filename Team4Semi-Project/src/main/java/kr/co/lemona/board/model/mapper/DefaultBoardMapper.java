package kr.co.lemona.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kr.co.lemona.board.model.dto.Board;

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
}
