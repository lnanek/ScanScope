<?php

// Saves uploads as web accessible JPG files named by their SHA1 hash.

$UPLOAD_DIR = "uploads/";
$UPLOAD_EXTENSION = ".jpg";

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
$fileDestination = $UPLOAD_DIR . $fileHash . $UPLOAD_EXTENSION;
$destination_exists = file_exists($fileDestination);

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
echo "http://server.neatocode.com/scanscope/uploads/" . $fileHash . $UPLOAD_EXTENSION;

?>