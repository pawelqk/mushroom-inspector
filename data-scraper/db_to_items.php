<?php

$dbFilename = "mushrooms.db";
try {
    $pdo = new \PDO("sqlite:" . $dbFilename);
} catch (\PDOException $e) {
    exit(1);
}
if ($pdo == null) {
    exit(1);
}

$distinctIDs = "SELECT DISTINCT id FROM mushrooms_search_result;";


$wikidataUrl = "https://www.wikidata.org/wiki/Special:EntityData/%s.json";

$options = [
    'http'=>[
      'method'=>"GET",
      'header'=> "User-Agent: MushroomBot 1.0\r\n"
    ],
];
  
$context = stream_context_create($options);

$targetFileName = "shroom_data.jsonl";

$targetFile = fopen($targetFileName, 'w');
if (!$targetFile) {
    fclose($sourceFile);
    exit(1);
}


foreach ($pdo->query($distinctIDs) as $row) {
    $name = trim($row['id']);
    echo $name . PHP_EOL;
    $query = urlencode($name);
    
    $response = file_get_contents(sprintf($wikidataUrl, $query), false, $context);
    $responseObj = json_decode($response);
    $result = json_encode($responseObj);
    fwrite($targetFile, $result . "\n");
    usleep(2*100*1000); //200ms
}

fclose($targetFile);
