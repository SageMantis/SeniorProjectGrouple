<?php

	include_once('/../includes/db_connect.inc.php');

	$stmt = $mysqli->prepare("SELECT COUNT(*) FROM users WHERE email = ? AND password = ?");
	$stmt->bind_param('ss', $_GET['email'], $_GET['password']);
	$stmt->execute();
	$result = $stmt->get_result();
	$row = $result->fetch_row();

	header('Content-type: application/json');
	if($row[0] == 0)
	{
		$response["success"] = $row[0];
		$response["message"] = "Login or password incorrect!";
		echo json_encode ( $response );
	}
	else
	{
		$response["success"] = $row[0];
		$response["message"] = "Successful Login!";
		echo json_encode ( $response );
	}	

	$mysqli->close();
	
	

?>