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

$createTable = file_get_contents("create_table.sql");
$pdo->exec($createTable);


$sourceFileName = "data.jsonl";
$sourceFile = fopen($sourceFileName, "r");
if (!$sourceFile) {
    exit(1);
}


$insertSQL = "INSERT INTO mushrooms_search_result(id,title,pageid,repository,url,concepturi,label,description,aliases0) VALUES (?,?,?,?,?,?,?,?,?);";

$pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES,true);
$statement = $pdo->prepare($insertSQL);

while (($line = fgets($sourceFile)) !== false) {
    $json = trim($line);
    $responseObj = json_decode($json, true);
    foreach($responseObj['search'] as $result){
        $statement->execute([
            $result['id'] ?? null,
            $result['title'] ?? null,
            $result['pageid'] ?? null,
            $result['repository'] ?? null,
            $result['url'] ?? null,
            $result['concepturi'] ?? null,
            $result['label'] ?? null,
            $result['description'] ?? null,
            $result['aliases0'] ?? null,
        ]);

    }
}
fclose($sourceFile);

