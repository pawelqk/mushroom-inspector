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

$createTable1 = "CREATE TABLE IF NOT EXISTS edibility_names(
    id             INTEGER PRIMARY KEY,
    name           VARCHAR(30) NOT NULL
 );";
$pdo->exec($createTable1);

$createTable2 = "CREATE TABLE IF NOT EXISTS mushroom_edibility(
    id           VARCHAR(14) NOT NULL PRIMARY KEY,
    edibility_id INTEGER
 ); ";
$pdo->exec($createTable2);


$sourceFileName = "shroom_data.jsonl";
$sourceFile = fopen($sourceFileName, "r");
if (!$sourceFile) {
    exit(1);
}

$x = 'INSERT INTO edibility_names (id, name) VALUES (1, "choice mushroom"), (2, "edible mushroom"),(3, "edible when cooked mushroom"),(4, "inedible mushroom"),(5, "caution mushroom"),(6, "poisonous mushroom"),(7, "psychoactive mushroom"),(8, "deadly mushroom"),(9, "inedible fungi"),(10, "unknown");';
$pdo->exec($x);

$pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES,true);

$insertSQL = "INSERT INTO mushroom_edibility(id,edibility_id) VALUES (?,?);";
$statement = $pdo->prepare($insertSQL);

while (($line = fgets($sourceFile)) !== false) {
    $json = trim($line);
    $responseObj = json_decode($json, true);
    foreach($responseObj as $result){
        foreach($result as $key => $value) {
            $claimNum = "";
            // print_r($value['id'] . ": " . ($value['claims']['P789'] ? "X" : "y") . "\n");
            if($value['claims']['P789'] != null) {
                $claimNum = "P789";                
            }
            if($value['claims']['P279'] != null) {
                $claimNum = "P279";                
            }
            if(!empty($claimNum)){
                $claimID = $value['claims'][$claimNum][0]['mainsnak']['datavalue']['value']['id'] ?? "";

                switch($claimID){
                    case "Q19888517":
                        echo "choice mushroom"; // grzyb jadalny najlepszy
                        $statement->execute([$value['id'] ?? null, 1]);
                        break;
                    case "Q654236":
                        echo "edible mushroom"; // grzyb jadalny
                        $statement->execute([$value['id'] ?? null, 2]);
                        break;
                    case "Q62102033":
                        echo "edible when cooked mushroom"; // grzyb jadalny po ugotowaniu
                        $statement->execute([$value['id'] ?? null, 3]);
                        break;
                    case "Q4317894":
                        echo "inedible mushroom"; // grzyb niejadalny
                        $statement->execute([$value['id'] ?? null, 4]);
                        break;
                    case "Q19888537":
                        echo "caution mushroom"; // grzyb z ostroznością
                        $statement->execute([$value['id'] ?? null, 5]);
                        break;
                    case "Q19888562":
                        echo "poisonous mushroom"; // grzyb trójący
                        $statement->execute([$value['id'] ?? null, 6]);
                        break;
                    case "Q1169875":
                        echo "psychoactive mushroom"; // grzyb psychoaktywny
                        $statement->execute([$value['id'] ?? null, 7]);
                        break;
                    case "Q19888591":
                        echo "deadly mushroom"; // grzyb zabójczy
                        $statement->execute([$value['id'] ?? null, 8]);
                        break;
                    case "Q8548454":
                        echo "inedible fungi"; // grzyby niejadalne (temat)
                        $statement->execute([$value['id'] ?? null, 9]);
                        break;
                    default:
                        // echo "unknown: " . $claimID;
                        $statement->execute([$value['id'] ?? null, 10]);
                        break;
                }
                echo "\n";
            }
        }
    }
}
fclose($sourceFile);
