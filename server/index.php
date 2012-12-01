<html><head></head><body><?php
    
    $files = glob('/home/lnanek/nfscope.com/*/*.jpg');
    
    foreach ($files as $filename) {
        
        $path = explode('/',$filename);
        
        $lastDir = array_pop($path);
        $lastDir = array_pop($path);
        
        echo '<img src="' . $lastDir . '/' . basename($filename) . '"/>';
    }
    

?></body></html>