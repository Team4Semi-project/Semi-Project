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
public class MainserviceImpl implements MainService{
	
	@Autowired
	private MainMapper mapper;

	@Override
	public Map<String, Object> mainRecipeBoardList() {
		

		// Mapper 메서드 호출 시 원래 전달할 수 있는 매개변수 1개
		// -> 2개를 전달할 수 있는 경우가 있음
		// rowBounds 를 이용할 때!
		// -> 첫번째 매개변수 -> SQL 에 전달할 파라미터
		// -> 두번째 매개변수 -> RowBounds 객체 전달
		List<RecipeBoard> recipeBoardList = mapper.mainRecipeBoardList();

		// log.debug("boardList 결과 : {}", boardList);

		// 4. 목록 조회 결과를 Map으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();
		
		map.put("recipeBoardList", recipeBoardList);

		// 5. 결과 반환
		return map;
	}

}
