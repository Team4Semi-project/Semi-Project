package kr.co.lemona.main.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;

@Mapper
public interface MainMapper {

	/** 메인페이지 레시피 최신 게시글 조회
	 * @return
	 */
	List<RecipeBoard> mainRecipeBoardList();

	

}
