<?php
define(DBUSER, "passtrun_oop");
define(DBNAME, "passtrun_oop");
define(DBPASSWORD, "ICKKCK!0");
define(DBSERVER, "localhost");

$mysqli_OOP = mysqli_connect(DBSERVER, DBUSER, DBPASSWORD, DBNAME);
if(mysqli_connect_errno($mysqli_OOP))
{
	$message = "Failed to Connect to the Database: " . mysqli_connect_error();
 
	//sendResponse("error", $message, "");
}

if(isset($_POST['action'])){
		
		
	$action = $_POST['action'];
	if($action == "sendNotifFCMUser")
	{
		$to = $_POST['to'];
		$title = $_POST['title'];
		$message = $_POST['message'];
		
		$payload = array();
		$payload['to'] = $to;
		$payload['notification']['title'] = $title;
		$payload['notification']['body'] = $message;
		
		//TODO Check if it is to a group or to a user, do query and call function accordingly
		
		sendNotification($payload);
		
	}
	if($action == "inviteUser"){
		$userName = $_POST['userName'];
		$roomName = $_POST['roomName'];
		$token = mysqli_fetch_assoc(mysqli_query($mysqli_OOP,"SELECT sFireBaseToken FROM tbl_Users WHERE sUserName = '$userName'"))['sFireBaseToken'];
		
		mysqli_query($mysqli_OOP,"INSERT INTO tbl_ChatRoomMembership (iChatRoomID,iUserID) VALUES ((SELECT iChatRoomID FROM tbl_ChatRooms WHERE sChatRoomName='$roomName'),(SELECT iUserID FROM tbl_Users WHERE sUserName = '$userName'))");
		$payload = array();
		$payload['to'] = $token;
		$payload['notification']['title'] = "You have been invited to a Chat Room";
		$payload['notification']['body'] = "You have been invited to $roomName. The room will be under 'Your Chat Rooms'";
		sendNotification($payload);
		
		
	}
	if($action == "banUser"){
		$userName = $_POST['userName'];
		$roomName = $_POST['roomName'];
		$token = mysqli_fetch_assoc(mysqli_query($mysqli_OOP,"SELECT sFireBaseToken FROM tbl_Users WHERE sUserName = '$userName'"))['sFireBaseToken'];
		
		mysqli_query($mysqli_OOP,"DELETE FROM tbl_ChatRoomMembership WHERE iChatRoomID = (SELECT iChatRoomID FROM tbl_ChatRooms WHERE sChatRoomName='$roomName') AND iUserID = (SELECT iUserID FROM tbl_Users WHERE sUserName = '$userName')");
		$payload = array();
		$payload['to'] = $token;
		$payload['notification']['title'] = "You have been banned from a Chat Room";
		$payload['notification']['body'] = "You have been banned from $roomName";
		sendNotification($payload);
	}
	if($action == "privMsg")
	{
	
		$to = $_POST['to'];
		
		$from = intval($_POST['from']);
		$message = $_POST['message'];
		
		$queryTo = "SELECT iUserID, sFireBaseToken FROM tbl_Users WHERE sUserName = '$to'";
		$result = mysqli_fetch_assoc(mysqli_query($mysqli_OOP,$queryTo));
		
		$to = $result['sFireBaseToken'];
		$receiverId = $result['iUserID'];
		$senderId = $from;
		
		$queryFrom = "SELECT sUserName,iUserIcon FROM tbl_Users WHERE iUserID = $from";
		$result = mysqli_fetch_assoc(mysqli_query($mysqli_OOP,$queryFrom));
		
		$from = $result['sUserName'];
		$icon = $result['iUserIcon'];
		$payload = array();
		$payload['to'] = $to;
		$payload['data']['message'] = $message;
		$payload['data']['userFrom'] = $from;
		$payload['data']['userGroup'] = "";
		$payload['data']['userIcon'] = $icon;
		//$payload['notification']['body'] = $body;
		
	
		$query = "INSERT INTO tbl_PrivateMessage (iMsgFrom, iMsgTo,sMessage) VALUES ($senderId,$receiverId,'$message')";
			if(mysqli_query($mysqli_OOP,$query)){
			sendNotification($payload);
			}
	}
	if($action == "groupMsg"){
				
		$userid = intval($_POST["userId"]);
		$groupName = $_POST["groupName"];
		$message = $_POST["message"];

		
		$dataArray = array();
		
		$query = "INSERT INTO tbl_ChatRoomMessages (iChatRoomID,iChatRoomMsgFrom,sChatRoomMessage) VALUES ((SELECT iChatRoomID FROM tbl_ChatRooms WHERE sChatRoomName = '$groupName'),$userid,'$message')";
		mysqli_query($mysqli_OOP, $query);
			
		$queryTo = "SELECT tbl_Users.sFireBaseToken, tbl_Users.iUserIcon FROM tbl_Users JOIN tbl_ChatRoomMembership ON tbl_ChatRoomMembership.iUserID = tbl_Users.iUserID WHERE tbl_ChatRoomMembership.iChatRoomID = (SELECT iChatRoomID FROM tbl_ChatRooms WHERE sChatRoomName = '$groupName')";
		$result = mysqli_query($mysqli_OOP,$queryTo);
		$queryGetFrom = "SELECT sUserName FROM tbl_Users WHERE iUserID = $userid";
		$from = mysqli_query($mysqli_OOP, $queryGetFrom);
		$from = mysqli_fetch_assoc($from)['sUserName'];
		
	
		while($row = mysqli_fetch_assoc($result)){
			$payload = array();
			$payload['to'] = $row['sFireBaseToken'];
			$payload['data']['message'] = $message;
			$payload['data']['userFrom'] = $from;
			$payload['data']['userGroup'] = $groupName;
			$payload['data']['userIcon'] = $row['iUserIcon'];
			sendNotification($payload);
		}
		
		

	}
	
}

function sendNotification($array){
	
		
		$load = json_encode($array);
		$ch = curl_init( 'https://fcm.googleapis.com/fcm/send' );
		# Setup request to send json via POST.

		curl_setopt( $ch, CURLOPT_POSTFIELDS, $load );
		curl_setopt( $ch, CURLOPT_HTTPHEADER, array('Content-Type:application/json', 'Authorization:key=AIzaSyDuh2d53YCtFXO2lEHaCjUvP4AOsU3nxcg'));
		# Return response instead of printing.
		curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
		# Send request.
		$result = curl_exec($ch);
		curl_close($ch);
		# Print response.
		echo "<pre>$result</pre>";
}

/*
$ch = curl_init( 'https://fcm.googleapis.com/fcm/send' );
# Setup request to send json via POST.

$payload = array();
$payload['notification']['title'] = "Private Message";
$payload['notification']['body'] = "Noel Sent you a private Message";
$payload['to'] = "cutYJO5-Y9w:APA91bH87xNJr_qcnzoqCnNrTrD1x7E7ZVfPIb5jfvjUFU1BXs7ifLSLNYFKimASMXh5_cfLnZnZgaVM4QGPkPe9ozlN0LfVfy2JmtS89hPcKp54bW-JZz2l93qlME2AR4qLs01R8x9C";
$payload = json_encode($payload);

curl_setopt( $ch, CURLOPT_POSTFIELDS, $payload );
curl_setopt( $ch, CURLOPT_HTTPHEADER, array('Content-Type:application/json', 'Authorization:key=AIzaSyDuh2d53YCtFXO2lEHaCjUvP4AOsU3nxcg'));
# Return response instead of printing.
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
# Send request.
$result = curl_exec($ch);
curl_close($ch);
# Print response.
echo "<pre>$result</pre>";*/
?>