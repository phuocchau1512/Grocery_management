<?php
$serverHost = "localhost";
$user = "root";
$pass = "";
$database = "grocery_db";

$connectNow = new mysqli($serverHost,$user,$pass,$database);

if (!$connectNow) {
    die("Kết nối thất bại: " . mysqli_connect_error());
}

?>