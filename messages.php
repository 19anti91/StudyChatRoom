<?php

define(DBUSER, "passtrun_oop");
define(DBNAME, "passtrun_oop");
define(DBPASSWORD, "ICKKCK!0");
define(DBSERVER, "localhost");

$mysqli_OOP = mysqli_connect(DBSERVER, DBUSER, DBPASSWORD, DBNAME);
if(mysqli_connect_errno($mysqli_OOP))
{
	$message = "Failed to Connect to the Database: " . mysqli_connect_error();
 
	sendResponse("error", $message, "");
}

$responseArray = array();


if(isset($_POST['action'])){
	
	$action = $_POST['action'];
	
	
	if($action == "getPrivMsg"){
		$myId = intval($_POST['userId']);
		$otherUserName = $_POST['otherUserName'];
		
	$otherId = intval(mysqli_fetch_assoc(mysqli_query($mysqli_OOP, "SELECT iUserID FROM tbl_Users WHERE sUserName = '$otherUserName'"))['iUserID']);
		
		
		$dataArray = array();
		
		$query = "SELECT * FROM tbl_PrivateMessage WHERE (iMsgFrom = $myId AND iMsgTo = $otherId) OR ((iMsgFrom = $otherId AND iMsgTo = $myId)) ORDER by dDateStampPrivateMessage ASC";
		$result = mysqli_query($mysqli_OOP, $query);
		if(mysqli_query($mysqli_OOP,$query)){
			$temp = array();
			while($row = mysqli_fetch_assoc($result)){
				$temp['from'] = $row['iMsgFrom'];
				$temp['message'] = $row['sMessage'];
				array_push($dataArray,$temp);
			}
			sendResponse("0","Messages Retrieved Successfuly",$dataArray);
		}
		
		
	}else if($action == "groupMsg"){
		$userid = intval($_POST["userId"]);
		$groupName = $_POST["groupName"];
		$message = $_POST["message"];
		$dataArray = array();
		$query = "INSERT INTO tbl_ChatRoomMessages (iChatRoomID,iChatRoomMsgFrom,sChatRoomMessage) VALUES ((SELECT iChatRoomID FROM tbl_ChatRooms WHERE sChatRoomName = '$groupName'),$userid,'$message')";
		mysqli_query($mysqli_OOP, $query);
		sendResponse("0","Message Sent",$dataArray);

	}else if($action == "getGroupMsg"){

		$roomname = $_POST['groupName'];
		$dataArray = array();
		$query = "SELECT * FROM tbl_ChatRoomMessages JOIN tbl_Users ON tbl_ChatRoomMessages.iChatRoomMsgFrom = tbl_Users.iUserID WHERE iChatRoomID = (SELECT iChatRoomID FROM tbl_ChatRooms WHERE sChatRoomName = '$roomname') ORDER by dDateStampChatRoomMessage ASC";
	
		if($result = mysqli_query($mysqli_OOP, $query)){
			$temp  = array();
			while($row = mysqli_fetch_assoc($result)){
				$temp['from'] = $row['iChatRoomMsgFrom'];
				$temp['message']  =$row['sChatRoomMessage'];
				$temp['fromusername'] = $row['sUserName'];
				$temp['fromusericon'] = $row['iUserIcon'];
				array_push($dataArray,$temp);
			}
			sendResponse("0","Messages Retrieved", $dataArray);
		}
		
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