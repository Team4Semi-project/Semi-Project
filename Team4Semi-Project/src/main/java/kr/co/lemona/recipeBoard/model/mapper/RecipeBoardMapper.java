package kr.co.lemona.recipeBoard.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;

@Mapper
public interface RecipeBoardMapper {

	/** 지정된 카테고리 게시판의 레시피 게시글 수를 조회
	 * @param categoryNo
	 * @return
	 * @author 재호
	 */
	int getRecipeBoardListCount(int categoryNo);

	/** 지정된 카테고리 게시판의 페이지 목록 조회
	 * @param categoryNo
	 * @param rowBounds
	 * @return
	 * @author 재호
	 */
	List<RecipeBoard> selectRecipeBoardList(int categoryNo, RowBounds rowBounds);

}
