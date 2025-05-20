package kr.co.lemona.main.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.lemona.board.model.dto.Pagination;
import kr.co.lemona.main.model.mapper.MainMapper;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;

@Service
public class MainServiceImpl implements MainService {

	@Autowired
	private MainMapper mapper;

	@Override
	public Map<String, Object> mainRecipeBoardList(int cp) {

		// 1. 지정된 게시판(boardCode)에서
		// 삭제되지 않은 게시글 수 조회
		int listCount = mapper.getListCount();

		// 2. 1번의 결과 + cp를 이용해서
		// Pagination 객체 생성
		// * Pagination 객체 : 게시글 목록 구성에 필요한 값에 저장한 객체
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 특정 게시판의 지정된 페이지 목록 조회
		/*
		 * ROWBOUNDS 객체 (Mybatis 제공 객체) : 지정된 크기만큼 건너 뛰고(offset) 제한된 크기만큼(limit)의 행 조회하는
		 * 객체
		 * 
		 * --> 페이징 처리가 굉장히 간단해짐
		 * 
		 */
		int limit = pagination.getLimit(); // 10개씩 조회
		int offset = (cp - 1) * limit; // cp : 현재 페이지
		RowBounds rowBounds = new RowBounds(offset, limit);

		// Mapper 메서드 호출 시 원래 전달할 수 있는 매개변수 1개
		// -> 2개를 전달할 수 있는 경우가 있음
		// rowBounds 를 이용할 때!
		// -> 첫번째 매개변수 -> SQL 에 전달할 파라미터
		// -> 두번째 매개변수 -> RowBounds 객체 전달
		List<RecipeBoard> recipeBoardList = mapper.mainRecipeBoardList(rowBounds);

		// log.debug("boardList 결과 : {}", boardList);

		// 4. 목록 조회 결과를 Map으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("recipeBoardList", recipeBoardList);

		// 5. 결과 반환
		return map;
	}

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
		// List<Board> boardList = mapper.selectSearchList(paramMap, rowBounds);

		// 4. 목록 조회 결과 + Paginaion 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		
		// 코드 수정 필요
		// map.put("boardList", boardList);

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
