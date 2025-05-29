const goToListBtn = document.querySelector("#goToListBtn"); // 목록으로 버튼
const goToPrev = document.querySelector("#goToPrev"); // 이전글 버튼
const goToNext = document.querySelector("#goToNext"); // 다음글 버튼

// 게시글 수정 버튼
const updateBtn = document.querySelector(".updateBtn");

// 수정 버튼 존재 시
if (updateBtn != null) {

  updateBtn.addEventListener("click", () => {
    // get 방식
    // 현재 : /board/1/2017?cp=1
    // 목표 : /editBoard/1/2017/update?cp=1

    location.href =
      location.pathname.replace("board", "editBoard") +
      "/update" +
      location.search;
  });
}

// 게시글 삭제 버튼
const deleteBtn = document.querySelector(".deleteBtn");

// 삭제 버튼 존재 시
if (deleteBtn != null) {

  deleteBtn.addEventListener("click", () => {
    if (!confirm("삭제하시겠습니까?")) {
      alert("취소됨");
      return;
    }

    // 현재 : /board/1/2004?cp=1
    // 목표 : /editBoard/1/2004/delete?cp=1
    const url = location.pathname.replace("board", "editBoard") + "/delete";

    const queryString = location.search; // ?ch=1

    location.href = url + queryString;

    location.href =
      location.pathname.replace("board", "editBoard") +
      "/delete" +
      location.search;
  });
}


// 목록으로 버튼 클릭 시 이동
goToListBtn.addEventListener("click", () => {
  location.href = `/board/${boardCode}?cp=${cp}`;
  return;
});

// 이전글 버튼
goToPrev.addEventListener("click", () => {
  const naviBtn = document.querySelector("#naviBtn");
  const prevBoardNo = naviBtn.dataset.prevBoardNo;
  if (prevBoardNo == 0) {
    alert("이전 글이 없습니다.");
    return;
  }

  const key = urlParams.get("key") || "";
  const queryb = urlParams.get("queryb") || "";
  const querys = urlParams.get("querys") || "";
  const sort = urlParams.get("sort") || "";
  location.href = `/board/${boardCode}/${prevBoardNo}?cp=${cp}&key=${key}&queryb=${queryb}&querys=${querys}&sort=${sort}`;
});

// 다음글 버튼
goToNext.addEventListener("click", () => {
  const naviBtn = document.querySelector("#naviBtn");
  const nextBoardNo = naviBtn.dataset.nextBoardNo;
  if (nextBoardNo == 0) {
    alert("다음 글이 없습니다.");
    return;
  }

  const key = urlParams.get("key") || "";
  const queryb = urlParams.get("queryb") || "";
  const querys = urlParams.get("querys") || "";
  const sort = urlParams.get("sort") || "";
  location.href = `/board/${boardCode}/${nextBoardNo}?cp=${cp}&key=${key}&queryb=${queryb}&querys=${querys}&sort=${sort}`;
});

/* 좋아요 기능 */
const likes = document.querySelectorAll(".likes");
if (likes) {

  // obj 객체를 서버로 비동기 요청
  likes.forEach((likesBtn) => {
    likesBtn.addEventListener("click", function (e) {
      const loginMemberNo = this.dataset.loginMemberNo;
      const boardNo = this.dataset.boardNo;
      const boardCode = this.dataset.boardCode;
      let likeCK = Number(this.dataset.likeCheck);

      // console.log(loginMemberNo, boardNo, likeCK, `${boardCode}`);

      if (!loginMemberNo || loginMemberNo === "null") {
        alert("로그인 후 이용해주세요");
        return;
      }

      const obj = {
        memberNo: loginMemberNo,
        boardNo: boardNo,
        likeCheck: likeCK
      };

      fetch(`/board/${boardCode}/like`, {
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