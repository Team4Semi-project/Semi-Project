// 탭 전환
document.getElementById('findIdTab').addEventListener('click', function() {
  window.location.href = 'find-id.html';
});

document.getElementById('findPwTab').addEventListener('click', function() {
  window.location.href = 'find-password.html';
});

// 아이디 찾기 로직
document.getElementById('findIdForm').addEventListener('submit', function(e) {
  e.preventDefault();
  // 아이디 찾기 로직 - 여기서는 예시로 아이디를 표시
  document.getElementById('idResult').textContent = 'example_user123';
  document.getElementById('idResult').style.display = 'block';
  
  // 버튼 전환
  document.getElementById('initialButtons').style.display = 'none';
  document.getElementById('resultButtons').style.display = 'block';
}); 