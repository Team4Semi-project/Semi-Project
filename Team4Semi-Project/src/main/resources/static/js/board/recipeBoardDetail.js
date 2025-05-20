const goToListBtn = document.querySelector("#goToListBtn"); // 목록으로 버튼
const goToPrev = document.querySelector("#goToPrev"); // 이전글 버튼
const goToNext = document.querySelector("#goToNext"); // 다음글 버튼

// 목록으로 버튼 클릭 시 이동
goToListBtn.addEventListener("click", () => {
  const pathname = window.location.pathname;
  const segments = pathname.split("/"); // ['', 'board', '1', '0', '3']
  const categoryNo = segments[3]; // 인덱스 3에 있는 게 바로 '0'
  location.href = `/board/1/${categoryNo}`;
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
  location.href = `/board/1/${categoryNo}/${prevBoardNo}`;
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
  location.href = `/board/1/${categoryNo}/${nextBoardNo}`;
});
