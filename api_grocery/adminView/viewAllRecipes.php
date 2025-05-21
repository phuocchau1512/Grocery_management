<div class="container mt-4">
  <h2 class="mb-3">Recipe List</h2>
  
  <!-- Nút thêm công thức mới -->
  <button type="button" class="btn btn-success mb-3" style="height:40px" data-bs-toggle="modal" data-bs-target="#addRecipeModal">
    Add Recipe
  </button>
  
  <table class="table table-bordered table-striped">
    <thead class="table-dark">
      <tr>
        <th class="text-center">#</th>
        <th class="text-center">Title</th>
        <th class="text-center">Ingredients</th>
        <th class="text-center">Instructions</th>
        <th class="text-center">Image</th>
        <th class="text-center">Likes</th>
        <th class="text-center">Time (min)</th>
        <th class="text-center">Created By</th>
        <th class="text-center" colspan="2">Action</th>
      </tr>
    </thead>
    <tbody>
      <?php
        include_once "../connection.php";

        $sql = "SELECT r.*, 
                       CASE 
                         WHEN r.user_id = 0 THEN 'Admin'
                         ELSE u.name 
                       END AS creator
                FROM recipes r
                LEFT JOIN users u ON r.user_id = u.id";
                
        $result = $connectNow->query($sql);
        $count = 1;

        if ($result && $result->num_rows > 0) {
          while ($row = $result->fetch_assoc()) {
      ?>
      <tr>
        <td class="text-center"><?= $count++ ?></td>
        <td><?= htmlspecialchars($row["title"]) ?></td>
        <td><?= nl2br(htmlspecialchars($row["ingredients"])) ?></td>
        <td><?= nl2br(htmlspecialchars($row["instructions"])) ?></td>
        <td class="text-center">
          <?php if (!empty($row["img"])): ?>
            <img src="<?= htmlspecialchars($row["img"]) ?>" alt="Recipe Image" height="100">
          <?php else: ?>
            <em>No image</em>
          <?php endif; ?>
        </td>
        <td class="text-center"><?= $row["likes"] ?></td>
        <td class="text-center"><?= $row["time_minutes"] ?></td>
        <td class="text-center"><?= htmlspecialchars($row["creator"]) ?></td>
        <td class="text-center">
          <button class="btn btn-primary" style="height:40px" onclick="editRecipes('<?=$row['id']?>')">Edit</button>
        </td>
        <td class="text-center">
          <button class="btn btn-danger" style="height:40px" onclick="deleteRecipes('<?=$row['id']?>')">Delete</button>
        </td>
      </tr>
      <?php 
          }
        } else {
          echo '<tr><td colspan="9" class="text-center">No recipes found.</td></tr>';
        }
      ?>
    </tbody>
  </table>
</div>



<!-- Modal Add Recipe -->
<div class="modal fade" id="addRecipeModal" tabindex="-1" aria-labelledby="addRecipeModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="addRecipeModalLabel">Add New Recipe</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <form id="addRecipeForm" method="POST" enctype="multipart/form-data">
          <div class="mb-3">
            <label for="recipeTitle" class="form-label">Title</label>
            <input type="text" class="form-control" id="recipeTitle" name="title" required>
          </div>
          <div class="mb-3">
            <label for="recipeIngredients" class="form-label">Ingredients</label>
            <textarea class="form-control" id="recipeIngredients" name="ingredients" rows="3" required></textarea>
          </div>
          <div class="mb-3">
            <label for="recipeInstructions" class="form-label">Instructions</label>
            <textarea class="form-control" id="recipeInstructions" name="instructions" rows="3" required></textarea>
          </div>
          <div class="mb-3">
            <label for="recipeImage" class="form-label">Image</label>
            <input type="file" class="form-control" id="r_img" name="r_img">
          </div>
          <div class="mb-3">
            <label for="recipeTime" class="form-label">Time (minutes)</label>
            <input type="number" class="form-control" id="recipeTime" name="time_minutes" required>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            <button type="submit" class="btn btn-primary">Add Recipe</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>

<!-- Bootstrap CDN nếu chưa có -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

<script>

$('#addRecipeForm').submit(function(e) {
  e.preventDefault();

  var formData = new FormData(this);

  $.ajax({
    url: 'adminView/addRecipe.php',
    method: 'POST',
    data: formData,
    contentType: false,
    processData: false,
    success: function(response) {
      alert(response);

      const modalElement = document.getElementById('addRecipeModal');
      const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
      modal.hide();

      // Xóa backdrop và khôi phục scroll
      $('body').removeClass('modal-open');
      $('.modal-backdrop').remove();

      $('#addRecipeForm')[0].reset();
      setTimeout(() => {
        $('body').removeClass('modal-open');
        $('.modal-backdrop').remove();
        $('body').css('overflow', 'auto'); // Cho phép cuộn lại
      }, 500);
      showRecipes(); // làm mới danh sách công thức
    }
  });
});



</script>