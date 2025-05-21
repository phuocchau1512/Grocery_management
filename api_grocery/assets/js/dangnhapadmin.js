document.getElementById('adminLoginForm').addEventListener('submit', function(e) {
    const email = this.email.value.trim();
    const password = this.password.value.trim();
    if (!email || !password) {
        e.preventDefault();
        alert('Vui lòng nhập đầy đủ thông tin!');
    }
});
