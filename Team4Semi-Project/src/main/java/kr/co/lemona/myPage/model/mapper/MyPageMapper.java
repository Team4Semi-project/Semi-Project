package kr.co.lemona.myPage.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kr.co.lemona.member.model.dto.Member;
import kr.co.lemona.myPage.model.dto.UploadFile;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;


@Mapper
public interface MyPageMapper {

	/** 회원 정보 수정
	 * @param inputMember
	 * @return
	 */
	int updateInfo(Member inputMember);

	/** 회원의 비밀번호 조회
	 * @param memberNo
	 * @return
	 */
	String selectPw(int memberNo);

	/** 회원 비밀번호 변경
	 * @param paramMap
	 * @return
	 */
	int changePw(Map<String, String> paramMap);

	/** 회원 탈퇴
	 * @param memberNo
	 * @return
	 */
	int secession(int memberNo);

	/** 파일 정보를 DB에 삽입
	 * @param uf
	 * @return
	 */
	int insertUploadFile(UploadFile uf);

	/** 파일 목록 조회
	 * @param memberNo
	 * @return
	 */
	List<UploadFile> fileList(int memberNo);

	/** 프로필 이미지 변경
	 * @param member
	 * @return
	 */
	int profile(Member member);

	/** 사용자 조회
	 * @param loginMember
	 * @return
	 * @author miae
	 */
	Member selectMember(int memberNo);

	/** 특정 사용자가 쓴 글 목록
	 * @param memberNo
	 * @return
	 * @author miae
	 * @param rowBounds 
	 */
	List<RecipeBoard> selectMemberRecipeList(int memberNo, RowBounds rowBounds);

	/** 특정 사용자가 쓴 글의 갯수 조회
	 * @param memberNo
	 * @return
	 */
	int getMemberRecipeListCount(int memberNo);

}
