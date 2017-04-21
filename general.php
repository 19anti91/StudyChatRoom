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

$dataArray = array();

if(isset($_POST['action']))
{
	
	$action = $_POST['action'];
	
	if($action == "updateFireBaseToken")
	{
		$userID = intval($_POST['userid']);
		$token = $_POST['token'];
	
		$result = mysqli_query($mysqli_OOP, "UPDATE tbl_Users SET sFireBaseToken = '$token' WHERE iUserID = $userID");
		if($result){
		$dataArray['data'] = "No issues during update";
		sendResponse("0", "Update Successful", $dataArray);
		}
			
	}
	if($action == "updateFname"){
		$userID = intval($_POST['userid']);
		$fname = $_POST['fname'];
		
		$result = mysqli_query($mysqli_OOP, "UPDATE tbl_Users SET sFName = '$fname' WHERE iUserID = $userID");
		if($result){
		$dataArray['data'] = "No issues during update";
		sendResponse("0", "Update Successful", $dataArray);
	}
	}
	if($action == "updateLname"){
		$userID = intval($_POST['userid']);
		$lname = $_POST['lname'];
		$result = mysqli_query($mysqli_OOP, "UPDATE tbl_Users SET sLName = '$lname' WHERE iUserID = $userID");
		if($result){
		$dataArray['data'] = "No issues during update";
		sendResponse("0", "Update Successful", $dataArray);
		}
	}
	if($action == "updateEmail"){
		$userID = intval($_POST['userid']);
		$email = $_POST['emailaddress'];
		$result = mysqli_query($mysqli_OOP, "UPDATE tbl_Users SET sEAddress = '$email' WHERE iUserID = $userID");
		if($result){
		$dataArray['data'] = "No issues during update";
		sendResponse("0", "Update Successful", $dataArray);
		}
	}
	if($action == "updateNotif"){
		$userID = intval($_POST['userid']);
		$notif = intval($_POST['enableNotif']);
		$result = mysqli_query($mysqli_OOP, "UPDATE tbl_Users SET iEnableNotif = '$notif' WHERE iUserID = $userID");
		if($result){
		$dataArray['data'] = "No issues during update";
		sendResponse("0", "Update Successful", $dataArray);
	}
	}
	if($action == "createRoom"){
		$userID = intval($_POST['userid']);
		$roomName = $_POST['roomName'];
		$roomPass = $_POST['roomPassword'];
		$isPriv = $_POST['isPrivate'] == "true" ? 1 : 0;
		
		//intval($_POST['isPrivate']);
		
		$checkNameQuery = "SELECT sChatRoomName FROM tbl_ChatRooms WHERE sChatRoomName = '$roomName'";
		$duplicate = mysqli_query($mysqli_OOP, $checkNameQuery);
		if(mysqli_num_rows($duplicate) > 0)
		{
			$dataArray['data'] ="Room Name Taken";
			$dataArray['action'] = "Create Room";
			sendResponse("1", "The Room Name is taken",$dataArray);
			
		}
		$result = mysqli_query($mysqli_OOP, "INSERT INTO tbl_ChatRooms (sChatRoomName,iChatRoomOwnerID,iPrivate,sPassword) VALUES('$roomName',$userID,$isPriv,'$roomPass')");
		if($result){
			$chatRoomID = mysqli_insert_id($mysqli_OOP);
			$result1 = mysqli_query($mysqli_OOP, "INSERT INTO tbl_ChatRoomMembership (iChatRoomID,iUserID) VALUES ($chatRoomID,$userID)");
			if($result1){
				$dataArray['data'] = "No issues creating the room";
				$dataArray['action'] = $action;
				sendResponse("0", "Update Successful", $dataArray);
			}
		
	}
	}
	if($action == "getAllUsers"){


	
		$usersArray = array();
		$tempArray = array();
		$userID = intval($_POST['userid']);
		$query = "SELECT * FROM tbl_Users WHERE iUserID != $userID";
		$result = mysqli_query($mysqli_OOP, $query);
		
		while($row = mysqli_fetch_assoc($result)){
			$tempArray['userid'] = $row['iUserID'];
			$tempArray['username'] = $row['sUserName'];
			$tempArray['usericon'] = $row['iUserIcon'];
			array_push($usersArray,$tempArray);
		}
		sendResponse("0","User Retrieval Successful", $usersArray);
		
	}
	if($action == "getAllChatRooms"){
		
		$chatRoomsArray = array();
		$tempArray = array();
		$userId = intval($_POST['userid']);
		
		$userType = mysqli_fetch_assoc(mysqli_query($mysqli_OOP, "SELECT iUserType FROM tbl_Users WHERE iUserID = $userId"))['iUserType'];
		if($userType == 1){
				$queryAllChatRooms = "SELECT tbl_ChatRooms.iChatRoomID, tbl_ChatRooms.iPrivate, tbl_ChatRooms.sPassword, tbl_ChatRooms.sChatRoomName, tbl_ChatRooms.iChatRoomOwnerID, tbl_Users.sUserName AS OwnerUserName, COUNT(*) AS TotalMembers FROM tbl_ChatRoomMembership JOIN tbl_ChatRooms ON tbl_ChatRooms.iChatRoomID = tbl_ChatRoomMembership.iChatRoomID JOIN tbl_Users on tbl_ChatRooms.iChatRoomOwnerID = tbl_Users.iUserID WHERE 1 Group By tbl_ChatRoomMembership.iChatRoomID";
		
				$queryMyChatRooms = "SELECT tbl_ChatRooms.iChatRoomID, tbl_ChatRooms.iPrivate, tbl_ChatRooms.sPassword, tbl_ChatRooms.sChatRoomName, tbl_ChatRooms.iChatRoomOwnerID, tbl_Users.sUserName AS OwnerUserName, COUNT(*) AS TotalMembers FROM tbl_ChatRoomMembership JOIN tbl_ChatRooms ON tbl_ChatRooms.iChatRoomID = tbl_ChatRoomMembership.iChatRoomID JOIN tbl_Users on tbl_ChatRooms.iChatRoomOwnerID = tbl_Users.iUserID WHERE 1 Group By tbl_ChatRoomMembership.iChatRoomID";
	
		}
		else{
			$queryAllChatRooms = "SELECT tbl_ChatRooms.iChatRoomID, tbl_ChatRooms.iPrivate, tbl_ChatRooms.sPassword, tbl_ChatRooms.sChatRoomName, tbl_ChatRooms.iChatRoomOwnerID, tbl_Users.sUserName AS OwnerUserName, COUNT(*) AS TotalMembers FROM tbl_ChatRoomMembership JOIN tbl_ChatRooms ON tbl_ChatRooms.iChatRoomID = tbl_ChatRoomMembership.iChatRoomID JOIN tbl_Users on tbl_ChatRooms.iChatRoomOwnerID = tbl_Users.iUserID WHERE tbl_ChatRooms.iPrivate = 0 Group By tbl_ChatRoomMembership.iChatRoomID";
			$queryMyChatRooms = "SELECT tbl_ChatRooms.iChatRoomID, tbl_ChatRooms.iPrivate, tbl_ChatRooms.sPassword, tbl_ChatRooms.sChatRoomName, tbl_ChatRooms.iChatRoomOwnerID, tbl_Users.sUserName AS OwnerUserName FROM tbl_ChatRoomMembership JOIN tbl_ChatRooms ON tbl_ChatRooms.iChatRoomID = tbl_ChatRoomMembership.iChatRoomID JOIN tbl_Users on tbl_ChatRooms.iChatRoomOwnerID = tbl_Users.iUserID WHERE tbl_ChatRooms.iChatRoomOwnerID = $userId OR tbl_ChatRoomMembership.iUserID = $userId Group By tbl_ChatRoomMembership.iChatRoomID";
	
		
		}
	
		
		$allChatRooms = array();
		$myChatRooms = array();
		$result = mysqli_query($mysqli_OOP, $queryMyChatRooms);
		
		while($row = mysqli_fetch_assoc($result)){
			$tempArray['chatroomname'] =$row['sChatRoomName'];
			$tempArray['chatroomownerusername'] =$row['OwnerUserName'];
			$tempArray['chatroomownerid'] = $row['iChatRoomOwnerID'];
			$tempArray['chatispriv'] = $row['iPrivate'];
			
			$tempArray['chatroommembers']  = mysqli_fetch_assoc(mysqli_query($mysqli_OOP, "SELECT COUNT(*) AS TotalMembers FROM tbl_ChatRoomMembership WHERE iChatRoomID = ".$row['iChatRoomID']))['TotalMembers'];
			$tempArray['chatroompass'] = $row['sPassword'];
			
			array_push($myChatRooms, $tempArray);
		}


		$result = mysqli_query($mysqli_OOP, $queryAllChatRooms);
		while($row = mysqli_fetch_assoc($result)){
			$tempArray['chatroomname'] =$row['sChatRoomName'];
			$tempArray['chatroomownerusername'] =$row['OwnerUserName'];
			$tempArray['chatroomownerid'] = $row['iChatRoomOwnerID'];
			$tempArray['chatispriv'] = $row['iPrivate'];
			$tempArray['chatroommembers'] =$row['TotalMembers'];
			$tempArray['chatroompass'] = $row['sPassword'];

			
			array_push($allChatRooms, $tempArray);
		}
		
		$chatRoomsArray['myChatRooms'] = $myChatRooms;
		$chatRoomsArray['allChatRooms'] = $allChatRooms;
		sendResponse("0","Chat Rooms Retrieval Successful", $chatRoomsArray);

	}
	if($action == "joinChatRoom"){
		
		$userId = intval($_POST['userid']);
		$roomname = $_POST['roomName'];
		$response = array();
		if(mysqli_num_rows(mysqli_query($mysqli_OOP,"SELECT * FROM tbl_ChatRoomMembership WHERE iChatRoomID = (SELECT tbl_ChatRooms.iChatRoomID FROM tbl_ChatRooms WHERE tbl_ChatRooms.sChatRoomName = '$roomname') AND iUserID =$userId")) == 0)
		{
			$query = "INSERT INTO tbl_ChatRoomMembership (iChatRoomID, iUserID) VALUES ((SELECT tbl_ChatRooms.iChatRoomID FROM tbl_ChatRooms WHERE tbl_ChatRooms.sChatRoomName = '$roomname'),$userId)";
			mysqli_query($mysqli_OOP,$query);
			$response['data'] = "All good";
			sendResponse("0","User From Groups Retrieval Successful", $response);
		}
			sendResponse("0","User From Groups Retrieval Successful", $response);
	}
	if($action == "getAllUsersFromChatRoom"){
		$userId = intval($_POST['userid']);
		$roomname = $_POST['roomName'];
		$tempArray = array();
		$usersArray = array();
		
		$query = "SELECT tbl_Users.sUserName, tbl_Users.iUserID, tbl_Users.iUserIcon FROM tbl_Users JOIN tbl_ChatRoomMembership ON tbl_ChatRoomMembership.iUserID = tbl_Users.iUserID WHERE tbl_ChatRoomMembership.iChatRoomID = (SELECT tbl_ChatRooms.iChatRoomID from tbl_ChatRooms WHERE tbl_ChatRooms.sChatRoomName = '$roomname')";
		
		$result = mysqli_query($mysqli_OOP,$query);
		
		while($row = mysqli_fetch_assoc($result)){
			$tempArray['userid'] = $row['iUserID'];
			$tempArray['username'] = $row['sUserName'];
			$tempArray['usericon'] = $row['iUserIcon'];
			array_push($usersArray,$tempArray);
		}
		sendResponse("0","User From Groups Retrieval Successful", $usersArray);
		
	}
	if($action == "updateChatRoomName"){
		
		$response = array();
		$currentName = $_POST["roomName"];
		$newName = $_POST["newRoomName"];
		
		$querySelect = "SELECT sChatRoomName FROM tbl_ChatRooms WHERE sChatRoomName = '$newName'";
		if(mysqli_num_rows(mysqli_query($mysqli_OOP,$querySelect)) == 0){
			mysqli_query($mysqli_OOP,"UPDATE tbl_ChatRooms SET sChatRoomName ='$newName' WHERE sChatRoomName = '$currentName'");
			$response['name'] = $newName;
			sendResponse("0", "Name Changed Successfully", $response);
			
		}
		$response['name'] = $currentName;
		sendResponse("1", "The room name already exists", $response);
	}
	if($action == "makeRoomPriv"){
		
		$responseArray = array();
		$roomName = $_POST['roomName'];
		$priv = intval($_POST['private']);
		
		mysqli_query($mysqli_OOP, "UPDATE tbl_ChatRooms SET iPrivate = $priv WHERE sChatRoomName = '$roomName'");
		sendResponse("0","Update successful",$responseArray); 
		
	}
	if($action == "setupRoomPassword"){
		$responseArray = array();
		$roomName = $_POST['roomName'];
		$password = $_POST['password'];
		
		mysqli_query($mysqli_OOP, "UPDATE tbl_ChatRooms SET sPassword = '$password' WHERE sChatRoomName = '$roomName'");
		sendResponse("0","Password Setup Sucessful",$responseArray); 
		
	}
	if($action == "removeChatRoomPassword"){
		$responseArray = array();
		$roomName = $_POST['roomName'];
		$password = hash("SHA256", "");
		
		mysqli_query($mysqli_OOP, "UPDATE tbl_ChatRooms SET sPassword = '$password' WHERE sChatRoomName = '$roomName'");
		sendResponse("0","Password Removal Successful",$responseArray); 
		
	}
	if($action == "leaveChatRoom"){
		$userId = intval($_POST['userid']);
		$roomName = $_POST['roomName'];
		$responseArray = array();
		mysqli_query($mysqli_OOP, "DELETE FROM tbl_ChatRoomMembership WHERE iChatRoomID =(SELECT iChatRoomID FROM tbl_ChatRooms WHERE sChatRoomName ='$roomName') AND iUserID=$userId");
		sendResponse("0","Room left successfully",$responseArray); 
	}
	if($action == "deleteChatRoom"){
		$userId = intval($_POST['userid']);
		$roomName = $_POST['roomName'];
		$responseArray = array();
		mysqli_query($mysqli_OOP, "DELETE FROM tbl_ChatRoomMembership WHERE sChatRoomName='$roomName'");
		mysqli_query($mysqli_OOP, "DELETE FROM tbl_ChatRooms WHERE sChatRoomName='$roomName'");
		mysqli_query($mysqli_OOP, "DELETE FROM tbl_ChatRoomMessages WHERE iChatRoomID =(SELECT iChatRoomID FROM tbl_ChatRooms WHERE sChatRoomName ='$roomName')");
		sendResponse("0","Room deletion successful",$responseArray); 
	}
	
	if($action == "blockUser"){
		$userId = intval($_POST['userid']);
		$blockeeId = $_POST['blockee'];
		$responseArray = array();
		$blockedId = mysqli_fetch_assoc(mysqli_query($mysqli_OOP, "SELECT iUserID FROM tbl_Users WHERE sUserName = '$blockeeId'"))['iUserID'];
		mysqli_query($mysqli_OOP, "INSERT INTO tbl_BlockedUsers (iBlockedUserID,iBlockerUserID) VALUES ($blockedId,$userId)");
		
			sendResponse("0","User Blocked Successfully",$responseArray); 
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