<?php

// Saves uploads as web accessible JPG files named by their SHA1 hash.

$BASE_DIR = "/home/lnanek/nfscope.com/";
$UPLOAD_DIR = "uploads";
$UPLOAD_EXTENSION = ".jpg";
    
    $request_board = preg_replace("/[^A-Za-z0-9]/",'',$_REQUEST['board']);
    if ( !$request_board ) {
        $request_board = $UPLOAD_DIR;
    }
    
    
    $request_email = $_REQUEST['email'];
    
    $target_dir = $BASE_DIR . $request_board;
    
    if ( !file_exists($target_dir) ) {
        mkdir($target_dir, 0775);
    }
    

//echo 'Uploaded file is: ' . $_FILES['uploadedfile']['name'] . "\n";
//echo 'Temp upload on server is: ' . $_FILES['uploadedfile']['tmp_name'] . "\n";
//echo 'FILES: ' . print_r($_FILES);

// Confirm we got an upload.
$upload_set = isset($_FILES["uploadedfile"]["tmp_name"]);
$upload_exists = file_exists($_FILES["uploadedfile"]["tmp_name"]);
if ( !$upload_set || !$upload_exists ) {
	header('HTTP/1.0 400 Bad Request', true, 400);
	echo 'No uploaded file.';
	exit(1);
}

// Get hash and check if exists already.
$fileHash = sha1_file($_FILES['uploadedfile']['tmp_name']);
$fileDestination = $target_dir . '/' . $fileHash . $UPLOAD_EXTENSION;
$destination_exists = file_exists($fileDestination);

//echo 'dest: ' . $fileDestination . "\n\n";;

// Save it if doesn't exist already.
if ( !$destination_exists ) {
	$moved = move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $fileDestination);
	if ( !$moved ) {
		header('HTTP/1.0 500 Server Error', true, 500);
		echo 'Error saving upload.';
		exit(1);
	}
}

	// Let client know where it is stored.
	//echo "http://nfscope.com/" . $request_board . '/' . $fileHash . $UPLOAD_EXTENSION;
	echo "http://nfscope.com/?board=" . $request_board;

?>