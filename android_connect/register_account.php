<?php

	include_once('/../includes/db_connect.inc.php');

	#Validate E-mail
	#Source: http://www.w3schools.com/php/php_form_url_email.asp
	$email = $_POST['email'];
	if(!filter_var($email, FILTER_VALIDATE_EMAIL))
	{
		$result["success"] = 3;
		$result["message"] = "Invalid email format";
		echo json_encode ( $result );
		$mysqli->close();
		exit();
	}
	
	#Validate password min/max length 
	$password = $_POST['password'];
	if(strlen($password) < 4 || strlen($password) > 24)
	{
		$result["success"] = 3;
		$result["message"] = "Invalid password length";
		echo json_encode ( $result );
		$mysqli->close();
		exit();
	}

	$stmt = $mysqli->prepare("SELECT COUNT(*) FROM users WHERE email = ?");
	$stmt->bind_param('s', $_POST['email']);
	$stmt->execute();
	$response = $stmt->get_result();
	$row = $response->fetch_row();

	header('Content-type: application/json');
	if($row[0] == 0)
	{

		$password_hash = password_hash($_POST['password'], PASSWORD_DEFAULT);
		if($password_hash == false)
		{
			$result["success"] = 0;
			$result["message"] = "Internal server problem!  Please inform admin.";
			echo json_encode ( $result );
			exit();
		}
		$stmt = $mysqli->prepare("INSERT INTO users(email, password, first, last) VALUES (?, ?, ?, ?)");
		if($stmt === false)
		{
			$result["success"] = 0;
			$result["message"] = "Internal server problem!  Please inform admin.";
			echo json_encode ( $result );
			exit();
		}

		$stmt->bind_param('ssss', $_POST['email'], $password_hash, $_POST['first'], $_POST['last']);
		if($stmt->execute())
		{
			$result["success"] = 1;
			$result["message"] = "Account registered successfully!";
			echo json_encode ( $result );
		}
		else
		{
			$result["success"] = 0;
			$result["message"] = "Unable to create account!";
		echo json_encode ( $result );
		}		
	}
	else
	{
		$result["success"] = 2;
		$result["message"] = "Email address already registered!";
		echo json_encode ( $result );
	}	


	$mysqli->close();
	
?>