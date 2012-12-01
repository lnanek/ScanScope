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
    
    $files = glob('/home/lnanek/server.neatocode.com/scanscope/uploads/*.jpg');
    
    foreach ($files as $filename) {
        echo '<img src="' . basename($filename) . '"/>';
    }
    

?></body></html>