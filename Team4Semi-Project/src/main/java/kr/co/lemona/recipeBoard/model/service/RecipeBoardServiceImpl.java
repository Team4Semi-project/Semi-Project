package kr.co.lemona.recipeBoard.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.lemona.board.model.dto.Pagination;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;
import kr.co.lemona.recipeBoard.model.mapper.RecipeBoardMapper;

@Service
public class RecipeBoardServiceImpl implements RecipeBoardService{
	
	@Autowired
	private RecipeBoardMapper mapper;
	
	
	/** 레시피 게시판/카테고리 별 페이지 요청 서비스
	 * @author 재호
	 */
	@Override
	public Map<String, Object> selectRecipeBoardList(int categoryNo, int cp) {
	
		// 지정된 카테고리 게시판의 레시피 게시글 수를 조회
		int listCount = mapper.getRecipeBoardListCount(categoryNo);
		
		// 게시글 수를 이용해서 Pagination 생성
		Pagination pagination = new Pagination(cp, listCount);
		
		// 지정된 카테고리 게시판의 페이지 목록 조회
		int limit = pagination.getLimit(); // 한 페이지에 보여줄 게시글 개수
		int offset = (cp-1)*limit; 		   // 건너뛸 게시글 수
		RowBounds rowBounds = new RowBounds(offset,limit);
		
		List<RecipeBoard> recipeBoardList = mapper.selectRecipeBoardList(categoryNo, rowBounds);
		
		// 조회 결과 + 페이지내이션 객체 전달
		Map<String,Object> map = new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("recipeBoardList", recipeBoardList);
		
		return map;
	}
}
