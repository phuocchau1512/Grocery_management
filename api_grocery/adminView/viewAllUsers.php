<div>
  <h2>User/Admin List</h2>

  <div class="mb-3">
    <label for="filterRole" class="form-label">Filter by Role</label>
    <select id="filterRole" class="form-select" onchange="filterUsers()">
      <option value="all">All</option>
      <option value="user">User thường</option>
      <option value="admin">Admin</option>
    </select>
  </div>

  <table class="table" id="userTable">
    <thead>
      <tr>
        <th class="text-center">S.N.</th>
        <th class="text-center">Name</th>
        <th class="text-center">Email</th>
        <th class="text-center">Role</th>
        <th class="text-center" colspan="2">Action</th>
      </tr>
    </thead>
    <tbody id="userTableBody">
      <!-- User rows sẽ load bằng JavaScript -->
    </tbody>
  </table>

  <!-- Nút thêm người dùng -->
  <button type="button" class="btn btn-secondary" data-bs-toggle="modal" data-bs-target="#addUserModal">
    Add User
  </button>

  <!-- Modal thêm người dùng -->
  <div class="modal fade" id="addUserModal" tabindex="-1" aria-labelledby="addUserModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="addUserModalLabel">Add New User</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form id="addUserForm">
            <div class="mb-3">
              <label for="userName" class="form-label">Name</label>
              <input type="text" class="form-control" id="userName" name="name" required>
            </div>
            <div class="mb-3">
              <label for="userEmail" class="form-label">Email</label>
              <input type="email" class="form-control" id="userEmail" name="email" required>
            </div>
            <div class="mb-3">
              <label for="userPassword" class="form-label">Password</label>
              <input type="password" class="form-control" id="userPassword" name="password" required>
            </div>
            <div class="mb-3">
              <label for="userRole" class="form-label">Role</label>
              <select class="form-select" id="userRole" name="role" required>
                <option value="user">User</option>
                <option value="admin">Admin</option>
              </select>
            </div>
            <div class="modal-footer">
              <button type="submit" class="btn btn-primary" id="addUserButton">Add User</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>

</div>

<!-- Thêm CSS Bootstrap -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- Thêm jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Thêm JS Bootstrap -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
// Hàm lọc người dùng theo vai trò
function filterUsers() {
  var role = $('#filterRole').val();
  
  $.ajax({
    url: 'adminView/loadUser.php',   // file PHP để load danh sách theo role
    type: 'POST',
    data: { role: role },
    success: function(response) {
      $('#userTableBody').html(response);
    }
  });

  

}

$('#addUserForm').submit(function(e) {
  e.preventDefault();

  var name = $('#userName').val();
  var email = $('#userEmail').val();
  var password = $('#userPassword').val();
  var role = $('#userRole').val();

  $.ajax({
    url: 'adminView/addUser.php',
    method: 'POST',
    data: { name: name, email: email, password: password, role: role },
    success: function(response) {
      alert(response);  // Có thể sửa thành JSON nếu muốn

      // 1. Đóng modal bằng JavaScript Bootstrap
      const modalElement = document.getElementById('addUserModal');
      const modal = bootstrap.Modal.getInstance(modalElement);
      if (modal) modal.hide();

      // 2. Reset form
      $('#addUserForm')[0].reset();

      // 3. Gọi lại danh sách người dùng
      filterUsers();

      // 4. Đảm bảo backdrop và scroll được xử lý
      setTimeout(() => {
        $('.modal-backdrop').remove();
        $('body').removeClass('modal-open');
        $('body').css({ 'overflow': 'auto', 'padding-right': '' });
      }, 500); // chờ cho modal hide xong rồi mới xử lý
    },
    error: function(xhr, status, error) {
      alert('Có lỗi xảy ra: ' + error);
    }
  });
});


// Khi load trang lần đầu cũng gọi luôn
$(document).ready(function() {
  filterUsers();
});
</script>
