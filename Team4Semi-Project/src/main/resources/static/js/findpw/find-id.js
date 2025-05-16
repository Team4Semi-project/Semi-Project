// LEMONA Find Password JavaScript
console.log("비밀번호 찾기 스크립트 로드됨");

document.addEventListener('DOMContentLoaded', function () {
    console.log("DOM 완전히 로드됨 - find-password");

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

    // 이후 비밀번호 찾기 로직 필요 시 여기에 추가
});
