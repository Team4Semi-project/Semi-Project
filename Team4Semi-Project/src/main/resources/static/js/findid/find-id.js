// LEMONA Find ID JavaScript
console.log("아이디 찾기 스크립트 로드됨");

document.addEventListener('DOMContentLoaded', function () {
    console.log("DOM 완전히 로드됨 - find-id");

    const findIdTab = document.getElementById('findIdTab');
    const findPwTab = document.getElementById('findPwTab');

    // 탭 전환 이벤트
    findIdTab.addEventListener('click', function () {
        console.log("아이디 찾기 탭 클릭");
        window.location.href = 'find-id.html';
    });

    findPwTab.addEventListener('click', function () {
        console.log("비밀번호 찾기 탭 클릭");
        window.location.href = 'find-password.html';
    });

    // 아이디 찾기 버튼 처리
    const form = document.getElementById('findIdForm');
    const idResult = document.getElementById('idResult');
    const initialButtons = document.getElementById('initialButtons');
    const resultButtons = document.getElementById('resultButtons');

    if (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            console.log("아이디 찾기 제출됨");

            // 임시 예시 결과
            idResult.textContent = 'example_user123';
            idResult.style.display = 'block';

            // 버튼 전환
            initialButtons.style.display = 'none';
            resultButtons.style.display = 'block';
        });
    }
});
