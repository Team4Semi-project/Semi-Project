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
import kr.co.lemona.board.model.dto.DefaultComment;
import kr.co.lemona.board.model.dto.Pagination;
import kr.co.lemona.board.model.mapper.DefaultBoardMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class DefaultBoardServiceImpl implements DefaultBoardService {

	@Autowired
	private DefaultBoardMapper mapper;

	/**
	 * 게시글 리스트 조회
	 * 
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
					// 문자열을 board의 thumbnail에 세팅 // 0 : img src 태그 전체
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

	/**
	 * 게시글 상세 조회
	 * 
	 * @author 민장
	 */
	@Override
	public Map<String, Object> selectOne(Map<String, Integer> map) {
		log.debug("🟡 ServiceImpl: 넘겨받은 파라미터 맵 => {}", map);
		log.debug("🟡 ServiceImpl: memberNo => {}", map.get("memberNo"));
		Map<String, Object> resultMap = new HashMap<>();

		Board board = mapper.selectOne(map);

		// 이전 글
		Board prevBoard = mapper.selectPrevBoard(map);

		// 다음 글
		Board nextBoard = mapper.selectNextBoard(map);

		// 이전 글 다음글 목록이 있을때만 값을 받아오기
		int prevBoardNo = (prevBoard != null) ? prevBoardNo = prevBoard.getBoardNo() : 0;
		int nextBoardNo = (nextBoard != null) ? nextBoardNo = nextBoard.getBoardNo() : 0;

		// 3. 댓글 목록 (로그인 회원의 댓글 좋아요 여부 포함)
		Map<String, Object> commentMap = new HashMap<>();
		commentMap.put("boardNo", map.get("boardNo"));
		commentMap.put("memberNo", map.getOrDefault("memberNo", 0)); // 있으면 가져오고 없으면 0 : 에러 방지

		List<DefaultComment> commentList = mapper.selectCommentList(commentMap);

		// 4. Board 에 댓글 목록 추가
		board.setCommentList(commentList);

		resultMap.put("board", board);
		resultMap.put("prevBoardNo", prevBoardNo);
		resultMap.put("nextBoardNo", nextBoardNo);

		return resultMap;
	}

	/**
	 * 해당 게시판 검색 결과 조회
	 * 
	 * @author jihyun
	 */
	@Override
	public Map<String, Object> serchList(Map<String, Object> paramMap, int cp) {
		// paramMap (key, query, boardCode)

		// 1. 지정된 게시판(boardCode)에서
		// 검색 조건에 맞으면서
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
		List<Board> boardList = mapper.selectSearchList(paramMap, rowBounds);

		// 4. 목록 조회 결과 + Paginaion 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("boardList", boardList);

		return map;
	}

	/**
	 * 게시글 조회수 증가 boardNo : 게시글 번호
	 */
	@Override
	public int updateReadCount(int boardNo) {
		// 1, 조회수 1 증가 (UPDATE)
		int result = mapper.updateReadCount(boardNo);

		// 2. 현재 조회 수 조회
		if (result > 0) {
			return mapper.selectReadCount(boardNo);
		}

		// 실패한 경우 -1 반환
		return -1;
	}
}
