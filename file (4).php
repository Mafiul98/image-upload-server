<?php


$image64 = $_POST['image'] ?? '';
file_put_contents('debug.txt', $image64);
$decodedImage = base64_decode($image64);

$filename = time(). '_'.rand(1000,100000).'.jpg';
$filepath = 'images/'.$filename;


if (file_put_contents($filepath, $decodedImage)) {
    echo 'Image Upload Successful';
} else {
    echo 'Image Upload Error';
}
?> 