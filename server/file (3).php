<?php


$image64 = $_POST['image'] ?? '';
file_put_contents('debug.txt', $image64);


$decodedImage = base64_decode($image64);

if (file_put_contents('images/my_file.jpg', $decodedImage)) {
    echo 'Image Upload Successful';
} else {
    echo 'Image Upload Error';
}
?> 