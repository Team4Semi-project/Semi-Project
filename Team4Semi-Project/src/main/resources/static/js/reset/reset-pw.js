document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("updatePwForm");
  const userId = document.getElementById("#userId");
  const confirmPw = document.getElementById("confirmPw");

  form.addEventListener("submit", async (e) => {
    const memberId = userId.value.trim();
    const pw1 = newPw.value.trim();
    const pw2 = confirmPw.value.trim();

    // 1️⃣ 유효성 검사
    const pwRegex =
      /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,20}$/;
    if (!pw1) {
      alert("새 비밀번호를 입력해주세요.");
      newPw.focus();
      e.preventDefault();
      return;
    }
    if (!pwRegex.test(pw1)) {
      alert("비밀번호는 영문, 숫자, 특수문자를 포함해 8~20자로 입력해주세요.");
      newPw.focus();
      e.preventDefault();
      return;
    }
    if (pw1 !== pw2) {
      alert("비밀번호가 일치하지 않습니다.");
      confirmPw.focus();
      e.preventDefault();
      return;
    }

    //
    try {
      const response = await fetch("/updatepassword", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ memberId, memberPw: pw1 }),
      });

      const result = await response.json();
      if (result.success) {
        alert("비밀번호가 변경되었습니다. 로그인 페이지로 이동합니다.");
        window.location.href = "/login"; // 로그인 페이지 경로에 맞게 수정
      } else {
        alert(result.message || "비밀번호 변경에 실패했습니다.");
      }
    } catch (error) {
      console.log(error)
      alert(error)
      alert("서버 오류가 발생했습니다.");
    }
  });
});
