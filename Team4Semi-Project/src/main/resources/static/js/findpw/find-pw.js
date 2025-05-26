document.addEventListener("DOMContentLoaded", function () {
  const goToResetBtn = document.getElementById("goToReset");

  if (goToResetBtn) {
    goToResetBtn.addEventListener("click", function (e) {
      e.preventDefault(); // form 제출 막기
      window.location.href = "/reset/findpw-update.html"; // 여길 서버 경로에 맞게 수정하세요
    });
  }

  // ⭐ 폼 제출 처리 코드 추가
  const findPwForm = document.getElementById("findPwForm");

  if (findPwForm) {
    findPwForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const form = e.target;
      const data = {
        userId: form.userId.value.trim(),
        userName: form.userName.value.trim(),
        userEmail: form.userEmail.value.trim(),
      };

      try {
        const response = await fetch(form.action, {
          method: form.method,
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(data),
        });
        const result = await response.json();

        if (result.success) {
          window.location.href = result.redirectUrl;
        } else {
          alert(result.message);
        }
      } catch (error) {
        alert("서버 통신 중 오류가 발생했습니다.");
      }
    });
  }
});
