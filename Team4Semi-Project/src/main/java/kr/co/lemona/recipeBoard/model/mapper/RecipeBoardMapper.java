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

}
