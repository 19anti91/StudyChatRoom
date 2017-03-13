package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Anti1991 on 3/11/2017.
 */

public class MessageAsync extends AsyncTask<String, Void, String> {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    Context context;
    Activity act;
    View view;

    public MessageAsync(Context context, Activity act, View view) {
        this.context = context;
        this.act = act;
        this.view = view;
    }

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
    protected String doInBackground(String... args) {
        String action = args[0];
        String senderId = args[1];
        String receiverUsername = args[2];
        String message = args[3];


        URL url;
        OutputStream outputPost;
        BufferedReader in;
        HttpURLConnection client;
        String response = "";

        try {
            String link = "http://www.passtrunk.com/OOPAPI/fcmhandler.php";
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
            data += "&" + URLEncoder.encode("from", "UTF-8") + "=" + URLEncoder.encode(senderId, "UTF-8");
            data += "&" + URLEncoder.encode("to", "UTF-8") + "=" + URLEncoder.encode(receiverUsername, "UTF-8");
            data += "&" + URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");

            url = new URL(link);

            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");


            outputPost = new BufferedOutputStream(client.getOutputStream());
            outputPost.write(data.getBytes());
            outputPost.flush();


            in = new BufferedReader(new InputStreamReader(client.getInputStream()));


            StringBuilder sb = new StringBuilder();
            String line = null;

            //Read response
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }

            response = sb.toString();
            Log.d("Result", response);
            in.close();
            outputPost.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {

        //processFinish(result);

    }

    public void processFinish(String result) {
        JSONObject res;
        JSONArray messages;
        try {
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.privMsgLayout);
            //res = new JSONObject(result);
            //  messages = (JSONArray)res.get("messages");
            String msg = "hello";

            View v = LayoutInflater.from(act).inflate(R.layout.msg_from_them, null);

            layout.addView(v);
            TextView msgFromThem = (TextView) v.findViewById(R.id.msgFromThemTxt);
            msgFromThem.setId(generateViewId());
            msgFromThem.setText(msg);
            layout.invalidate();
            final ScrollView scroll = (ScrollView) v.findViewById(R.id.scrollPriv);
            scroll.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll.fullScroll(View.FOCUS_DOWN);
                            }
                        }

            );


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
