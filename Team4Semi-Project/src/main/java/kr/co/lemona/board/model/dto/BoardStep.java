package kr.co.lemona.board.model.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 레시피 게시글의 단계별 설명을 담는 DTO 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardStep {

    private int stepNo;             // 단계 번호 (PK)
    private int stepOrder;          // 단계 순서
    private String stepContent;     // 설명 텍스트
    private String imgPath;         // 이미지 경로 저장
    private String imgOriginalName; // 업로드한 원본 파일명
    private String imgRename;       // 서버에 저장된 파일명
    private int boardNo;            // 게시글 번호 (PK 또는 FK)

    // 파일 업로드 시 사용
    private MultipartFile uploadFile;
}