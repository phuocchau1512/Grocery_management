<?php
include_once "../connection.php";

$role = $_POST['role'] ?? 'all'; // nếu chưa có role, mặc định là all

$where = "";
if ($role == 'user') {
    $where = "WHERE is_admin = 0";
} else if ($role == 'admin') {
    $where = "WHERE is_admin = 1";
}

$sql = "SELECT * FROM users $where";
$result = $connectNow->query($sql);
$count = 1;

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
?>
<tr>
  <td class="text-center"><?= $count ?></td>
  <td class="text-center"><?= htmlspecialchars($row["name"]) ?></td>
  <td class="text-center"><?= htmlspecialchars($row["email"]) ?></td>
  <td class="text-center"><?= $row["is_admin"] == 1 ? "Admin" : "User" ?></td>
  <td><button class="btn btn-danger" onclick="deleteUser('<?= $row['id'] ?>')">Delete</button></td>
</tr>
<?php
    $count++;
    }
} else {
    echo "<tr><td colspan='6' class='text-center'>No users found.</td></tr>";
}
?>
