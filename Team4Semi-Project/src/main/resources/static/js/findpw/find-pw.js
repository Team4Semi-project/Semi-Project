document.getElementById('findPwForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const form = e.target;
  const data = {
    userId: form.userId.value.trim(),
    userName: form.userName.value.trim(),
    userEmail: form.userEmail.value.trim(),
  };

  try {
    const response = await fetch(form.action, {
      method: form.method,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    const result = await response.json();

    if (result.success) {
      window.location.href = result.redirectUrl;
    } else {
      alert(result.message);
    }
  } catch (error) {
    alert('서버 통신 중 오류가 발생했습니다.');
  }
});
