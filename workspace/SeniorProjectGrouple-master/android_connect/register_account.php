<?php

	include_once('/../includes/db_connect.inc.php');

	$stmt = $mysqli->prepare("INSERT INTO users(email, password, first, last) VALUES (?, ?, ?, ?)");
	if($stmt === false)
	{
		echo "error in sql";
	}
	$stmt->bind_param('ssss', $_POST['email'], $_POST['password'], $_POST['first'], $_POST['last']);
	if($stmt->execute())
	{
		$result["success"] = 1;
		$result["message"] = "User added to database!";
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