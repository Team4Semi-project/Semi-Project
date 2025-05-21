package kr.co.lemona.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.dto.Pagination;
import kr.co.lemona.board.model.mapper.DefaultBoardMapper;
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

	    // 썸네일 추출 추가
	    for (Board board : boardList) {
	    	// 글 내용만 가져옴
	        String content = board.getBoardContent();
	        if (content != null) {
	            // 글 내용에서 img 태그의 src 속성 값을 추출하는 정규식을 Patter에 정의 
	        	// 그 정규식으로 matcher 객체 생성
	            Matcher matcher = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']").matcher(content);
	            // matcher에서 패턴에 정의 된 정규식에 맞는 첫번째 문자열을 찾음
	            if (matcher.find()) {
	            	// 문자열을 board의 thumbnail에 세팅  // 0 : img src 태그 전체
	                board.setThumbnail(matcher.group(1)); // 1 : "또는'이 나오기 전까지의 모든 문자(첫번째 ()의 내용인 [^\"']+) 
	            }
	        }
	    }
		
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
