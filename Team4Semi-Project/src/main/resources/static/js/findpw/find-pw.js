document.addEventListener("DOMContentLoaded", () => {
  const findPwForm = document.getElementById("findPwForm");
  const userIdInput = document.getElementById("userId");
  const userNameInput = document.getElementById("userName");
  const userEmailInput = document.getElementById("userEmail");

  findPwForm.addEventListener("submit", async (e) => {
    e.preventDefault(); // 기본 form 제출 막기

    const id = userIdInput.value.trim();
    const name = userNameInput.value.trim();
    const email = userEmailInput.value.trim();

    // 1️⃣ 입력 유효성 검사
    if (!id) {
      alert("아이디를 입력해주세요.");
      userIdInput.focus();
      return;
    }

    if (!name) {
      alert("이름을 입력해주세요.");
      userNameInput.focus();
      return;
    }

    if (!email) {
      alert("이메일을 입력해주세요.");
      userEmailInput.focus();
      return;
    }

    // 2️⃣ 서버에 POST 요청 보내기
    try {
      const response = await fetch("/findpassword", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ id, name, email }),
      });

      const data = await response.json();
      console.log("응답 내용:", data);

      if (data.success) {
        // 비밀번호 재설정 페이지로 이동
        window.location.href = data.redirectUrl;
      } else {
        alert(data.message || "입력한 값이 일치하지 않습니다.");
      }
    } catch (error) {
      console.error("에러:", error);
      alert("비밀번호 찾기 중 오류가 발생했습니다.");
    }
  });
});
