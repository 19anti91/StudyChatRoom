package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    LinearLayout layout;

    String action;
    public MessageAsync(Context context, Activity act, View view, LinearLayout layout) {
        this.context = context;
        this.act = act;
        this.view = view;
        this.layout = layout;
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
        action = args[0];
        String senderId = args[1];
        String receiverUsername = args[2];
        String message;


        URL url;
        OutputStream outputPost;
        BufferedReader in;
        HttpURLConnection client;
        String response = "";
        String link = "";
        String data = "";
        try {
            if (action.equals("getPrivMsg")) {
                link = "http://www.passtrunk.com/OOPAPI/messages.php";
                data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                data += "&" + URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(senderId, "UTF-8");
                data += "&" + URLEncoder.encode("otherUserName", "UTF-8") + "=" + URLEncoder.encode(receiverUsername, "UTF-8");

            } else if (action.equals("groupMsg")) {
                message = args[3];
                link = "http://www.passtrunk.com/OOPAPI/fcmhandler.php";
                data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                data += "&" + URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(senderId, "UTF-8");
                data += "&" + URLEncoder.encode("groupName", "UTF-8") + "=" + URLEncoder.encode(receiverUsername, "UTF-8");
                data += "&" + URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");

            } else if (action.equals("getGroupMsg")) {
                link = "http://www.passtrunk.com/OOPAPI/messages.php";
                data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                data += "&" + URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(senderId, "UTF-8");
                data += "&" + URLEncoder.encode("groupName", "UTF-8") + "=" + URLEncoder.encode(receiverUsername, "UTF-8");
            }else if(action.equals("inviteUser")){
                link = "http://www.passtrunk.com/OOPAPI/fcmhandler.php";
                data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                data += "&" + URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(senderId, "UTF-8");
                data += "&" + URLEncoder.encode("roomName", "UTF-8") + "=" + URLEncoder.encode(receiverUsername, "UTF-8");
                Log.e("Data", senderId+receiverUsername);
            }else if(action.equals("banUser")){
                link = "http://www.passtrunk.com/OOPAPI/fcmhandler.php";
                data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                data += "&" + URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(senderId, "UTF-8");
                data += "&" + URLEncoder.encode("roomName", "UTF-8") + "=" + URLEncoder.encode(receiverUsername, "UTF-8");
            }
            else {
                link = "http://www.passtrunk.com/OOPAPI/fcmhandler.php";
                message = args[3];
                data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                data += "&" + URLEncoder.encode("from", "UTF-8") + "=" + URLEncoder.encode(senderId, "UTF-8");
                data += "&" + URLEncoder.encode("to", "UTF-8") + "=" + URLEncoder.encode(receiverUsername, "UTF-8");
                data += "&" + URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");

            }

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

        return response;
    }

    @Override
    protected void onPostExecute(String result) {

        if (action.equals("getPrivMsg") || action.equals("getGroupMsg")) {
        processFinish(result);
        }

    }

    private void processFinish(String results) {
        JSONObject res;
        JSONArray messages;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
        try {

            LinearLayout layout = (LinearLayout) view.findViewById(R.id.privMsgLayout);
            res = new JSONObject(results);
            messages = (JSONArray) res.get("data");

            for (int i = 0; i < messages.length(); i++) {
                JSONObject message = (JSONObject) messages.get(i);
                String from = message.getString("from");
                String msg = message.getString("message");
                String fromUser = "";
                int icon = 7;
                if (action.equals("getGroupMsg")) {
                    fromUser = message.getString("fromusername");
                    icon = Integer.valueOf(message.getString("fromusericon"));
                }
                if (from.equals(String.valueOf(pref.getInt("userid", 0)))) {
                    populateMsgFromMe(msg);
                } else {
                    populateReceivedMsg(msg, fromUser, icon);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateMsgFromMe(String msg) {

        View view = LayoutInflater.from(act).inflate(R.layout.msg_from_me, null);
        // LinearLayout layout = (LinearLayout) view.findViewById(R.id.privMsgLayout);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
        String toUsername = pref.getString("currentPrivUser", "");


        layout.addView(view);
        TextView msgFromMe = (TextView) view.findViewById(R.id.msgFromMeTxt);
        msgFromMe.setId(generateViewId());
        msgFromMe.setText(msg);
        ImageView icon = getIcon(pref.getInt("usericon", 7), R.id.messageFromMeIcon);

            layout.invalidate();


    }


    public void populateReceivedMsg(String msg, String fromUser, int ico) {


        View view = LayoutInflater.from(act).inflate(R.layout.msg_from_them, null);
        // LinearLayout layout = (LinearLayout) view.findViewById(R.id.privMsgLayout);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
        String toUsername = pref.getString("currentPrivUser", "");
        int privUserIcon = pref.getInt("currentPrivUserIcon", 7);

        layout.addView(view);
        TextView msgFromMe = (TextView) view.findViewById(R.id.msgFromThemTxt);
        ImageView icon;
        if (!(ico == 7)) {
            icon = getIcon(ico, R.id.msgFromThemIcon);
        } else {
            icon = getIcon(privUserIcon, R.id.msgFromThemIcon);
        }
        if (!fromUser.equals("")) {
            TextView userName = (TextView) view.findViewById(R.id.msgFromGroup);
            userName.setId(generateViewId());
            userName.setText(fromUser);
        }
        msgFromMe.setId(generateViewId());
        msgFromMe.setText(msg);
        layout.invalidate();


    }

    public ImageView getIcon(int icon, int id) {
        ImageView imageView = (ImageView) view.findViewById(id);
        imageView.setId(generateViewId());
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
}
