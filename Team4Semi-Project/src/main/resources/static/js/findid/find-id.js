console.log("아이디 찾기 스크립트 로드됨");

document.addEventListener('DOMContentLoaded', function () {
    console.log("DOM 완전히 로드됨 - find-id");

    const findIdTab = document.getElementById('findIdTab');
    const findPwTab = document.getElementById('findPwTab');
    const form = document.getElementById('findIdForm');
    const nameInput = document.getElementById('nameInput');
    const emailInput = document.getElementById('emailInput');
    const foundIdInput = document.getElementById('foundId');
    const initialButtons = document.getElementById('initialButtons');
    const resultButtons = document.getElementById('resultButtons');

    // 탭 전환
    findIdTab.addEventListener('click', function () {
        window.location.href = 'find-id.html';
    });

    findPwTab.addEventListener('click', function () {
        window.location.href = 'find-password.html';
    });

    // 아이디 찾기 제출
    if (form) {
        form.addEventListener('submit', async function (e) {
            e.preventDefault();

            const name = nameInput.value.trim();
            const email = emailInput.value.trim();

            if (!name || !email) {
                alert("이름과 이메일을 모두 입력해주세요.");
                return;
            }

            try {
                const response = await fetch('/find-id', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ name, email })
                });

                if (!response.ok) {
                    throw new Error("서버 오류 또는 네트워크 문제");
                }

                const data = await response.json();

                if (data.success && data.userId) {
                    foundIdInput.value = data.userId;
                    initialButtons.style.display = 'none';
                    resultButtons.style.display = 'flex';
                } else {
                    alert("일치하는 아이디를 찾을 수 없습니다.");
                }
            } catch (error) {
                console.error("에러 발생:", error);
                alert("오류가 발생했습니다. 다시 시도해주세요.");
            }
        });
    }
});
