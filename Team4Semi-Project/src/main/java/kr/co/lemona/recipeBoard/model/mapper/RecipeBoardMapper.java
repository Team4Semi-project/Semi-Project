package kr.co.lemona.recipeBoard.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.recipeBoard.model.dto.BoardStep;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;
import kr.co.lemona.recipeBoard.model.dto.RecipeComment;

@Mapper
public interface RecipeBoardMapper {


	/** 해당 카테고리 내에서 삭제되지 않은 글 갯수
	 * @param categoryNo
	 * @return
	 * @author miae
	 */
	int getRecipeBoardListCount(int categoryNo);

	/** 특정 카테고리의 레시피 게시판 지정된 페이지 목록 조회
	 * @param categoryNo
	 * @param rowBounds
	 * @return
	 * @author miae
	 */
	List<RecipeBoard> selectRecipeBoardList(Map<String, Object> inputMap, RowBounds rowBounds);

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
	List<RecipeBoard> selectPopularBoardList(Map<String, Object> inputMap, RowBounds rowBounds);

	/** 레시피 게시글 상세 조회
	 * @param map
	 * @return
	 * @author miae
	 */
	RecipeBoard selectOneRecipe(Map<String, Integer> map);

	/** BOARD_STEP 에서 게시글 번호로 조회
	 * @return
	 * @author miae
	 * @param boardNo 
	 */
	List<BoardStep> selectBoardStepList(int boardNo);

	/** RECIPE_BOARD에 게시글 제목+카테고리번호+작성자 정보 삽입
	 * @param inputBoard
	 * @return
	 * @author 재호
	 */
	int insertRecipeBoard(RecipeBoard inputBoard);

	/** BOARD_STEP 삽입
	 * @param boardStepList
	 * @return
	 * @author 재호
	 */
	int insertBoardStepContent(@Param("list") List<BoardStep> boardStepList);

	/** 해시태그 중복검사 및 삽입
	 * @param hashTagList
	 * @return
	 * @author 재호
	 */
	int insertNewHashtagIfNotExists(@Param("list") List<String> hashTagList);
	
	/** 해시태그 삽입
	 * @param map
	 * @return
	 * @author 재호
	 */
	int insertHashTag(Map<String, Object> map);

	/** 이전 글 받아오기
	 * @param map
	 * @return
	 */
	RecipeBoard selectPrevBoard(Map<String, Integer> map);

	/** 다음 글 받아오기
	 * @param map
	 * @return
	 */
	RecipeBoard selectNextBoard(Map<String, Integer> map);

	/** 레시피 게시글 조회수 증가
	 * @param boardNo
	 * @return
	 * @author miae
	 */
	int updateReadCount(int boardNo);

	/** 현재 게시글 조회수 조회
	 * @param boardNo
	 * @return
	 * @author miae
	 */
	int selectReadCount(int boardNo);

	/** 검색 결과 게시글 갯수 조회
	 * @param paramMap
	 * @return
	 * @author jihyun
	 */
	int getSearchCount(Map<String, Object> paramMap);

	/** 검색 결과 게시글 목록 조회
	 * @param paramMap
	 * @param rowBounds
	 * @return
	 * @author jihyun
	 */
	List<RecipeBoard> selectSearchList(Map<String, Object> paramMap, RowBounds rowBounds);

	/** 검색 결과 인기 게시글 갯수 조회
	 * @param paramMap
	 * @return
	 * @author jihyun
	 */
	int getPopularSearchCount(Map<String, Object> paramMap);

	/** 검색 결과 인기 게시글 목록 조회
	 * @param paramMap
	 * @param rowBounds
	 * @author jihyun
	 * @return
	 */
	List<RecipeBoard> selectPopularSearchList(Map<String, Object> paramMap, RowBounds rowBounds);

	/** 좋아요 해제
	 * @param map
	 * @return
	 * @author 재호
	 */
	int decreaseLikeCount(Map<String, Integer> map);

	/** 좋아요 체크
	 * @param map
	 * @return
	 * @author 재호
	 */
	int increaseLikeCount(Map<String, Integer> map);

	/** 좋아요 갯수 갱신
	 * @param map
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

	/** 댓글 목록 조회
	 * @param commentMap
	 * @return
	 * @author miae
	 */
	List<RecipeComment> selectCommentList(Map<String, Object> commentMap);

	/** 레시피 게시글 수정/{제목,카테고리번호,수정일}
	 * @param inputBoard
	 * @return
	 * @author 재호
	 */
	int updateRecipeBoard(RecipeBoard inputBoard);

	/** 기존의 게시글 해시태그 삭제/신규 해시태그 추가는 기존 SQL 활용
	 * @param inputBoard
	 * @return
	 * @author 재호
	 */
	int deleteBoardHashtagByBoardNo(RecipeBoard inputBoard);

	/** 기존의 boardStep 정보 조회
	 * @param boardNo
	 * @return
	 * @author 재호
	 */
	List<Integer> selectOriginalList(int boardNo);

	/** 이미지가 삭제된 스텝 삭제
	 * @param deletedList
	 * @return
	 * @author 재호
	 */
	int deleteBoardStep(int deletedList);

	/** 스텝 순서 변경
	 * @param stepOrderList
	 * @return
	 * @author 재호
	 */
	int changeBoardStep(BoardStep stepOrderList);

	/** 썸내일이 아닌 이미지들 설정 초기화
	 * @param thumbnailOrder
	 * @author 재호
	 */
	void resetThumnail(Map<String, Integer> map);

}
