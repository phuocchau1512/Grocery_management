

function showProductItems(){  
    $.ajax({
        url:"./adminView/viewAllProducts.php",
        method:"post",
        data:{record:1},
        success:function(data){
            $('.allContent-section').html(data);
        }
    });
}

function showUsers(){
    $.ajax({
        url:"./adminView/viewAllUsers.php",
        method:"post",
        data:{record:1},
        success:function(data){
            $('.allContent-section').html(data);
        }
    });
}


function showRecipes(){
    $.ajax({
        url:"./adminView/viewAllRecipes.php",
        method:"post",
        data:{record:1},
        success:function(data){
            $('.allContent-section').html(data);
        }
    });
}



function addItems() {
    $.ajax({
        url:"./adminView/addItemForm.php",
        method:"post",
        success:function(data){
            alert('Items Successfully deleted');
            $('form').trigger('reset');
            showProductItems();
        }
    });
}

function itemEditForm(id){
    $.ajax({
        url:"./adminView/editItemForm.php",
        method:"post",
        data:{record:id},
        success:function(data){
            $('.allContent-section').html(data);
        }
    });
}


//delete product data
function itemDelete(id){
    $.ajax({
        url:"adminView/deleteItemController.php",
        method:"post",
        data:{record:id},
        success:function(data){
            alert('Items Successfully deleted');
            $('form').trigger('reset');
            showProductItems();
        }
    });
}



function deleteUser(id){
    $.ajax({
        url:"adminView/deleteUserController.php",
        method:"post",
        data:{record:id},
        success:function(data){
            alert('Xóa user thành công!');
            $('form').trigger('reset');
            showUsers();
        }
    });
}

function editRecipes(id){
    $.ajax({
        url:"./adminView/editRecipesForm.php",
        method:"post",
        data:{record:id},
        success:function(data){
            $('.allContent-section').html(data);
        }
    });
}

function deleteRecipes(id){
    $.ajax({
        url:"adminView/deleteRecipeController.php",
        method:"post",
        data:{record:id},
        success:function(data){
            alert('Xóa thành công!');
            $('form').trigger('reset');
            showRecipes();
        }
    });
}
