// LEMONA Login JavaScript
console.log("로그인 스크립트가 로드되었습니다!");

document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM이 로드되었습니다!");

    // 버튼 요소 선택
    console.log("버튼 요소 선택 시도...");
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const loginError = document.getElementById('loginError');
    const loginBtn = document.getElementById('loginBtn');
    const signupBtn = document.getElementById('signupBtn');
    const findIdBtn = document.getElementById('findIdBtn');
    const findPwBtn = document.getElementById('findPwBtn');
    
    console.log("로그인 버튼:", loginBtn);
    console.log("회원가입 버튼:", signupBtn);
    console.log("아이디 찾기 버튼:", findIdBtn);
    console.log("비밀번호 찾기 버튼:", findPwBtn);

    // Login form submission
    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Validate inputs
        if (!usernameInput.value.trim() || !passwordInput.value.trim()) {
            loginError.style.display = 'block';
            return;
        }
        
        // Simulating login - In a real app, you would send an API request here
        // For demonstration, we'll just show the error message
        loginError.style.display = 'block';
        
        // In a real app, you would redirect on success:
        // window.location.href = 'dashboard.html';
    });

    // Clear error message when inputs change
    usernameInput.addEventListener('input', function() {
        loginError.style.display = 'none';
    });
    
    passwordInput.addEventListener('input', function() {
        loginError.style.display = 'none';
    });

    // Button event handlers
    signupBtn.addEventListener('click', function() {
        window.location.href = 'register.html';
    });

    // 회원가입 버튼 이벤트
    signupBtn.addEventListener('click', function() {
        console.log("회원가입 버튼 클릭됨!");
        window.location.href = 'register.html';
    });

    // 아이디 찾기 버튼 이벤트
    findIdBtn.addEventListener('click', function() {
        console.log("아이디 찾기 버튼 클릭됨!");
        window.location.href = 'find-id.html';
    });

    // 비밀번호 찾기 버튼 이벤트
    findPwBtn.addEventListener('click', function() {
        console.log("비밀번호 찾기 버튼 클릭됨!");
        window.location.href = 'find-password.html';
    });
});