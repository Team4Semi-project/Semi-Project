package kr.co.lemona.recipeBoard.model.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.dto.Pagination;
import kr.co.lemona.common.util.Utility;
import kr.co.lemona.recipeBoard.model.dto.BoardStep;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;
import kr.co.lemona.recipeBoard.model.mapper.RecipeBoardMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@PropertySource("classpath:/config.properties")
@Service
public class RecipeBoardServiceImpl implements RecipeBoardService {

	@Autowired
	private RecipeBoardMapper mapper;

	@Value("${my.recipeBoard.web-path}")
	private String webPath;
	// /images/recipeBoard/

	@Value("${my.recipeBoard.folder-path}")
	private String folderPath;
	// C:/lemonaFiles/recipeBoard/

	/** 레시피 게시판 목록 조회
	 * 
	 * @author miae
	 */
	@Override
	public Map<String, Object> selectRecipeBoardList(Map<String, Object> inputMap) {
		
		// 지역 변수 설정
		int listCount = 0;
		int cp = (int)inputMap.get("cp");
		boolean popularFl = false;
		List<RecipeBoard> recipeBoardList = null;
		
		if(((String)inputMap.get("categoryNo")).equals("popular")) {
			// 조회 대상이 인기 레시피 게시글
			listCount = mapper.getPopularListCount();
			popularFl = true;
		} else {
			// 조회 대상이 일반 레시피 게시글
			int categoryNo = Integer.parseInt((String)inputMap.get("categoryNo"));
			
			// 1. 지정된 카테고리(categoryNo)에서 삭제 되지 않은 게시글 수를 조회
			listCount = mapper.getRecipeBoardListCount(categoryNo);
		}

		// 2. 1번의 결과 + cp 를 이용해서 Pagination 객체를 생성
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 특정 게시판의 지정된 페이지 목록 조회
		int limit = pagination.getLimit(); // 한 페이지 내에 보여줄 게시글 수
		int offset = (cp - 1) * limit; // 보여줄 페이지의 앞에 건너뛸 게시글 개수
		RowBounds rowBounds = new RowBounds(offset, limit);

		if(popularFl) {
			
			recipeBoardList = mapper.selectPopularBoardList(inputMap,rowBounds);
			
		} else {
			// 조회 결과를 리스트에 저장
			recipeBoardList = mapper.selectRecipeBoardList(inputMap, rowBounds);
		}

		// 해시태그 받아오는 부분
		for (RecipeBoard recipeBoard : recipeBoardList) {
			String tags = recipeBoard.getTags();
			if(tags != null && !tags.isEmpty()) {
				 List<String> tagList = Arrays.stream(tags.split(","))
                         .map(String::trim)
                         .collect(Collectors.toList());
				 recipeBoard.setHashTagList(tagList);
			}
		}
		
		// 4. 목록 조회 결과 + Pagination 객체를 Map 으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("recipeBoardList", recipeBoardList);

		// 5. 결과 반환
		return map;
	}

	/**
	 * 레시피 게시글 상세 조회
	 * 
	 * @author miae
	 */
	@Override
	public Map<String, Object> selectOneRecipe(Map<String, Integer> map) {

		// 각각의 테이블에서 값을 조회해와야하기 때문에 map 사용
		Map<String, Object> map2 = new HashMap<>();

		// boardStep은 여러개의 값을 받아오기 때문에 ArrayList 사용
		List<BoardStep> boardStepList = new ArrayList<>();

		boardStepList = mapper.selectBoardStepList(map.get("boardNo"));
		RecipeBoard recipeBoard = mapper.selectOneRecipe(map);
		log.info("sorttttttttttttttttttttttt : " + map.get("sort"));
		
		// 해시태그 받아오는 부분
		if(recipeBoard != null) {
			String tags = recipeBoard.getTags();
			if(tags != null && !tags.isEmpty()) {
				 List<String> tagList = Arrays.stream(tags.split(","))
	                     .map(String::trim)
	                     .collect(Collectors.toList());
				 recipeBoard.setHashTagList(tagList);
			}
		}
		
		
		// 이전 글
		RecipeBoard prevBoard = mapper.selectPrevBoard(map);

		// 다음 글
		RecipeBoard nextBoard = mapper.selectNextBoard(map);

		int prevBoardNo = 0;
		int nextBoardNo = 0;

		// 이전 글 다음글 목록이 있을때만 값을 받아오기
		if (prevBoard != null) {
			prevBoardNo = prevBoard.getBoardNo();
		}

		if (nextBoard != null) {
			nextBoardNo = nextBoard.getBoardNo();
		}

		map2.put("recipeBoard", recipeBoard);
		map2.put("boardStepList", boardStepList);
		map2.put("prevBoardNo", prevBoardNo);
		map2.put("nextBoardNo", nextBoardNo);

		return map2;
	}

	/**
	 * 레시피 게시글 작성 서비스
	 * 
	 * @author 재호
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@Override
	public int insertRecipeBoard(RecipeBoard inputBoard,
								 List<MultipartFile> images,
								 List<String> inputStepContent,
								 int thumbnailNo) throws Exception {

		// 1. 작성자 정보/레시피의 제목/카테고리 번호 먼저 RECIPE_BOARD 테이블에 저장
		int result = mapper.insertRecipeBoard(inputBoard);
		// 1-1. 삽입 실패시 return
		if (result == 0) {return 0;}
		// 1-2. 삽입 성공, 삽입된 게시글의 번호를 변수로 저장
		int boardNo = inputBoard.getBoardNo();
		
		
		// 2. 해쉬태그 추가		
		if(inputBoard.getHashTagList()!=null) {			
			List<String> hashTagList = inputBoard.getHashTagList();
			// 해쉬 태그 리스트와 게시글 번호를 map 으로 묶기		
			Map<String, Object> map = new HashMap<>();		
			map.put("hashTagList", hashTagList);
			map.put("boardNo", boardNo);
						
			// 해시태그 중복감사 + 해시태그 테이블에 삽입
			result = mapper.insertNewHashtagIfNotExists(hashTagList);
			// 해시태그를 글에 추가
			result = mapper.insertHashTag(map);
		}
		
				
		// 3. boardStep 업로드
		// 업로드된 boardStep의 정보를 저장하는 List
		List<BoardStep> boardStepList = new ArrayList<>();

		// 업로드할 boardStep을 저장하는 객체
		BoardStep boardStep = null;

		for (int i = 0; i < inputStepContent.size(); i++) {

			// 단계 순서
			int stepOrder = i + 1;
			// 설명 텍스트
			String stepContent = inputStepContent.get(i);
			
			// 원본명
			String originalName = null;
			// 변경명
			String rename = null;
			// 썸내일 여부
			String thumbnailCheck = "N";
			
			// 3-1. 단계에 이미지가 있는 경우
			if (!images.get(i).isEmpty()) {

				// 원본명
				originalName = images.get(i).getOriginalFilename();
				// 변경명
				rename = Utility.fileRename(originalName);
				// 썸내일 여부
				if(i==thumbnailNo-1) {thumbnailCheck = "Y";}
			}

			// boardStep에 boardNo와 순서, 설명 삽입
			boardStep = BoardStep.builder()
					.stepOrder(stepOrder)
					.stepContent(stepContent)
					.imgPath(webPath)
					.imgOriginalName(originalName)
					.imgRename(rename)
					.uploadFile(images.get(i))
					.boardNo(boardNo)
					.thumbnailCheck(thumbnailCheck)
					.build();

			// 해당 boardStep을 boardStepList에 추가
			boardStepList.add(boardStep);
		}

		// boardStepList를 DB에 삽입
		result = mapper.insertBoardStepContent(boardStepList);

		
		// 4. 다중 삽입 성공 확인
		if (result > 0) {
			// 서버에 이미지 저장
			for(BoardStep step : boardStepList) {				
				step.getUploadFile().transferTo(new File(folderPath + step.getImgRename()));
			}
		} else { // 삽입 실패
			log.debug("삽입실패");
			throw new RuntimeException();
		}

		// 5. 반환
		return boardNo;
	}

	
	/** 레시피 게시글 조회수 증가
	 * boardNo : 게시글 번호
	 *@author miae
	 */
	@Override
	public int updateReadCount(int boardNo) {
		// 1, 조회수 1 증가 (UPDATE)
		int result = mapper.updateReadCount(boardNo);
		
		// 2. 현재 조회 수 조회
		if(result > 0) {
			return mapper.selectReadCount(boardNo);
		}
		
		// 실패한 경우 -1 반환
		return -1;
	}

	/** 레시피 게시판 전용 검색 결과 목록 조회
	 * @author jihyun
	 */
	@Override
	public Map<String, Object> searchList(Map<String, Object> paramMap, Map<String, Object> inputMap) {
		// paramMap (key, query, boardCode)

		// 1. 지정된 게시판(boardCode)에서
		// 검색 조건에 맞으면서
		// 삭제되지 않은 게시글 수 조회
		
		int cp = (int)inputMap.get("cp");

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
		List<RecipeBoard> recipeBoardList = mapper.selectSearchList(paramMap, rowBounds);

		// 4. 목록 조회 결과 + Paginaion 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("recipeBoardList", recipeBoardList);

		return map;
	}

	/** 인기 게시판 전용 검색 결과 목록 조회
	 * @author jihyun
	 */
	@Override
	public Map<String, Object> searchPopularList(Map<String, Object> paramMap, int cp) {
		// paramMap (key, query, boardCode)

		// 1. 지정된 게시판(boardCode)에서
		// 검색 조건에 맞으면서
		// 삭제되지 않은 게시글 수 조회
		int listCount = mapper.getPopularSearchCount(paramMap);

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
		List<RecipeBoard> popularBoardList = mapper.selectPopularSearchList(paramMap, rowBounds);

		// 4. 목록 조회 결과 + Paginaion 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("boardList", popularBoardList);

		return map;
	}
	
	/** 좋아요 기능
	 * @author 재호
	 */
	@Override
	public int updateLikeCount(Map<String, Integer> map) {
		
		log.debug("map : {}",map);
		
		int result = 0;
		
		if(map.get("likeCheck")==1) {
			// 좋아요가 체크되어있음
			result = mapper.decreaseLikeCount(map);
		} else {
			// 좋아요가 체크되어있지 않음
			result = mapper.increaseLikeCount(map);
		}
		
		if(result>0) {
			// 좋아요 갯수 갱신 반환
			return mapper.updateLikeCount(map); 
		}
		return -1;
	}
	
	/** 인기글 ON
	 * @author 재호
	 */
	@Override
	public int updatePopularStateToY() {
		return mapper.updatePopularStateToY();
	}
	
	/** 인기글 OFF
	 * @author 재호
	 */
	@Override
	public int updatePopularStateToN() {
		return mapper.updatePopularStateToN();
	}

	@Override
	public int deleteRecipeBoard(int boardNo) {
		
		return mapper.deleteRecipeBoard(boardNo);
	}
}
