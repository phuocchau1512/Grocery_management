<?php
include_once "../connection.php";

// Xử lý khi nhận dữ liệu form và tìm công thức (recipe)
if (isset($_POST['record'])) {
    $ID = intval($_POST['record']);
    $qry = mysqli_query($connectNow, "SELECT * FROM recipes WHERE id='$ID'");
    $numberOfRow = mysqli_num_rows($qry);

    if ($numberOfRow > 0) {
        while ($row1 = mysqli_fetch_array($qry)) {
            ?>
            <form id="updateForm" method="POST" enctype="multipart/form-data">
            <input type="hidden" name="recipe_id" value="<?= $row1['id'] ?>">

            <div class="form-group">
                <label for="title">Recipe Title:</label>
                <input type="text" class="form-control" name="r_title" value="<?= htmlspecialchars($row1['title']) ?>">
            </div>

            <div class="form-group">
                <label for="ingredients">Ingredients:</label>
                <textarea class="form-control" name="r_ingredients"><?= htmlspecialchars($row1['ingredients']) ?></textarea>
            </div>

            <div class="form-group">
                <label for="instructions">Instructions:</label>
                <textarea class="form-control" name="r_instructions"><?= htmlspecialchars($row1['instructions']) ?></textarea>
            </div>

            <div class="form-group">
                <label>Current Image:</label><br>
                <img width="200px" height="150px" src="<?= htmlspecialchars($row1["img"]) ?>"><br>
                <input type="hidden" name="existingImage" value="<?= htmlspecialchars($row1['img']) ?>">
            </div>

            <div class="form-group">
                <label for="file">Choose New Image (optional):</label>
                <input type="file" class="form-control" name="newImage">
            </div>

            <div class="form-group">
                <label for="likes">Likes:</label>
                <input type="number" class="form-control" name="r_likes" value="<?= $row1['likes'] ?>">
            </div>

            <div class="form-group">
                <label for="time_minutes">Time (minutes):</label>
                <input type="number" class="form-control" name="r_time" value="<?= $row1['time_minutes'] ?>">
            </div>

            <div class="form-group">
                <button type="button" id="updateBtn" class="btn btn-primary" style="height:40px;">Update Recipe</button>
            </div>
        </form>

        <script src="assets/js/jquery3.7.1.js"></script>
        <script>
            $(document).ready(function() {
                $('#updateBtn').click(function() {
                    var formData = new FormData($('#updateForm')[0]);

                    $.ajax({
                        url: 'adminView/update_recipe.php',  // File PHP xử lý cập nhật công thức
                        type: 'POST',
                        data: formData,
                        contentType: false,
                        processData: false,
                        success: function(response) {
                            alert(response);
                            if (response === 'Cập nhật công thức thành công!') { 
                                showRecipes();
                            }
                        },
                        error: function(xhr, status, error) {
                            alert("Có lỗi xảy ra: " + error);
                        }
                    });
                });
            });
        </script>

            <?php
        }
    } else {
        echo "<p>Không tìm thấy công thức với ID: $ID</p>";
    }
}
?>
