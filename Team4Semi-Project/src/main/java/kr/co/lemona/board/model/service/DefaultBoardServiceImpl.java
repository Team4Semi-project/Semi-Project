package kr.co.lemona.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.dto.Pagination;
import kr.co.lemona.board.model.mapper.DefaultBoardMapper;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class DefaultBoardServiceImpl implements DefaultBoardService{

	@Autowired
	private DefaultBoardMapper mapper;
	


	/** 게시글 리스트 조회
	 * @author 민장
	 */
	@Override
	public Map<String, Object> selectBoardList(int boardCode, int cp) {
		// 1. 지정된 게시판(boardCode)에서 삭제 되지 않은 게시글 수를 조회
		int listCount = mapper.getListCount(boardCode);

		// 2. 1번의 결과 + cp를 이용해서 Pagination 객체를 생성
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 특정 게시판의 지정된 페이지 목록 조회
		int limit = pagination.getLimit(); // 10개씩 조회
		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);

		// 게시글 조회
		List<Board> boardList = mapper.selectBoardList(boardCode, rowBounds);

		// 4. 목록 조회 결과 + Pagination 객체를 Map 으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("boardList", boardList);

		// 5. 결과 반환
		return map;
	}

	/** 게시글 상세 조회
	 * @author 민장
	 */
	@Override
	public Board selectOne(Map<String, Integer> map) {
		// 
		return mapper.selectOne(map);
	}
}
