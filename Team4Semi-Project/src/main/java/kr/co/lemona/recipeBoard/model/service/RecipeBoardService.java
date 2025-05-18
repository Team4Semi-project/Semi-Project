package kr.co.lemona.recipeBoard.model.service;

import java.util.Map;

import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;

public interface RecipeBoardService {

	/** 레시피 게시판 목록 조회
	 * @param categoryNo : 카테고리 넘버
	 * @param cp : 현재 페이지
	 * @return
	 */
	Map<String, Object> selectRecipeBoardList(int categoryNo, int cp);

	/** 인기 게시판 목록 조회 서비스
	 * @param cp : 현재 페이지
	 * @return
	 * @author 재호
	 */
	Map<String, Object> selectPopularBoardList(int cp);

	/** 레시피 게시글 상세 조회
	 * @param map
	 * @return
	 * @author miae
	 * @param boardNo 
	 */
	Map<String, Object> selectOneRecipe(Map<String, Integer> map);

}
