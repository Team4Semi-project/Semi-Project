package kr.co.lemona.recipeBoard.model.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.lemona.board.model.dto.Pagination;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;
import kr.co.lemona.recipeBoard.model.mapper.RecipeBoardMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecipeBoardServiceImpl implements RecipeBoardService {

	@Autowired
	private RecipeBoardMapper mapper;

	// 레시피 게시판 목록 조회
	@Override
	public Map<String, Object> selectRecipeBoardList(int categoryNo, int cp) {

		// 1. 지정된 카테고리(categoryNo)에서 삭제 되지 않은 게시글 수를 조회
		int listCount = mapper.getRecipeBoardListCount(categoryNo);

		// 2. 1번의 결과 + cp 를 이용해서 Pagination 객체를 생성
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 특정 게시판의 지정된 페이지 목록 조회
		int limit = pagination.getLimit(); // 한 페이지 내에 보여줄 게시글 수
		int offset = (cp - 1) * limit;	   // 보여줄 페이지의 앞에 건너뛸 게시글 개수
		RowBounds rowBounds = new RowBounds(offset, limit);

		List<RecipeBoard> recipeBoardList = mapper.selectRecipeBoardList(categoryNo, rowBounds);

		// 4. 목록 조회 결과 + Pagination 객체를 Map 으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("recipeBoardList", recipeBoardList);

		// 5. 결과 반환
		return map;
	}
	
	/** 인기 게시판 조회 서비스
	 * @author 재호
	 */
	@Override
	public Map<String, Object> selectPopularBoardList(int cp) {

		// 1. 레시피 보드에서 삭제되지 않은 인기 게시글 수를 조회
		int listCount = mapper.getPopularListCount();

		// 2. 1번의 결과 + cp 를 이용해서 Pagination 객체를 생성
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 인기 게시판 중 지정된 페이지 목록 조회
		int limit = pagination.getLimit(); // 한 페이지 내에 보여줄 게시글 수
		int offset = (cp - 1) * limit;	   // 보여줄 페이지의 앞에 건너뛸 게시글 개수
		RowBounds rowBounds = new RowBounds(offset, limit);

		List<RecipeBoard> PopularBoardList = mapper.selectPopularBoardList(rowBounds);

		// 4. 목록 조회 결과 + Pagination 객체를 Map 으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("boardList", PopularBoardList);
		
//		log.debug("listCount 결과 : " + listCount);
//		log.debug("PopularBoardList 결과 : " + PopularBoardList);

		// 5. 결과 반환
		return map;
	}

}
