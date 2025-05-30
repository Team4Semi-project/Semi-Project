const goToListBtn = document.querySelector("#goToListBtn"); // 목록으로 버튼
const goToPrev = document.querySelector("#goToPrev"); // 이전글 버튼
const goToNext = document.querySelector("#goToNext"); // 다음글 버튼
const urlParams = new URLSearchParams(window.location.search);
const cp = urlParams.get("cp") || 1;
const sort = urlParams.get("sort") || "latest";
const key = urlParams.get("key") || "";
const queryb = urlParams.get("queryb") || "";
const querys = urlParams.get("querys") || "";

//const popular = urlParams.get('popular');

// 목록으로 버튼 클릭 시 이동
goToListBtn.addEventListener("click", () => {
  const pathname = window.location.pathname;
  const segments = pathname.split("/"); // ['', 'board', '1', '0', '3']
  const categoryNo = segments[3]; // 인덱스 3에 있는 게 바로 '0'

  location.href = `/board/1/${categoryNo}?cp=${cp}&sort=${sort}`;
});

// 이전글 버튼
goToPrev.addEventListener("click", () => {
  const naviBtn = document.querySelector("#naviBtn");
  const prevBoardNo = naviBtn.dataset.prevBoardNo;
  const prevBoard = naviBtn.dataset.prevBoard;
  if (prevBoardNo == 0) {
    alert("이전 글이 없습니다.");
    return;
  }

  const pathname = window.location.pathname;
  const segments = pathname.split("/");
  const categoryNo = segments[3];

  location.href = `/board/${prevBoard}/${categoryNo}/${prevBoardNo}?cp=${cp}&key=${key}&queryb=${queryb}&querys=${querys}&sort=${sort}`;
});

// 다음글 버튼
goToNext.addEventListener("click", () => {
  const naviBtn = document.querySelector("#naviBtn");
  const nextBoardNo = naviBtn.dataset.nextBoardNo;
  const nextBoard = naviBtn.dataset.nextBoard;
  // console.log(nextBoardNo);
  if (nextBoardNo == 0) {
    alert("다음 글이 없습니다.");
    return;
  }

  const pathname = window.location.pathname;
  const segments = pathname.split("/");
  const categoryNo = segments[3];

  location.href = `/board/${nextBoard}/${categoryNo}/${nextBoardNo}?cp=${cp}&key=${key}&queryb=${queryb}&querys=${querys}&sort=${sort}`;
});

const deleteBtn = document.querySelector("#deleteBtn");
// 삭제 버튼 존재 시
if (deleteBtn != null) {

  deleteBtn.addEventListener("click", () => {
    if (!confirm("삭제하시겠습니까?")) {
      return;
    }

    // 현재 : /board/1/2004?cp=1
    // 목표 : /board/1/2004/delete?cp=1
    const url = location.pathname + "/delete";
    console.log(url);
    const queryString = location.search;

    location.href = url + queryString;

  });
}

// 수정하기
const updateBtn = document.querySelector("#updateBtn");
if (updateBtn) {

  updateBtn.addEventListener("click", () => {
    // 보드 번호
    const boardNo = updateBtn.dataset.boardNo;

    // 카테고리 번호 수집
    const pathname = window.location.pathname;
    const segments = pathname.split("/");
    const categoryNo = segments[3];

    // 수정 페이지로
    location.href = `/board/1/${categoryNo}/${boardNo}/update`;
  })
}

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

      console.log(loginMemberNo, boardNo, likeCK, `${boardCode}`);

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

/* 프로필, 닉네임 클릭 시 유저 페이지로 이동 */
document.addEventListener("DOMContentLoaded", function () {
  // 닉네임 클릭
  document.querySelectorAll(".author-name").forEach((el) => {
    el.addEventListener("click", function () {
      const nickname = this.dataset.nickname;
      if (nickname) {
        window.location.href = `/mypage/userProfile?memberNickname=${encodeURIComponent(nickname)}`;
      }
    });
  });

  // 프로필 이미지 클릭
  document.querySelectorAll(".author-image").forEach((el) => {
    el.addEventListener("click", function () {
      const nickname = this.dataset.nickname;
      if (nickname) {
        window.location.href = `/mypage/userProfile?memberNickname=${encodeURIComponent(nickname)}`;
      }
    });
  });
});


