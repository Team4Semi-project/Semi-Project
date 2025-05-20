package kr.co.lemona.main.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;

@Mapper
public interface MainMapper {

	/** 메인페이지 레시피 최신 게시글 조회
	 * @return
	 */
	List<RecipeBoard> mainRecipeBoardList(RowBounds rowBounds);

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

	

}
