<html><head></head><body><?php

    /*

if ($handle = opendir('.')) {
    while (false !== ($file = readdir($handle)))
    {
        if ($file != "." && $file != ".." && strtolower(substr($file, strrpos($file, '.') + 1)) == 'jpg')
        {
            $thelist .= '<img src="'.$file.'"/>';
        }
    }
    closedir($handle);
}
     */
    
    $files = glob('/home/lnanek/server.neatocode.com/scanscope/*/*.jpg');
    
    foreach ($files as $filename) {
        
        $path = explode('/',$filename);
        
        $lastDir = array_pop($path);
        $lastDir = array_pop($path);
        
        echo '<img src="' . $lastDir . '/' . basename($filename) . '"/>';
    }
    

?></body></html>