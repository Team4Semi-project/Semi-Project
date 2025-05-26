const goToListBtn = document.querySelector("#goToListBtn"); // 목록으로 버튼
const goToPrev = document.querySelector("#goToPrev"); // 이전글 버튼
const goToNext = document.querySelector("#goToNext"); // 다음글 버튼
const urlParams = new URLSearchParams(window.location.search);
const cp = urlParams.get("cp") || 1;
const sort = urlParams.get("sort") || "latest";

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
  if (prevBoardNo == 0) {
    alert("이전 글이 없습니다.");
    return;
  }

  const pathname = window.location.pathname;
  const segments = pathname.split("/");
  const categoryNo = segments[3];

  location.href = `/board/1/${categoryNo}/${prevBoardNo}?sort=${sort}`;
});

// 다음글 버튼
goToNext.addEventListener("click", () => {
  const naviBtn = document.querySelector("#naviBtn");
  const nextBoardNo = naviBtn.dataset.nextBoardNo;
  console.log(nextBoardNo);
  if (nextBoardNo == 0) {
    alert("다음 글이 없습니다.");
    return;
  }

  const pathname = window.location.pathname;
  const segments = pathname.split("/");
  const categoryNo = segments[3];

  location.href = `/board/1/${categoryNo}/${nextBoardNo}?sort=${sort}`;
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
