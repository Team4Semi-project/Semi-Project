package kr.co.lemona.main.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.dto.Pagination;
import kr.co.lemona.main.model.mapper.MainMapper;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MainServiceImpl implements MainService {

	@Autowired
	private MainMapper mapper;

	// 전체 게시판 통합 검색 서비스
	@Override
	public Map<String, Object> AllsearchList(Map<String, Object> paramMap, int cp) {
		// paramMap (key, query)

		// 1. 검색 조건에 맞으면서
		// 삭제되지 않은 게시글 수 조회

		int listCount = mapper.getSearchCount(paramMap);

		// 2. 1번의 결과 + cp를 이용해서
		// Pagination 객체 생성
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 특정 게시판의 지정된 페이지 목록 조회
		int limit = pagination.getLimit(); // 10개씩 조회
		int offset = (cp - 1) * limit; // cp : 현재 페이지
		RowBounds rowBounds = new RowBounds(offset, limit);

		// mapper 메서드 호출 코드 수행
		// -> Mapper 메서드 호출 시 전달할 수 있는 매개변수 1개
		// -> 2개를 전달할 수 있는 경우가 있음
		// RowBounds를 이용할 때
		// 1번째 : sql에 전달할 파라미터
		// 2번째 : RowBounds 객체
		
		// DTO 변경, 코드 수정 필요
		 List<Board> searchAllBoardList = mapper.searchAllBoardList(paramMap, rowBounds);

		// 4. 목록 조회 결과 + Paginaion 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("searchAllBoardList", searchAllBoardList);

		return map;

	}

	@Override
	public Map<String, Object> selectRecipeBoardList(int categoryNo, int cp) {
		// 1. 지정된 카테고리(categoryNo)에서 삭제 되지 않은 게시글 수를 조회
		int listCount = mapper.getRecipeBoardListCount(categoryNo);

		// 2. 1번의 결과 + cp 를 이용해서 Pagination 객체를 생성
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 특정 게시판의 지정된 페이지 목록 조회
		int limit = pagination.getLimit(); // 한 페이지 내에 보여줄 게시글 수
		int offset = (cp - 1) * limit; // 보여줄 페이지의 앞에 건너뛸 게시글 개수
		RowBounds rowBounds = new RowBounds(offset, limit);

		List<RecipeBoard> recipeBoardList = mapper.selectRecipeBoardList(categoryNo, rowBounds);

		// 4. 목록 조회 결과 + Pagination 객체를 Map 으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("recipeBoardList", recipeBoardList);

		// 5. 결과 반환
		return map;
	}

	@Override
	public Map<String, Object> selectPopularBoardList(int cp) {
		// 1. 레시피 보드에서 삭제되지 않은 인기 게시글 수를 조회
		int listCount = mapper.getPopularListCount();

		// 2. 1번의 결과 + cp 를 이용해서 Pagination 객체를 생성
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 인기 게시판 중 지정된 페이지 목록 조회
		int limit = pagination.getLimit(); // 한 페이지 내에 보여줄 게시글 수
		int offset = (cp - 1) * limit; // 보여줄 페이지의 앞에 건너뛸 게시글 개수
		RowBounds rowBounds = new RowBounds(offset, limit);

		List<RecipeBoard> popularBoardList = mapper.selectPopularBoardList(rowBounds);

		// 4. 목록 조회 결과 + Pagination 객체를 Map 으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("popularBoardList", popularBoardList);

		// 5. 결과 반환
		return map;
	}
}
