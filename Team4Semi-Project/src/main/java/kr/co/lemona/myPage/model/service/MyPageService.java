package  kr.co.lemona.myPage.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import kr.co.lemona.member.model.dto.Member;
import kr.co.lemona.myPage.model.dto.UploadFile;

public interface MyPageService {

	/** 회원 정보 수정 서비스
	 * @param inputMember
	 * @return
	 */
	int updateInfo(Member inputMember);

	/** 비밀번호 변경 서비스
	 * @param paramMap
	 * @param memberNo
	 * @return
	 */
	int changePw(Map<String, String> paramMap, int memberNo);

	/** 회원 탈퇴 서비스
	 * @param memberPw
	 * @param memberNo
	 * @return
	 */
	int secession(String memberPw, int memberNo);


	/** 프로필 이미지 수정 서비스
	 * @param profileImg
	 * @param loginMember
	 * @return
	 */
	int profile(MultipartFile profileImg, Member loginMember) throws Exception;

	/** 사용자 조회
	 * @param loginMember
	 * @return
	 * @author miae
	 */
	Map<String, Object> selectMemberInfo(Map<String, Object> inputMap);

	/** 사용자가 작성한 일반 게시글 조회
	 * @param inputMap
	 * @return
	 */
	Map<String, Object> selectMemberBoardList(Map<String, Object> inputMap);
	
//	/** 내가 쓴 글 수 조회 
//     * @param memberId
//     * @return
//     */
//    int getWrittenPostsCount(String memberId);
//
//    /** 내가 쓴 글 목록 조회 
//     * @param memberId
//     * @param currentPage
//     * @param limit
//     * @return
//     */
//    List<PostDTO> getWrittenPosts(String memberId, int currentPage, int limit);
//
//    /** 댓글 수 조회 
//     * @param memberId
//     * @return
//     */
//    int getCommentCount(String memberId);
//
//    /** 좋아요한 게시글 수 조회
//     * @param memberId
//     * @return
//     */
//    int getLikedPostsCount(String memberId);
//
//    /** 좋아요한 게시글 목록 조회
//     * @param memberId
//     * @param currentPage
//     * @param limit
//     * @return
//     */
//    List<PostDTO> getLikedPosts(String memberId, int currentPage, int limit);

}
