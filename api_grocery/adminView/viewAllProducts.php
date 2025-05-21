<div>
  <h2>Product Items</h2>
  <table class="table">
    <thead>
      <tr>
        <th class="text-center">S.N.</th>
        <th class="text-center">Image</th>
        <th class="text-center">Name</th>
        <th class="text-center">Barcode</th>
        <th class="text-center">Description</th>
        <th class="text-center">Is Private</th>
        <th class="text-center" colspan="2">Action</th>
      </tr>
    </thead>
    <?php
      include_once "../connection.php";
      $sql = "SELECT * FROM products";
      $result = $connectNow->query($sql);
      $count = 1;
      if ($result->num_rows > 0) {
          while ($row = $result->fetch_assoc()) {
    ?>
    <tr>
      <td class="text-center"><?=$count?></td>
      <td class="text-center"><img height='100px' src='<?=$row["img"]?>'></td>
      <td class="text-center"><?=$row["name"]?></td>
      <td class="text-center"><?=$row["barcode"]?></td>
      <td class="text-center"><?=$row["description"]?></td>      
      <td class="text-center"><?=$row["is_private"] == 1 ? "Yes" : "No"?></td>     
      <td><button class="btn btn-primary" style="height:40px" onclick="itemEditForm('<?=$row['id']?>')">Edit</button></td>
      <td><button class="btn btn-danger" style="height:40px" onclick="itemDelete('<?=$row['id']?>')">Delete</button></td>
    </tr>
    <?php
            $count++;
          }
        }
    ?>
  </table>
  
  <!-- Button to trigger modal -->
<button type="button" class="btn btn-secondary" style="height:40px" data-bs-toggle="modal" data-bs-target="#addProductModal">
  Add Product
</button>

<!-- Modal Add Product -->
<div class="modal fade" id="addProductModal" tabindex="-1" aria-labelledby="addProductModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="addProductModalLabel">Add New Product</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <form id="addProductForm" method="POST" enctype="multipart/form-data">
          <div class="mb-3">
            <label for="productName" class="form-label">Tên</label>
            <input type="text" class="form-control" id="productName" name="p_name" required>
          </div>
          <div class="mb-3">
            <label for="productBarcode" class="form-label">Barcode</label>
            <input type="text" class="form-control" id="productBarcode" name="p_barcode" required>
          </div>
          <div class="mb-3">
            <label for="productDescription" class="form-label">Mô tả</label>
            <textarea class="form-control" id="productDescription" name="p_desc" rows="3" required></textarea>
          </div>
          <div class="mb-3">
            <label for="productImage" class="form-label">Hình ảnh</label>
            <input type="file" class="form-control" id="productImage" name="p_img">
          </div>
          <div class="mb-3">
            <label for="productPrivate" class="form-label">Ẩn</label>
            <select class="form-select" id="productPrivate" name="p_private" required>
              <option value="0">Không</option>
            </select>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            <button type="submit" class="btn btn-primary">Add Product</button>
          </div>
        </form>
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
$('#addProductForm').submit(function(e) {
  e.preventDefault();

  var formData = new FormData(this);

  $.ajax({
    url: 'adminView/addProduct.php',
    method: 'POST',
    data: formData,
    contentType: false,
    processData: false,
    success: function(response) {
      alert(response);

      // Ẩn modal
      const modalElement = document.getElementById('addProductModal');
      const modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
      modal.hide();

      // Reset form
      $('#addProductForm')[0].reset();

      // Xóa lớp nền modal và khôi phục cuộn
      setTimeout(() => {
        $('.modal-backdrop').remove();              // xóa lớp nền
        $('body').removeClass('modal-open');        // xóa class giữ trạng thái modal
        $('body').css({ 'overflow': 'auto', 'padding-right': '' }); // bật scroll và xóa padding
      }, 500);

      // Reload lại danh sách sản phẩm
      showProductItems();
    }
  });
});
</script>


</div>
