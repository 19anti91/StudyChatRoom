package com.oop.projectgroup10.studychatroom;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.google.android.gms.internal.zzt.TAG;

//This is the notification service for firebase
public class NotificationService extends FirebaseMessagingService {
    public NotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...


        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            String message = remoteMessage.getData().get("message");
            String fromUser = remoteMessage.getData().get("userFrom");
            String fromGroup = remoteMessage.getData().get("userGroup");
            String icon = remoteMessage.getData().get("userIcon");

            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pref.edit();
            if (!fromUser.equals(pref.getString("currentPrivUser", "0")) /*&& fromGroup.equals("")*/) {
                sendNotification("New message from " + fromUser, message);
            } else if (!fromGroup.equals(pref.getString("currentChatRoom", ""))) {
                sendNotification("New message on " + fromGroup + " Chat Room", message);
            } else {

                editor.putInt("hasMessage", 1);
                editor.putString("message", message);
                editor.putString("userFrom", fromUser);
                editor.putString("userGroup", fromGroup);
                editor.putString("groupUserIcon", icon);
                editor.apply();

            }

            Log.d("FROM", fromUser);
            Log.d("Message", message);


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d("Title", remoteMessage.getData().toString());
            sendNotification("Study Chat Room",remoteMessage.getNotification().getBody());
            //sendNotification("message from noel", "test");

        }


        //  sendNotification(remoteMessage.getData().toString());
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String title, String message) {

        Intent intent = new Intent(this, DashBoard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_gear)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
