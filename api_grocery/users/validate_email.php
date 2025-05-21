<?php
include '../connection.php';


$userEmail = $_POST['email'];


$query = "SELECT * FROM users WHERE email = '$userEmail'";

$result = $connectNow->query($query);

if ( $result->num_rows > 0 ){
    echo json_encode(array("emailFound"=> true) );
}
else {
    echo json_encode(array("emailFound"=> false));
}

?>