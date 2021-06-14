<?php

$wikidataUrl = "https://www.wikidata.org/w/api.php?action=wbsearchentities&format=json&language=en&search=%s";

$options = [
    'http'=>[
      'method'=>"GET",
      'header'=> "User-Agent: MushroomBot 1.0\r\n"
    ],
];
  
$context = stream_context_create($options);

$sourceFileName = "labels.txt";
$targetFileName = "search_result.jsonl";

$sourceFile = fopen($sourceFileName, "r");
if (!$sourceFile) {
    exit(1);
}

$targetFile = fopen($targetFileName, 'w');
if (!$targetFile) {
    fclose($sourceFile);
    exit(1);
}

while (($line = fgets($sourceFile)) !== false) {
    $name = trim($line);
    $query = urlencode($name);
    
    $response = file_get_contents(sprintf($wikidataUrl, $query), false, $context);
    $responseObj = json_decode($response);
    $result = json_encode($responseObj);
    fwrite($targetFile, $result . "\n");
    //usleep(2*1000*1000); //2sec
    usleep(2*100*1000); //200ms -> max 5 req. / sec (wikidata limit)
}

fclose($targetFile);
fclose($sourceFile);

