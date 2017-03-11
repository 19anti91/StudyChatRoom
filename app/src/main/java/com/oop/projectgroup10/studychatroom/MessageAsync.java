package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * Created by Anti1991 on 3/11/2017.
 */

public class MessageAsync extends AsyncTask<String, Void, String> {

    Context context;
    Activity act;

    public MessageAsync(Context context, Activity act) {
        this.context = context;
        this.act = act;
    }

    @Override
    protected String doInBackground(String... args) {
        String action = args[0];
        String senderId = args[1];
        String receiverId = args[2];
        String message = args[3];


        URL url;
        OutputStream outputPost;
        BufferedReader in;
        HttpURLConnection client;
        MessageDigest md;
        String response = "";

        try {
            String link = "http://www.passtrunk.com/OOPAPI/messages.php";
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
            data += "&" + URLEncoder.encode("senderId", "UTF-8") + "=" + URLEncoder.encode(senderId, "UTF-8");
            data += "&" + URLEncoder.encode("receiverId", "UTF-8") + "=" + URLEncoder.encode(receiverId, "UTF-8");
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


        super.onPostExecute(result);
    }
}
