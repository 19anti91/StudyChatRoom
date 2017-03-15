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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class PrivateMessage extends AppCompatActivity {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    static boolean isActive = false;
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
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

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

        new MessageAsync(this.getApplicationContext(), this, view, layout).execute("getPrivMsg", String.valueOf(pref.getInt("userid", 0)), pref.getString("currentPrivUser", ""));


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (pref.getInt("hasMessage", 0) == 1) {

                            populateReceivedMsg(pref.getString("message", ""), pref.getString("userFrom", ""));
                            Log.e("TEST", pref.getString("message", "a"));

                            edit.putInt("hasMessage", 0);
                            edit.apply();

                        }


                    }
                });

            }
        }, 0, 500);


    }


    public void sendMessage(View v) {

        View view = LayoutInflater.from(this).inflate(R.layout.msg_from_me, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String toUsername = pref.getString("currentPrivUser", "");

        if (!getMessage().isEmpty()) {
            new MessageAsync(getApplicationContext(), this, view, layout).execute("privMsg", String.valueOf(pref.getInt("userid", 0)), toUsername, getMessage());
            layout.addView(view);
            TextView msgFromMe = (TextView) findViewById(R.id.msgFromMeTxt);
            msgFromMe.setId(generateViewId());
            msgFromMe.setText(getMessage());
            ImageView icon = getIcon(pref.getInt("usericon", 0), R.id.messageFromMeIcon);
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

    //TODO emojis
/*
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
        msgFromMe.setText(getEmojiByUnicode(0x1F60A));
        http://apps.timwhitlock.info/emoji/tables/unicode
    }*/
    public void populateReceivedMsg(String msg, String from) {


        View view = LayoutInflater.from(this).inflate(R.layout.msg_from_them, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String toUsername = pref.getString("currentPrivUser", "");

        if (!msg.isEmpty() && from.equals(toUsername)) {

            layout.addView(view);
            TextView msgFromMe = (TextView) findViewById(R.id.msgFromThemTxt);
            //int privUserIcon = pref.getInt("currentPrivUserIcon",7);
            ImageView icon = getIcon(pref.getInt("currentPrivUserIcon", 7), R.id.msgFromThemIcon);

            msgFromMe.setId(generateViewId());
            msgFromMe.setText(msg);
            layout.invalidate();

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

    public ImageView getIcon(int icon, int id) {
        ImageView imageView = (ImageView) findViewById(id);
        switch (icon) {
            case 0:
                imageView.setImageResource(R.drawable.ic_femalelight);
                break;
            case 1:
                imageView.setImageResource(R.drawable.ic_femaledark);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_femaledarker);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_maleredhair);
                break;
            case 4:
                imageView.setImageResource(R.drawable.ic_malelight);
                break;
            case 5:
                imageView.setImageResource(R.drawable.ic_maledarker);
                break;

        }
        return imageView;
    }



    public void recieveMessage(View v) {
        new MessageAsync(this.getApplicationContext(), this, this.view, layout).execute();
    }

    public String getMessage() {
        String msg;
        EditText msgToSend = (EditText) findViewById(R.id.msgToSend);
        msg = msgToSend.getText().toString();

        return msg;
    }

}