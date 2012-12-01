<!DOCTYPE html>
<html>

    <head>
        <meta charset='UTF-8'>
        <meta http-equiv="refresh" content="10">

        <?php
    
            $BASE_DIR = "/home/lnanek/nfscope.com/";
    
            $request_board = preg_replace("/[^A-Za-z0-9]/",'',$_REQUEST['board']);
            $files = array();
            if ( !$request_board ) {
                $files = glob('/home/lnanek/nfscope.com/*/*.jpg');
                ?>
                    <title>Everything</title>
                <?php
            } else {
                $files = glob($BASE_DIR . $request_board . '/*.jpg');
                ?>
                    <title><?= $request_board ?></title>
                <?php
            }
                ?>

        <link rel='stylesheet' href='css/style.css'>
</head>

<body>
<?php
    
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
            
            echo '<img src="' . $link . '" />';
        }
        
        ?>


</section>

</body>

</html>