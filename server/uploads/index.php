<html><head></head><body><?php
    
    $files = glob('/home/lnanek/nfscope.com/uploads/*.jpg');
    
    foreach ($files as $filename) {
        echo '<img src="' . basename($filename) . '"/>';
    }
    

?></body></html>