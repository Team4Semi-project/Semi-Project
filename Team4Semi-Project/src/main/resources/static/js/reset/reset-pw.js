document.addEventListener("DOMContentLoaded", function () {
  const goToResetBtn = document.getElementById("goToReset");

  if (goToResetBtn) {
    goToResetBtn.addEventListener("click", function () {
      // 경로 수정: reset 폴더 기준으로 이동
      console.log("버튼 클릭됨!");
      window.location.href = "/reset/findpw-update.html";
    });
  }
});
