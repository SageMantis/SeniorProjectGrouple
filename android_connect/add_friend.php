<?php

	include_once('/../includes/db_connect.inc.php');

	$stmt = $mysqli->prepare("INSERT INTO friends(sender, receiver) VALUES (?, ?)");
	$stmt->bind_param('ss', $_POST['sender'], $_POST['receiver']);
	$checkPending = $mysqli->prepare("SELECT COUNT(*) FROM FRIENDS WHERE sender = ? and receiver = ? AND REC_DATE IS NULL");
	$checkPending->bind_param('ss', $_POST['sender'], $_POST['receiver']);
	$checkFriends = $mysqli->prepare("SELECT sender FROM FRIENDS WHERE sender = ? and receiver = ? AND REC_DATE IS NOT NULL UNION SELECT sender FROM FRIENDS WHERE sender = ? and receiver = ? AND REC_DATE IS NOT NULL");
	$checkFriends->bind_param('ssss', $_POST['sender'], $_POST['receiver'], $_POST['receiver'], $_POST['sender']);

	#check if user is attempting to friend self
	$r = $_POST['receiver'];
	$s = $_POST['sender'];
	if (strcmp($r, $s) == 0) {
    		$result["success"] = 4;
		$result["message"] = "You can't friend request yourself!";
		echo json_encode ( $result );
	}
	else
	{
		#check if there is already a pending friend request
		if($checkPending->execute())
		{
			$res = $checkPending->get_result();
			$row = $res->fetch_row();
			if($row[0] == 1)
			{
				$result["success"] = 3;
				$result["message"] = "A friend request to that user is already pending.";
				echo json_encode($result);
			}
			#check if they are already friends
			else 
			{
				if($checkFriends->execute())
				{
					$res = $checkFriends->get_result();
					$row_cnt = $res->num_rows;
					if($row_cnt == 1)
					{
						$result["success"] = 2;
						$result["message"] = "You are already friends with that user.";
						echo json_encode($result);
					}
					#proceed with friend request
					else
					{
						if($stmt->execute())
						{
							$result["success"] = 1;
							$result["message"] = "Friend request sent!";
							echo json_encode ( $result );
						}
						else
						{
							$result["success"] = 0;
							$result["message"] = "That user does not exist!";
							echo json_encode ( $result );
						}
					}
	
				}
				else
				{
					$result["success"] = 0;
					$result["message"] = "Unable to process request.";
					echo json_encode ( $result );
				}
			}
		}	
		else
		{
			$result["success"] = 0;
			$result["message"] = "Unable to process request.";
			echo json_encode ( $result );
		}		
	}

	$mysqli->close();
?>