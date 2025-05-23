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

  location.href = `/board/${boardCode}/${prevBoardNo}?cp=${cp}`;
});

// 다음글 버튼
goToNext.addEventListener("click", () => {
  const naviBtn = document.querySelector("#naviBtn");
  const nextBoardNo = naviBtn.dataset.nextBoardNo;
  if (nextBoardNo == 0) {
    alert("다음 글이 없습니다.");
    return;
  }

  location.href = `/board/${boardCode}/${nextBoardNo}?cp=${cp}`;
});
