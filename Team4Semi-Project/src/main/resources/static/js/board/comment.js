// 레시피, 자유 나누기
let fetchUrl;
if (boardCode === 1) {
  fetchUrl = "/recipeComments";
} else if (boardCode === 2 || boardCode === 3) {
  fetchUrl = "/defaultComments";
}

const selectCommentList = () => {
  fetch(`${fetchUrl}?boardNo=${boardNo}`)
    .then(resp => resp.json())
    .then(data => {
      const commentList = data.commentList;
      const commentCount = data.commentCount;

      document.querySelector(".comment-count-area p span:nth-child(2)").innerText = commentCount;

      const ul = document.querySelector("#commentList");
      ul.innerHTML = ""; // 기존 목록 비우기

      for (let comment of commentList) {
        const li = document.createElement("li");
        li.classList.add("comment-row");
        if (comment.parentCommentNo != 0) li.classList.add("child-comment");

        if (comment.commentDelFl === 'Y') {
          li.innerText = "삭제된 댓글 입니다";
          ul.append(li);
          continue;
        }

        // ---------- 댓글 상단(header)
        const header = document.createElement("div");
        header.classList.add("comment-header");

        const writer = document.createElement("p");
        writer.classList.add("comment-writer");

        const img = document.createElement("img");
        img.src = comment.profileImg ? comment.profileImg : userDefaultIamge;

        const nickname = document.createElement("span");
        nickname.innerText = comment.memberNickname;

        const date = document.createElement("span");
        date.classList.add("comment-date");
        date.innerText = comment.commentWriteDate;

        writer.append(img, nickname, date);
        header.append(writer);

        // ---------- 좋아요 버튼
        const likeBtn = document.createElement("button");
        likeBtn.classList.add("commentLikes");
        likeBtn.dataset.loginMemberNo = loginMemberNo;
        likeBtn.dataset.commentNo = comment.commentNo;
        likeBtn.dataset.likeCheck = comment.likeCheck;
        likeBtn.classList.add(comment.likeCheck == 1 ? "likeChecked" : "likeUnChecked");

        const likeIcon = document.createElement("i");
        likeIcon.classList.add("fa-lemon", comment.likeCheck == 1 ? "fa-solid" : "fa-regular");

        const likeCount = document.createElement("span");
        likeCount.innerText = comment.likeCount;

        likeBtn.append(likeIcon, likeCount);
        header.append(likeBtn);
        li.append(header);

        // 좋아요버튼 기능 살리기
        likeBtn.addEventListener("click", function () {
          const loginMemberNo = this.dataset.loginMemberNo;
          const commentNo = this.dataset.commentNo;
          let likeCK = Number(this.dataset.likeCheck);

          if (!loginMemberNo || loginMemberNo === "null") {
            alert("로그인 후 이용해주세요");
            return;
          }

          const obj = {
            memberNo: loginMemberNo,
            commentNo: commentNo,
            likeCheck: likeCK
          };

          fetch(`${fetchUrl}/like`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(obj)
          })
            .then(resp => resp.text())
            .then(count => {
              if (count == -1) {
                console.log("좋아요 처리 실패");
                return;
              }

              // 상태 토글 및 반영
              likeCK = likeCK === 0 ? 1 : 0;
              this.dataset.likeCheck = likeCK;

              const icon = this.querySelector("i");
              if (icon) {
                icon.classList.toggle("fa-regular", likeCK === 0);
                icon.classList.toggle("fa-solid", likeCK === 1);
              }

              const countSpan = this.querySelector("span");
              if (countSpan) {
                countSpan.innerText = count;
              }
            })
            .catch(err => {
              console.error("좋아요 처리 중 오류:", err);
            });
        });

        // ---------- 댓글 본문
        const content = document.createElement("p");
        content.classList.add("comment-content");
        content.innerText = comment.commentContent;
        li.append(content);

        // ---------- 버튼 영역
        const btnArea = document.createElement("div");
        btnArea.classList.add("comment-btn-area");

        if (loginMemberNo != null) {
          // 답글 버튼
          const replyBtn = document.createElement("button");
          replyBtn.innerText = "답변하기";
          replyBtn.setAttribute("onclick", `showInsertComment(${comment.commentNo}, this)`);
          btnArea.append(replyBtn);

          // 수정/삭제 버튼 그룹
          if (loginMemberNo == comment.memberNo) {
            const editDeleteWrapper = document.createElement("div");
            editDeleteWrapper.classList.add("edit-delete-group");

            const updateBtn = document.createElement("button");
            updateBtn.innerText = "수정하기";
            updateBtn.setAttribute("onclick", `showUpdateComment(${comment.commentNo}, this)`);

            const deleteBtn = document.createElement("button");
            deleteBtn.innerText = "삭제하기";
            deleteBtn.setAttribute("onclick", `deleteComment(${comment.commentNo})`);

            editDeleteWrapper.append(updateBtn, deleteBtn);
            btnArea.append(editDeleteWrapper);
          }
        }

        li.append(btnArea);
        ul.append(li);
      }
    });
};


// 댓글 입력창 높이 조정
document.addEventListener("DOMContentLoaded", function () {
  const textarea = document.getElementById("commentContent");

  if (textarea) {
    textarea.setAttribute("style", "height:auto; overflow-y:hidden;");
    textarea.addEventListener("input", function () {
      this.style.height = "auto"; // 초기화
      this.style.height = (this.scrollHeight) + "px"; // 실제 내용 높이로 설정
    });
  }
});

// 댓글 등록 (ajax)
const commentContent = document.querySelector("#commentContent"); // textarea
const addComment = document.querySelector("#addComment"); // button

// 댓글 등록 버튼 클릭 시
addComment.addEventListener("click", () => {

  // 로그인이 되어있지 않은 경우
  if (loginMemberNo == null) {
    alert("로그인 후 이용해주세요");
    return;
  }

  // 댓글 내용이 작성되지 않은 경우
  if (commentContent.value.trim().length == 0) {
    alert("내용 작성 후 등록버튼 클릭해주세요");
    commentContent.value = "";
    commentContent.focus();
    return;
  }

  // ajax를 이용해 댓글 등록 요청
  const data = {
    "commentContent": commentContent.value,
    "memberNo": loginMemberNo,
    "boardNo": boardNo
  };

  fetch(`${fetchUrl}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
    .then(resp => resp.text())
    .then(result => {

      if (result > 0) {

        alert("댓글이 등록 되었습니다");
        selectCommentList(); // 댓글 목록 다시 조회해서 화면에 출력
        commentContent.value = ""; // 작성한 댓글 내용 지우기
        commentContent.style.height = "auto";
      } else {
        alert("댓글 등록 실패");
      }

    }).catch(err => console.log(err));


});


/** 답글 작성 화면 추가
* @param {*} parentCommentNo
* @param {*} btn
*/
const showInsertComment = (parentCommentNo, btn) => {
  // ** 답글 작성 textarea가 한 개만 열릴 수 있도록 만들기 **
  const temp = document.getElementsByClassName("commentInsertContent");
  if (temp.length > 0) { // 답글 작성 textara가 이미 화면에 존재하는 경우
    if (confirm("다른 답글을 작성 중입니다. 현재 댓글에 답글을 작성 하시겠습니까?")) {
      temp[0].nextElementSibling.remove(); // 버튼 영역부터 삭제
      temp[0].remove(); // textara 삭제 (기준점은 마지막에 삭제해야 된다!)

    } else {
      return; // 함수를 종료시켜 답글이 생성되지 않게함.
    }
  }

  // 답글을 작성할 textarea 요소 생성
  const textarea = document.createElement("textarea");
  textarea.classList.add("commentInsertContent");
  textarea.placeholder = "답글을 입력하세요";
  textarea.maxLength = "100";
  textarea.rows = "1"

  // 답글(자식 댓글) 입력창 높이 조절
  textarea.setAttribute("style", "height:auto; overflow-y:hidden;");
  textarea.addEventListener("input", function () {
    this.style.height = "auto"; // 초기화
    this.style.height = (this.scrollHeight) + "px"; // 실제 내용 높이로 설정
  });

  // 답글 버튼의 부모의 뒤쪽에 textarea 추가
  // after(요소) : 뒤쪽에 추가
  btn.parentElement.after(textarea);
  // 답글 버튼 영역 + 등록/취소 버튼 생성 및 추가
  const commentBtnArea = document.createElement("div");
  commentBtnArea.classList.add("comment-btn-area");
  const insertBtn = document.createElement("button");
  insertBtn.innerText = "등록";

  // 매개변수에 +문자열+ 작성 시 Number타입으로 형변환 하라
  // Number(parentCommentNo) == +parentCommentNo+
  insertBtn.setAttribute("onclick", "insertChildComment(" + parentCommentNo + ", this)");
  const cancelBtn = document.createElement("button");
  cancelBtn.innerText = "취소";
  cancelBtn.setAttribute("onclick", "insertCancel(this)");

  // 답글 버튼 영역의 자식으로 등록/취소 버튼 추가
  const btnBox = document.createElement("div");
  btnBox.append(cancelBtn, insertBtn);

  commentBtnArea.append(btnBox);

  // 답글 버튼 영역을 화면에 추가된 textarea 뒤쪽에 추가
  textarea.after(commentBtnArea);
}

// ---------------------------------------

/** 답글 (자식 댓글) 작성 취소
* @param {*} cancelBtn : 취소 버튼
*/
const insertCancel = (cancelBtn) => {
  const commentBtnArea = cancelBtn.closest(".comment-btn-area"); // 버튼 묶음 전체
  const textarea = commentBtnArea?.previousElementSibling;

  // 취소 버튼이 존재하는 버튼영역 삭제
  textarea.remove();
  commentBtnArea.remove();
}

// 답글 (자식 댓글) 등록
const insertChildComment = (parentCommentNo, btn) => {
  // 답글 내용이 작성된 textarea 요소
  const commentBtnArea = btn.closest(".comment-btn-area"); // 버튼 전체 영역
  const textarea = commentBtnArea?.previousElementSibling; // 그 이전에 붙은 textarea

  // 유효성 검사
  if (textarea.value.trim().length == 0) {
    alert("내용 작성 후 등록 버튼을 클릭해주세요");
    textarea.focus;
    return;
  }

  // ajax를 이용해 전달할 데이터
  const data = {
    "commentContent": textarea.value, // 작성한 글
    "memberNo": loginMemberNo,              // 누가 작성했는가?
    "boardNo": boardNo,                     // 어느 게시글에 달린 댓글인가?
    "parentCommentNo": parentCommentNo      // 어느 댓글에 달린 답글인가?
  };

  fetch(`${fetchUrl}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
    .then(resp => resp.text())
    .then(result => {

      if (result > 0) {
        alert("답글이 등록되었습니다");
        selectCommentList();
        textarea.style.height = "auto";
      } else {
        alert("답글 등록 실패");
      }
    })
}

/** 댓글 삭제
 * @param comment (현재 댓글 번호)
 */
const deleteComment = (commentNo) => {

  // 취소 선택 시
  if (!confirm("삭제 하시겠습니까?")) return;

  fetch(`${fetchUrl}`, {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(commentNo)
  })
    .then(resp => resp.text())
    .then(result => {

      if (result > 0) {
        alert("삭제 되었습니다");
        selectCommentList();

      } else {
        alert("삭제 실패");
      }

    });
}


// 댓글 수정 (ajax)
// 수정 취소 시 원래 댓글 형태로 돌아가기 위한 백업 변수
let beforeCommentRow;
/** 댓글 수정 화면 전환
* @param {*} commentNo
* @param {*} btn
*/
const showUpdateComment = (commentNo, btn) => {
  /* 댓글 수정 화면이 1개만 열릴 수 있게 하기 */
  const temp = document.querySelector(".update-textarea");
  // .update-textarea 존재 == 열려있는 댓글 수정창이 존재
  if (temp != null) {
    if (confirm("수정 중인 댓글이 있습니다. 현재 댓글을 수정 하시겠습니까?")) {
      const commentRow = temp.parentElement; // 기존 댓글 행
      commentRow.after(beforeCommentRow); // 기존 댓글 다음에 백업 추가
      commentRow.remove(); // 기존 삭제 -> 백업이 기존 행 위치로 이동
    } else { // 취소
      return;
    }
  }
  // -------------------------------------------
  // 1. 댓글 수정이 클릭된 행 (.comment-row) 선택
  const commentRow = btn.closest("li");

  // 2. 행 전체를 백업(복제)
  // 요소.cloneNode(true) : 요소 복제,
  //           매개변수 true == 하위 요소도 복제
  beforeCommentRow = commentRow.cloneNode(true);
  // console.log(beforeCommentRow);

  // 3. 기존 댓글에 작성되어 있던 내용만 얻어오기
  let beforeContent = commentRow.children[1].innerText;

  // 4. 댓글 행 내부를 모두 삭제
  commentRow.innerHTML = "";

  // 5. textarea 생성 + 클래스 추가 + 내용 추가
  const textarea = document.createElement("textarea");
  textarea.classList.add("update-textarea");
  textarea.value = beforeContent;
  textarea.maxLength = "100";
  textarea.style.height = "auto";
  textarea.rows = "1"

  // 6. 댓글 행에 textarea 추가
  commentRow.append(textarea);

  // 수정 입력창 높이 조절  
  textarea.style.overflowY = "hidden";
  textarea.style.resize = "none";

  // DOM 추가 후 scrollHeight 반영
  setTimeout(() => {
    textarea.style.height = "auto";
    textarea.style.height = textarea.scrollHeight + "px";
  }, 0);

  textarea.addEventListener("input", function () {
    this.style.height = "auto"; // 초기화
    this.style.height = (this.scrollHeight) + "px"; // 실제 내용 높이로 설정
  });

  // 7. 버튼 영역 생성
  const commentBtnArea = document.createElement("div");
  commentBtnArea.classList.add("comment-btn-area");

  // 8. 수정 버튼 생성
  const updateBtn = document.createElement("button");
  updateBtn.innerText = "수정하기";
  updateBtn.setAttribute("onclick", `updateComment(${commentNo}, this)`);

  // 9. 취소 버튼 생성
  const cancelBtn = document.createElement("button");
  cancelBtn.innerText = "취소하기";
  cancelBtn.setAttribute("onclick", "updateCancel(this)");

  // 10. 버튼 영역에 수정/취소 버튼 추가 후
  //     댓글 행에 버튼 영역 추가
  const editDeleteWrapper = document.createElement("div");
  editDeleteWrapper.classList.add("edit-delete-group");

  editDeleteWrapper.append(cancelBtn, updateBtn);

  commentBtnArea.append(editDeleteWrapper);
  commentRow.append(commentBtnArea);
}

// --------------------------------------------------------------------

/** 댓글 수정 취소
* @param {*} btn : 취소 버튼
*/
const updateCancel = (btn) => {
  if (confirm("취소 하시겠습니까?")) {
    const commentRow = btn.closest("li"); // 기존 댓글 행
    commentRow.after(beforeCommentRow); // 기존 댓글 다음에 백업 추가
    commentRow.remove(); // 기존 삭제 -> 백업이 기존 행 위치로 이동
  }
}


// 댓글 수정 fetch 요청
const updateComment = (commentNo, btn) => {

  // 수정된 내용이 작성된 textarea 얻어오기
  const textarea = btn.parentElement.parentElement.previousElementSibling;

  // 유효성 검사
  if (textarea.value.trim().length == 0) {
    alert("댓글 작성 후 수정 버튼을 클릭해주세요");
    textarea.focus();
    return;
  }

  // 수정 시 전달 데이터
  const data = {
    "commentNo": commentNo,
    "commentContent": textarea.value
  }

  fetch(`${fetchUrl}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
    .then(resp => resp.text())
    .then(result => {

      if (result > 0) {
        alert("댓글이 수정 되었습니다");
        selectCommentList();
        textarea.style.height = "auto";
      } else {
        alert("댓글 수정 실패");
      }

    })

}

/* 좋아요 기능 */
const commentLikes = document.querySelectorAll(".commentLikes");
if (commentLikes) {
  commentLikes.forEach((likesBtn) => {
    likesBtn.addEventListener("click", function (e) {
      const loginMemberNo = this.dataset.loginMemberNo;
      const commentNo = this.dataset.commentNo;
      let likeCK = Number(this.dataset.likeCheck);

      console.log(loginMemberNo, commentNo, likeCK, `${boardCode}`);

      if (!loginMemberNo || loginMemberNo === "null") {
        alert("로그인 후 이용해주세요");
        return;
      }

      const obj = {
        memberNo: loginMemberNo,
        commentNo: commentNo,
        likeCheck: likeCK
      };

      fetch(`${fetchUrl}/like`, {
        method: "post",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(obj)
      })
        .then(resp => resp.text())
        .then(count => {

          if (count == -1) {
            console.log("좋아요 처리 실패");
            return;
          }

          // 상태 토글 및 반영
          likeCK = likeCK === 0 ? 1 : 0;
          this.dataset.likeCheck = likeCK;

          // 아이콘 클래스 토글
          const icon = this.querySelector("i");
          if (icon) {
            icon.classList.toggle("fa-regular");
            icon.classList.toggle("fa-solid");
          }

          // 좋아요 수 변경
          const countSpan = this.querySelector("span");
          if (countSpan) {
            countSpan.innerText = count;
          }
        })
        .catch(err => {
          console.error("좋아요 처리 중 오류:", err);
        });
    });
  });
};