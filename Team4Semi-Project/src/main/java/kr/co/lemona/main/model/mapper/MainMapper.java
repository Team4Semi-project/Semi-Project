package kr.co.lemona.main.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;

@Mapper
public interface MainMapper {

	/** 삭제되지 않은 게시글 갯수 조회
	 * @return
	 */
	int getListCount();

	/** 검색조건에 맞으면서 삭제되지 않은 게시글 갯수 조회
	 * @param paramMap
	 * @return
	 */
	int getSearchCount(Map<String, Object> paramMap);

	int getRecipeBoardListCount(int categoryNo);

	List<RecipeBoard> selectRecipeBoardList(int categoryNo, RowBounds rowBounds);

	int getPopularListCount();

	List<RecipeBoard> selectPopularBoardList(RowBounds rowBounds);

	/** 게시판 통합 검색 조회 결과
	 * @param paramMap
	 * @param rowBounds
	 * @return
	 */
	List<Board> searchAllBoardList(Map<String, Object> paramMap, RowBounds rowBounds);

	

}
