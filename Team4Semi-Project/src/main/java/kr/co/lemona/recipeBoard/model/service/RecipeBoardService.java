package kr.co.lemona.recipeBoard.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;

public interface RecipeBoardService {

	/** 레시피 게시판 목록 조회
	 * @param categoryNo : 카테고리 넘버
	 * @param cp : 현재 페이지
	 * @return
	 */
	Map<String, Object> selectRecipeBoardList(Map<String, Object> inputMap);

	/** 레시피 게시글 상세 조회
	 * @param map
	 * @return
	 * @author miae
	 * @param boardNo 
	 */
	Map<String, Object> selectOneRecipe(Map<String, Integer> map);

	/** 레시피 게시글 작성 서비스
	 * @param inputBoard
	 * @param images
	 * @param stepContent
	 * @param thumbnailNo
	 * @return
	 * @author 재호
	 */
	int insertRecipeBoard(RecipeBoard inputBoard,
						  List<MultipartFile> images,
						  List<String> inputStepContent,
						  int thumbnailNo) throws Exception;

	/** 레시피 게시글 조회수 증가
	 * @param boardNo
	 * @return
	 * @author miae
	 */
	int updateReadCount(int boardNo);

	/** 레시피 게시글 전용 검색 결과 조회
	 * @param paramMap
	 * @param cp
	 * @return
	 * @author jihyun
	 */
	Map<String, Object> searchList(Map<String, Object> paramMap, Map<String, Object> inputMap);

	/** 인기 게시글 전용 검색 결과 조회
	 * @param paramMap
	 * @param cp
	 * @return
	 * @author jihyunE
	 */
	Map<String, Object> searchPopularList(Map<String, Object> paramMap, int cp);

	/** 좋아요 기능
	 * @return
	 * @author 재호
	 */
	int updateLikeCount(Map<String, Integer> map);

	/** 인기글 ON
	 * @return
	 * @author 재호
	 */
	int updatePopularStateToY();
	
	/** 인기글 OFF
	 * @return
	 * @author 재호
	 */
	int updatePopularStateToN();

	/** 레시피 글 삭제
	 * @param boardNo
	 * @return
	 */
	int deleteRecipeBoard(int boardNo);
}
