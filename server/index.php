<html><head></head><body><?php
    
    
    $BASE_DIR = "/home/lnanek/nfscope.com/";
    
    $request_board = preg_replace("/[^A-Za-z0-9]/",'',$_REQUEST['board']);
    $files = array();
    if ( !$request_board ) {
        $files = glob('/home/lnanek/nfscope.com/*/*.jpg');
    } else {
        $files = glob($BASE_DIR . $request_board . '/*.jpg');
    }
    
    
    foreach ($files as $filename) {
        
        $path = explode('/',$filename);
        
        $lastDir = array_pop($path);
        $lastDir = array_pop($path);
        
        echo '<img src="' . $lastDir . '/' . basename($filename) . '" width="50" height="50" />';
    }
    

?></body></html>