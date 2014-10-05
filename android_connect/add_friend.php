<?php

	include_once('/../includes/db_connect.inc.php');

	$stmt = $mysqli->prepare("INSERT INTO friends(sender, receiver) VALUES (?, ?)");
	if($stmt === false)
	{
		echo "error in sql";
	}
	$stmt->bind_param('ss', $_POST['sender'], $_POST['receiver']);
	if($stmt->execute())
	{
		$result["success"] = 1;
		$result["message"] = "Friend request added to database!";
		echo json_encode ( $result );
	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "Failed to write to database";
		echo json_encode ( $result );
	}		
	$mysqli->close();
	
?>