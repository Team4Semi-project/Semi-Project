package kr.co.lemona.main.model.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	// 메인화면에 출력될 최근 인기 게시글 4개 조회
	@Override
	public Map<String, Object> selectPopularBoardList(int cp) {
		
		// 1. 레시피 게시글 중 조건에 맞는 최근 인기 게시글 4개 조회해서 List에 담기
		List<RecipeBoard> popularBoardList = mapper.selectPopularBoardList();
		
		// 2. 목록 조회 결과 객체를 Map 으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();
		map.put("popularBoardList", popularBoardList);
		
		// 3. 결과 반환
		return map;
	}
	
	// 메인화면에 출력될 최근 레시피 게시글 4개 조회
	@Override
	public Map<String, Object> selectRecipeBoardList(int categoryNo, int cp) {
		
		// 1. 최근 레시피 게시글 4개 조회해서 List에 담기
		List<RecipeBoard> recipeBoardList = mapper.selectRecipeBoardList(categoryNo);
		
		// 2. 목록 조회 결과 객체를 Map 으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();
		map.put("recipeBoardList", recipeBoardList);
		
		// 3. 결과 반환
		return map;
	}
	
	// 전체 게시판 통합 검색 서비스
	@Override
	public Map<String, Object> AllsearchList(Map<String, Object> paramMap, int cp) {
		// paramMap (key, query)

		// 1. 검색 조건에 맞으면서 삭제되지 않은 게시글 수 조회
		int listCount = mapper.getSearchCount(paramMap);

		// 2. 1번의 결과 + cp를 이용해서 Pagination 객체 생성
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
		
		// 4. 조회 결과 Board 객체 형태로 List에 담기
		// 레시피 게시판과 그 외의 게시판에서 검색어 조회 후 해당 게시글들 담기
		 List<Board> searchAllBoardList = mapper.searchAllBoardList(paramMap, rowBounds);
		 
		// 해시태그 받아오는 부분
			for (Board recipeBoard : searchAllBoardList) {
				String tags = recipeBoard.getTags();
				if(tags != null && !tags.isEmpty()) {
					 List<String> tagList = Arrays.stream(tags.split(","))
	                         .map(String::trim)
	                         .collect(Collectors.toList());
					 recipeBoard.setHashTagList(tagList);
				}
			}

		// 5. 목록 조회 결과 + Paginaion 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("searchAllBoardList", searchAllBoardList);

		// 6. 결과 반환
		return map;

	}
}
