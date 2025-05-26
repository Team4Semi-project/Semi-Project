package kr.co.lemona.main.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.recipeBoard.model.dto.BoardStep;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;

@Mapper
public interface MainMapper {

	/** 메인화면에 출력될 최근 4개 레시피 게시글 조회
	 * @param categoryNo
	 * @return
	 * @author jihyun
	 */
	List<RecipeBoard> selectRecipeBoardList(int categoryNo);

	int getPopularListCount();

	/** 메인화면에 출력될 최근 4개 인기 게시글 조회
	 * @return
	 * @author jihyun
	 */
	List<RecipeBoard> selectPopularBoardList();

	/** 검색 조건에 맞으면서 삭제되지 않은 게시글 갯수 조회
	 * @param paramMap
	 * @return
	 * @author jihyun
	 */
	int getSearchCount(Map<String, Object> paramMap);
	
	/** 전체 게시판 통합 검색 조회 결과
	 * @param paramMap
	 * @param rowBounds
	 * @return
	 * @author jihyun
	 */
	List<Board> searchAllBoardList(Map<String, Object> paramMap, RowBounds rowBounds);

	

}
