document.getElementById('registerForm').addEventListener('submit', function(e) {
  e.preventDefault();
  // 여기에 회원가입 로직을 추가
  window.location.href = 'login.html';
});

document.getElementById('checkIdBtn').addEventListener('click', function() {
  // 아이디 중복 체크 로직
  alert('사용 가능한 아이디입니다.');
});

document.getElementById('getVerificationBtn').addEventListener('click', function() {
  // 인증번호 발송 로직
  alert('인증번호가 발송되었습니다.');
});

document.getElementById('verifyBtn').addEventListener('click', function() {
  // 인증 로직
  alert('인증되었습니다.');
});

document.getElementById('checkNicknameBtn').addEventListener('click', function() {
  // 닉네임 중복 체크 로직
  alert('사용 가능한 닉네임입니다.');
});

// 비밀번호 일치 체크
const passwordInputs = document.querySelectorAll('input[type="password"]');
const passwordError = document.getElementById('passwordError');

passwordInputs[1].addEventListener('input', function() {
  if (passwordInputs[0].value !== passwordInputs[1].value) {
    passwordError.style.display = 'block';
  } else {
    passwordError.style.display = 'none';
  }
});