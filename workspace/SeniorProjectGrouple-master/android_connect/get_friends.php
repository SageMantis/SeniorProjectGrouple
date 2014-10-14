<?php

	include_once('/../includes/db_connect.inc.php');

	$stmt = $mysqli->prepare("SELECT sender FROM friends WHERE receiver = ? AND rec_date is not NULL
UNION SELECT receiver FROM friends WHERE sender = ? AND rec_date is not NULL");
	$stmt->bind_param('ss', $_GET['email'], $_GET['email']);
	$stmt->execute();
	$stmt->bind_result($friend);
	$stmt->store_result();

	$result = array();
	$row_cnt = $stmt->num_rows;

	$response = array();
	if($row_cnt > 0)
	{
		while($stmt->fetch())
		{
			$result[]= $friend;
		}
		$response["success"] = 1;
		$response["friends"] = array();
		array_push($response["friends"], $result); 
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No friends found for that username!"; 
	}

	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
	
	

?>
