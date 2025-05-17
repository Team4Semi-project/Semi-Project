package kr.co.lemona.recipeBoard.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;

@Mapper
public interface RecipeBoardMapper {


	/** 해당 카테고리 내에서 삭제되지 않은 글 갯수
	 * @param categoryNo
	 * @return
	 * @author miae
	 */
	int getRecipeBoardListCount(int categoryNo);

	/** 
	 * @param categoryNo
	 * @param rowBounds
	 * @return
	 * @author miae
	 */
	List<RecipeBoard> selectRecipeBoardList(int categoryNo, RowBounds rowBounds);

	/** 레시피 보드에서 삭제되지 않은 인기 게시글 수를 조회
	 * @return
	 * @author 재호
	 */
	int getPopularListCount();

	/** 인기 게시판 중 지정된 페이지 목록 조회
	 * @param rowBounds
	 * @return
	 * @author 재호
	 */
	List<RecipeBoard> selectPopularBoardList(RowBounds rowBounds);
}
