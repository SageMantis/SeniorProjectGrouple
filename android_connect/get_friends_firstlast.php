<?php

	include_once('/../includes/db_connect.inc.php');

	$stmt = $mysqli->prepare("SELECT first, last FROM users u INNER JOIN friends f ON f.sender=u.email WHERE f.receiver= ? AND rec_date IS NOT NULL
	UNION SELECT first, last FROM users u INNER JOIN friends f ON f.receiver=u.email WHERE f.sender= ? AND rec_date IS NOT NULL");
	$stmt->bind_param('ss', $_GET['email'], $_GET['email']);
	$stmt->execute();
	$stmt->bind_result($first,$last);
	$stmt->store_result();
	$row_cnt = $stmt->num_rows;

	if($row_cnt > 0)
	{	
		$result = array();
		$response["success"] = 1;
		$response["friends"] = array();	

		while($stmt->fetch())
		{
			$result["first"] = $first;
			$result["last"] = $last;
			array_push($response["friends"], $result);
		}
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No friends found!"; 
	}

	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
	
	

?>
