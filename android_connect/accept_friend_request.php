<?php

	include_once('/../includes/db_connect.inc.php');

	$stmt = $mysqli->prepare("UPDATE friends SET rec_date = CURRENT_TIMESTAMP where sender = ? AND receiver = ?");
	$removeRequests = $mysqli->prepare("DELETE from friends WHERE sender = ? AND receiver = ? AND rec_date IS NULL");
	$stmt->bind_param('ss', $_POST['sender'], $_POST['receiver']);
	$removeRequests->bind_param('ss', $_POST['receiver'], $_POST['sender']);
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$result["success"] = 1;
			$result["message"] = "Friend request accepted!";
			echo json_encode ( $result );
			$removeRequests->execute();
		}
		else
		{
			$result["success"] = 0;
			$result["message"] = "Friend request was not found!";
			echo json_encode ( $result );
		}
	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "Failed to write to database";
		echo json_encode ( $result );
	}		
	

	$mysqli->close();
	
?>