<!DOCTYPE html>
<html>

<head>
<meta charset='UTF-8'>
<meta http-equiv="refresh" content="10">
<title>NFScope - Hyperlocal across time!</title>

<link rel='stylesheet' href='css/style.css'>
</head>

<body>

<?php
    
    $BASE_DIR = "/home/lnanek/nfscope.com/";
    
    $request_board = preg_replace("/[^A-Za-z0-9]/",'',$_REQUEST['board']);
    $files = array();
    if ( !$request_board ) {
        $files = glob('/home/lnanek/nfscope.com/*/*.jpg');
        ?>
<h1><center> pics from scanning collages </center></h1>
<?php
    } else {
        $files = glob($BASE_DIR . $request_board . '/*.jpg');
        ?>
<h1><center> pics from scanning <?= $request_board ?> collage </center></h1>
<?php
    }
    
    if ( 0 == count($files) ) {
        ?>
<h2> <center> No one yet. <a href="http://play.google.com/store/apps/details?id=com.nfscope">Be the first!</a> </center> </h2>
<?php
    
    }
    
    ?>
<section id="photos"><?php
        foreach ($files as $filename) {
            
            $path = explode('/',$filename);
            $lastDir = array_pop($path);
            $lastDir = array_pop($path);
            
            $link = $lastDir . '/' . basename($filename);
            
            echo '<a href="' . $link . '"><img src="' . $link . '" width="50" /></a>';
        }
        
        ?>


</section>

</body>

</html>