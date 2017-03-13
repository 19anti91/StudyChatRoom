package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class PrivateMessage extends AppCompatActivity {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public ListView usersFound;
    ViewGroup view;

    Activity act = this;
    LinearLayout layout;
    private ReceiveMessageService mBoundService;

    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_message);
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor edit = pref.edit();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Private Conversation with " + pref.getString("currentPrivUser", ""));
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);

        layout = (LinearLayout) findViewById(R.id.privMsgLayout);
        view = (ViewGroup) findViewById(R.id.privMsgLayout);

        //TODO Test send message
        //TODO Get previous messages and populate(somehow)
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (pref.getInt("hasMessage", 0) == 1) {
                            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.msg_from_them, null);
                            layout.addView(view);
                            TextView msgFromMe = (TextView) findViewById(R.id.msgFromThemTxt);
                            msgFromMe.setId(generateViewId());
                            msgFromMe.setText(pref.getString("message", "a"));
                            Log.e("TEST", pref.getString("message", "a"));
                            layout.invalidate();
                            edit.putInt("hasMessage", 0);
                            edit.apply();

                        }


                    }
                });

            }
        }, 0, 1000);


    }


    //TODO either use FCM to send and receive messages or create a service that sends requests every 0.2 seconds to the server requesting new messages
    public void sendMessage(View v) {

        View view = LayoutInflater.from(this).inflate(R.layout.msg_from_me, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String toUsername = "";

        if (!getMessage().isEmpty()) {
            new MessageAsync(getApplicationContext(), this, view).execute("privMsg", String.valueOf(pref.getInt("userid", 0)), toUsername, getMessage());
            layout.addView(view);
            TextView msgFromMe = (TextView) findViewById(R.id.msgFromMeTxt);
            msgFromMe.setId(generateViewId());
            msgFromMe.setText(getMessage());
            layout.invalidate();
            EditText msgToSend = (EditText) findViewById(R.id.msgToSend);
            msgToSend.setText("");

            final ScrollView scroll = (ScrollView) findViewById(R.id.scrollPriv);
            scroll.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll.fullScroll(View.FOCUS_DOWN);
                            }
                        }

            );

        }
    }

    public void recieveMessage(View v) {
        new MessageAsync(this.getApplicationContext(), this, this.view).execute();
    }

    public String getMessage() {
        String msg;
        EditText msgToSend = (EditText) findViewById(R.id.msgToSend);
        msg = msgToSend.getText().toString();

        return msg;
    }

}