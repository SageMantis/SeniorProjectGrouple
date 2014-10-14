<?php

	include_once('/../includes/db_connect.inc.php');

	$stmt = $mysqli->prepare("SELECT first,last FROM users WHERE email = ?");
	$stmt->bind_param('s', $_GET['email']);
	$stmt->execute();
	$stmt->bind_result($first,$last);
	$stmt->store_result();

	$result = array();
	$row_cnt = $stmt->num_rows;

	$response = array();
	if($row_cnt > 0)
	{
		while($stmt->fetch())
		{
			$result[]=array($first,$last);
		}
		$response["success"] = 1;
		$response["users"] = array();
		array_push($response["users"], $result); 
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No users found"; 
	}

	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
	
	

?>