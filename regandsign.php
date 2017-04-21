<?php

define(DBUSER, "passtrun_oop");
define(DBNAME, "passtrun_oop");
define(DBPASSWORD, "ICKKCK!0");
define(DBSERVER, "localhost");

$responseArray = array();

$mysqli_OOP = mysqli_connect(DBSERVER, DBUSER, DBPASSWORD, DBNAME);
if(mysqli_connect_errno($mysqli_OOP))
{
	$message = "Failed to Connect to the Database: " . mysqli_connect_error();
	
	sendResponse("error", $message, "");
}

	
if(isset($_POST['action']))
{
	
	$dataArray = array();
	$action = $_POST['action'];
	
	if($action == "register")
	{
		
		$dataArray['action'] = "register";
		$firstName = $_POST['fname'];
		$lastName = $_POST['lname'];
		$userName = $_POST['username'];
		$emailAddress = $_POST['emailAddress'];
		$usericon = intval($_POST['icon']);
		if(filter_var($emailAddress, FILTER_VALIDATE_EMAIL) === false){
			$dataArray['data'] = "Invalid email";
			sendResponse("5", "Invalid Email Address", $dataArray);
			
		}
		$password = $_POST['password'];
		$hashKey = $_POST['date'];
		$userType = intval($_POST['type']);
		//Check if username or email is on the database
		$checkQuery = "SELECT iUserID FROM tbl_Users WHERE sUserName = '$userName'";
		
		$result = mysqli_query($mysqli_OOP, $checkQuery);
		if(mysqli_num_rows($result) > 0)
		{
			$dataArray['data'] ="Username Taken";
			$dataArray['action'] = "register";
			sendResponse("3", "That username is already taken",$dataArray);
			
		}
		$checkQuery = "SELECT iUserID FROM tbl_Users WHERE sEAddress = '$emailAddress'";
		$result = mysqli_query($mysqli_OOP, $checkQuery);
		if(mysqli_num_rows($result) > 0)
		{
			$dataArray['action'] = "register";
			$dataArray['data'] ="Email taken";
			sendResponse("4", "That email address is already taken",$dataArray);
					
		}
		
		$insertQuery = "INSERT into tbl_Users (sFName,sLName,sUserName, sEAddress, sHashKey, iUserType,iUserIcon)
		VALUES(?,?,?,?,?,?,?)";
		$preparedStmt = $mysqli_OOP->prepare($insertQuery);
		$preparedStmt->bind_param("sssssii", $firstName, $lastName, $userName, $emailAddress, $hashKey, $userType, $usericon);
		
		$preparedStmt->execute();
		$userID = $preparedStmt->insert_id;
		
		$preparedStmt->close();
		
		sleep(0.1);
		$insertQuery = "INSERT into tbl_Password (iUserID,sPassword) VALUES (?,?)";
		$preparedStmt = $mysqli_OOP->prepare($insertQuery);
		$preparedStmt->bind_param("is", $userID, $password);
		$preparedStmt->execute();
		$preparedStmt->close();
		
		$dataArray['action'] = "register";
		$dataArray['data'] = "No errors found";
		sendResponse("0", "Registration Sucessful", $dataArray);
			
	}else if($action == "login")
	{
	
		$dataArray['action']="login";
		$userName = $_POST['username'];
		$selectQuery = "SELECT tbl_Password.sPassword, tbl_Password.iUserID FROM tbl_Password JOIN tbl_Users ON tbl_Users.iUserID = tbl_Password.iUserID AND tbl_Users.sUserName = '$userName'";
		$result = mysqli_fetch_assoc(mysqli_query($mysqli_OOP,$selectQuery));
		if($result == NULL)
		{
			$dataArray['data'] = "User not found";
			$status = "2";
			$statusMessage = "Incorrect username or password. Please try again";
			sendResponse($status, $statusMessage, $dataArray);
			
		}
		$userID = $result['iUserID'];
		$realPassword = $result['sPassword'];
		$password = $_POST['password'];
		
		
		$status="";
		$statusMessage="";
		
		if($realPassword == $password)
		{
			$userInfoQuery = "SELECT * FROM tbl_Users JOIN tbl_UserType ON tbl_Users.iUserType = tbl_UserType.iUserTypeID AND tbl_Users.iUserID = ".$userID;
			$resultUserInfo = mysqli_fetch_assoc(mysqli_query($mysqli_OOP, $userInfoQuery));
			$dataArray['notifsettings'] = $resultUserInfo['iEnableNotif'];
			$dataArray['fname'] = $resultUserInfo['sFName'];
			$dataArray['lname'] = $resultUserInfo['sLName'];
			$dataArray['username'] = $resultUserInfo['sUserName'];
			$dataArray['emailaddress'] = $resultUserInfo['sEAddress'];
			$dataArray['type'] = $resultUserInfo['sName'];
			$dataArray['userid'] = $resultUserInfo['iUserID'];
			$dataArray['usericon'] = $resultUserInfo['iUserIcon'];
			
			
			$status = "0";
			$statusMessage = "Login Sucessful. You will be signed in shortly";
			
		}
		else{
			
			$status = "1";
			$statusMessage = "Incorrect username or password. Please try again";
		
		}
		sendResponse($status,$statusMessage,$dataArray);
		
		
		exit;
		//Select user based on username and select the password, then compare and proceed if they match
	}
	
	
}



//This Function will handle the response to the application
function sendResponse($status, $statusMessage, $data){
		
	$responseArray['status'] = $status;
	$responseArray['statusMessage'] = $statusMessage;
	$responseArray['data'] = $data;
	
	echo json_encode($responseArray);
	exit;
}

?>