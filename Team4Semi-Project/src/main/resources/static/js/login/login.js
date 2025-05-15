// LEMONA Login JavaScript

document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const loginError = document.getElementById('loginError');
    const loginBtn = document.getElementById('loginBtn');
    const signupBtn = document.getElementById('signupBtn');
    const findIdBtn = document.getElementById('findIdBtn');
    const findPwBtn = document.getElementById('findPwBtn');

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

    findIdBtn.addEventListener('click', function() {
        window.location.href = 'find-id.html';
    });

    findPwBtn.addEventListener('click', function() {
        window.location.href = 'find-password.html';
    });
});