<html>
    <head>
        <title>NFScope - Hyperlocal across time!</title>
        <meta http-equiv="refresh" content="10">
    </head>
    <body>
    <?php
        
        $BASE_DIR = "/home/lnanek/nfscope.com/";
    
        $request_board = preg_replace("/[^A-Za-z0-9]/",'',$_REQUEST['board']);
        $files = array();
        if ( !$request_board ) {
            $files = glob('/home/lnanek/nfscope.com/*/*.jpg');
            ?>
                <h1><center> pics from scanning </center></h1>
            <?php
        } else {
            $files = glob($BASE_DIR . $request_board . '/*.jpg');
            ?>
                <h1><center> pics from scanning <?= $request_board ?> </center></h1>
            <?php
        }
                
        if ( 0 == count($files) ) {
            ?>
                <h2> No one yet. Be the first! </h2>
            <?php
    
        }
    
    
        foreach ($files as $filename) {
            
            $path = explode('/',$filename);
            $lastDir = array_pop($path);
            $lastDir = array_pop($path);
            
            $link = $lastDir . '/' . basename($filename);
        
            echo '<a href="' . $link . '"><img src="' . $link . '" width="50" /></a>';
        }

    ?>
    </body>

</html>