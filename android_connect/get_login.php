<?php

	include_once('/../includes/db_connect.inc.php');

	$stmt = $mysqli->prepare("SELECT password FROM users WHERE email = ?");
	$stmt->bind_param('s', $_GET['email']);
	$stmt->execute();
	$result = $stmt->get_result();
	$row = $result->fetch_row();
	
	header('Content-type: application/json');
	if(mysqli_num_rows($result) < 1)
	{
		$response["success"] = 0;
		$response["message"] = "That email address is not registered!";
		echo json_encode ( $response );
	}
	else
	{
		
		if(password_verify($_GET['password'], $row[0]))
		{
			$response["success"] = 1;
			$response["message"] = "Successful Login!";
			echo json_encode ( $response );
		}
		else
		{
			$response["success"] = 0;
			$response["message"] = "Incorrect username or password!";
			echo json_encode ( $response );
		}
		
	}	

	$mysqli->close();
	
	

?>