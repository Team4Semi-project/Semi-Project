// LEMONA Register JavaScript
console.log("회원가입 스크립트 로드됨");

document.addEventListener('DOMContentLoaded', function () {
    console.log("DOM 완전히 로드됨 - register");

    // 로그인 페이지로 이동
    const loginBtn = document.getElementById('goLoginBtn');
    if (loginBtn) {
        loginBtn.addEventListener('click', function () {
            console.log("로그인 페이지로 이동 클릭됨");
            window.location.href = 'login.html';
        });
    }

    // 회원가입 로직 추가 시 여기에 작성
});
