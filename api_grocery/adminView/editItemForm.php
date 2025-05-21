<?php
include_once "../connection.php";

// Xử lý khi nhận dữ liệu form và tìm sản phẩm
if (isset($_POST['record'])) {
    $ID = intval($_POST['record']);
    $qry = mysqli_query($connectNow, "SELECT * FROM products WHERE id='$ID'");
    $numberOfRow = mysqli_num_rows($qry);

    if ($numberOfRow > 0) {
        while ($row1 = mysqli_fetch_array($qry)) {
            ?>
            <form id="updateForm" method="POST" enctype="multipart/form-data">
            <input type="hidden" name="product_id" value="<?= $row1['id'] ?>">

            <div class="form-group">
                <label for="name">Product Name:</label>
                <input type="text" class="form-control" name="p_name" value="<?= htmlspecialchars($row1['name']) ?>">
            </div>

            <div class="form-group">
                <label for="barcode">Barcode:</label>
                <input type="text" class="form-control" name="p_barcode" value="<?= htmlspecialchars($row1['barcode']) ?>">
            </div>

            <div class="form-group">
                <label for="desc">Product Description:</label>
                <textarea class="form-control" name="p_desc"><?= htmlspecialchars($row1['description']) ?></textarea>
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
                <label for="is_private">Private Product (1 for yes, 0 for no):</label>
                <input type="number" class="form-control" name="is_private" value="<?= $row1['is_private'] ?>">
            </div>

            <div class="form-group">
                <button type="button" id="updateBtn" class="btn btn-primary" style="height:40px;">Update Item</button>
            </div>
        </form>

        <script src="assets/js/jquery3.7.1.js"></script>
        <script>
            $(document).ready(function() {
                $('#updateBtn').click(function() {
                    var formData = new FormData($('#updateForm')[0]); // Lấy dữ liệu form

                    $.ajax({
                        url: 'adminView/update_product.php',  // File PHP xử lý cập nhật
                        type: 'POST',
                        data: formData,
                        contentType: false,
                        processData: false,
                        success: function(response) {
                            alert(response);  // Hiển thị kết quả trả về từ PHP
                            if (response === 'Cập nhật sản phẩm thành công!') { 
                                showProductItems();
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
        echo "<p>Không tìm thấy sản phẩm với ID: $ID</p>";
    }
}


?>