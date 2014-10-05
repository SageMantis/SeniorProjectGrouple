<?php

	include_once('/../includes/db_connect.inc.php');

	$stmt = $mysqli->prepare("SELECT sender FROM friends WHERE receiver = ? AND rec_date is NULL");
	$stmt->bind_param('s', $_GET['receiver']);
	$stmt->execute();
	$stmt->bind_result($sender);
	$stmt->store_result();

	$result = array();
	$row_cnt = $stmt->num_rows;

	$response = array();
	if($row_cnt > 0)
	{
		while($stmt->fetch())
		{
			$result[]= $sender;
		}
		$response["success"] = 1;
		$response["senders"] = array();
		array_push($response["senders"], $result); 
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No friend requests found for that username!"; 
	}

	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
	
	

?>
