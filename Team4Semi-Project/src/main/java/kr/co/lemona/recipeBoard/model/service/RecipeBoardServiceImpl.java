package kr.co.lemona.recipeBoard.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    private final DataSource dataSource;

	@Autowired
	private RecipeBoardMapper mapper;

	@Value("${my.recipeBoard.web-path}")
	private String webPath;
	// /images/recipeBoard/

	@Value("${my.recipeBoard.folder-path}")
	private String folderPath;

    RecipeBoardServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
	// C:/lemonaFiles/recipeBoard/

	// 레시피 게시판 목록 조회
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
		
		for (RecipeBoard recipeBoard : recipeBoardList) {
			log.info("recipeBoard : "+recipeBoard.getBoardCode());
		}
		
		// 4. 목록 조회 결과 + Pagination 객체를 Map 으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("recipeBoardList", recipeBoardList);

		// 5. 결과 반환
		return map;
	}

	/**
	 * 인기 게시판 조회 서비스
	 * 
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
		int offset = (cp - 1) * limit; // 보여줄 페이지의 앞에 건너뛸 게시글 개수
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
		
		// 이전 글 
		RecipeBoard prevBoard = mapper.selectPrevBoard(map);
		
		// 다음 글
		RecipeBoard nextBoard = mapper.selectNextBoard(map);
		
		log.info("map.get(\"boardNo\") : " + map.get("boardNo"));
		
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

	/** 레시피 게시글 작성 서비스
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
		if(result == 0) {return 0;}
		
		// 1-2. 삽입 성공, 삽입된 게시글의 번호를 변수로 저장
		int boardNo = inputBoard.getBoardNo();
		
		// 업로드된 boardStep의 정보를 저장하는 List
		List<BoardStep> boardStepList = new ArrayList<>();
		
		// 업로드할 boardStep을 저장하는 객체
		BoardStep boardStep = null;
		
		for(int i = 0; i < inputStepContent.size(); i++) {
			
			// 단계 순서
			int stepOrder = i + 1;
			
			// 설명 텍스트
			String stepContent = inputStepContent.get(i);
			
			// boardStep에 boardNo와 순서, 설명 삽입
			boardStep = BoardStep.builder()
								 .boardNo(boardNo)
								 .stepContent(stepContent)
								 .stepOrder(stepOrder)
								 .build();	
			
			// 해당 boardStep을 boardStepList에 추가
			boardStepList.add(boardStep);
		}
		// 비어있는 boardStep은 JS에서 처리
		
		// boardStepList를 DB에 삽입
		result = mapper.insertBoardStepContent(boardStepList);
		boardStepList.clear();
		
		// 해쉬태그 추가
		Map<String, Object> map = new HashMap<>();
		map.put("boardNo", boardNo);
		List<String> hashTagList = inputBoard.getHashTagList();
		map.put("hashTagList", hashTagList);
		
		result = mapper.insertNewHashtagIfNotExists(hashTagList);
		result = mapper.insertHashTag(map);
		
		// 2. 업로드된 이미지가 실제로 존재할 경우
		// 업로드된 이미지만 BOARD_STEP에 저장
		
		// 업로드된 이미지를 순회하며 존재유무를 검사
		for(int i = 0; i < images.size(); i++) {
			
			// 파일이 존재하는 경우
			if(!images.get(i).isEmpty()) {
				
				// 원본명
				String originalName = images.get(i).getOriginalFilename();
				
				// 변경명
				String rename = Utility.fileRename(originalName);
				
				// boardStep 저장
				boardStep = BoardStep.builder()
									 .imgOriginalName(originalName)
									 .imgRename(rename)
									 .imgPath(rename)
									 .uploadFile(images.get(i))
									 .stepOrder(i)
									 .boardNo(boardNo)
									 .build();
				
				boardStep.setThumbnailCheck("N");
				
				if(i == thumbnailNo) {
					boardStep.setThumbnailCheck("Y");
				}
				
				// 해당 boardStep을 boardStepList에 추가
				boardStepList.add(boardStep); 
			}
		}
		// 비어있는 boardStep은 JS에서 처리

		// boardStepList를 DB에 삽입
		result = mapper.insertBoardStepImage(boardStepList);
		
		// 다중 삽입 성공 확인
		if(result == boardStepList.size()) {
			
			// 서버에 이미지 저장
			for(BoardStep img : boardStepList) {
				img.getUploadFile().transferTo(new File(folderPath + img.getImgRename()));
			}
		} else { // 부분/전체 삽입 실패
			throw new RuntimeException();
		}
		
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
}
