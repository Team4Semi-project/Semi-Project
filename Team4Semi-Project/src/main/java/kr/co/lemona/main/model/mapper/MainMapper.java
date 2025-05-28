package kr.co.lemona.main.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
	List<RecipeBoard> selectRecipeBoardList(Map<String, Integer> dataMap);

	int getPopularListCount();

	/** 메인화면에 출력될 최근 4개 인기 게시글 조회
	 * @return
	 * @author jihyun
	 */
	List<RecipeBoard> selectPopularBoardList(Integer memberNo);

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

	/** 레시피/인기 검색 시 상세조회 - 레시피 단계 조회
	 * @param integer
	 * @return
	 * @author jihyun
	 */
	List<BoardStep> selectBoardStepList(Integer integer);

	/** 레시피/인기 검색 시 상세조회 - 레시피 조회
	 * @param map
	 * @return
	 * @author jihyun
	 */
	RecipeBoard selectOneRecipe(@Param("map") Map<String, Integer> map, @Param("paramMap") Map<String, Object> paramMap);

	/** 레시피 인기 검색 시 상세조회 - 이전글
	 * @param map
	 * @return
	 * @author jihyun
	 */
	RecipeBoard selectPrevBoard(Map<String, Integer> map);

	/** 레시피 인기 검색 시 상세조회 - 다음글
	 * @param map
	 * @return
	 * @author jihyun
	 */
	RecipeBoard selectNextBoard(Map<String, Integer> map);

	/** 레시피/인기 검색 시 상세조회 - 조회수 증가
	 * @param boardNo
	 * @return
	 * @author jihyun
	 */
	int updateReadCount(int boardNo);

	/** 레시피/인기 검색 시 상세조회 - 조회수 증가
	 * @param boardNo
	 * @return
	 * @author jihyun
	 */
	int selectReadCount(int boardNo);

}
