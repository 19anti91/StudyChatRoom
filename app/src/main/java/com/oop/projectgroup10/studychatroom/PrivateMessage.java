package com.oop.projectgroup10.studychatroom;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

public class PrivateMessage extends AppCompatActivity {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    LinearLayout layout;

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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Private Messages");
        layout = (LinearLayout) findViewById(R.id.privMsgLayout);
    }

    public void sendMessage(View v) {

        View view = LayoutInflater.from(this).inflate(R.layout.msg_from_me, null);

        if (!getMessage().isEmpty()) {
            layout.addView(view);
            TextView msgFromMe = (TextView) findViewById(R.id.msgFromMeTxt);
            msgFromMe.setId(generateViewId());
            msgFromMe.setText(getMessage());
            layout.invalidate();
        }
    }

    public String getMessage() {
        EditText msgToSend = (EditText) findViewById(R.id.msgToSend);
        return msgToSend.getText().toString();
    }
}
