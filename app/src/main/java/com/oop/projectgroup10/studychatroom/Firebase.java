package com.oop.projectgroup10.studychatroom;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.google.android.gms.internal.zzt.TAG;

/**
 * Created by no717490 on 3/7/2017.
 */

public class Firebase extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        SharedPreferences pref = getBaseContext().getSharedPreferences("StudyChatRoom", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("firebasetoken", refreshedToken);
        edit.apply();

    }

    private void sendRegistrationToServer(String token, String userid) {

    }

}
