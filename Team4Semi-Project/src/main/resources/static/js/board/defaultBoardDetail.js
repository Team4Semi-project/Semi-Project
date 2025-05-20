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