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

/**
 * Created by no717490 on 3/7/2017.
 */

public class SendDataAsync extends AsyncTask<String, Void, String> {
    private Context context;
    private Activity act;

    public SendDataAsync(Context context, Activity act) {
        this.context = context;
        this.act = act;
    }

    @Override
    protected String doInBackground(String... args) {

        String action = args[0];
        String userId = args[1];
        String token = "";
        //These values are used for generic reasons
        String value1 = "";
        String value2 = "";
        String value3 = "";

        URL url;
        OutputStream outputPost;
        BufferedReader in;
        HttpURLConnection client;
        String response = "";

        try {
            String link = "http://www.passtrunk.com/OOPAPI/general.php";
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
            data += "&" + URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
            if (action.equals("updateFireBaseToken")) {
                token = args[2];
                data += "&" + URLEncoder.encode("token", "UTF-8") + "=" + token;
            } else if (action.equals("updateFname")) {
                value1 = args[2];
                data += "&" + URLEncoder.encode("fname", "UTF-8") + "=" + value1;
            } else if (action.equals("updateLname")) {
                value1 = args[2];
                data += "&" + URLEncoder.encode("lname", "UTF-8") + "=" + value1;
            } else if (action.equals("updateEmail")) {
                value1 = args[2];
                data += "&" + URLEncoder.encode("emailaddress", "UTF-8") + "=" + value1;
            } else if (action.equals("updateNotif")) {
                value1 = args[2];
                data += "&" + URLEncoder.encode("enableNotif", "UTF-8") + "=" + value1;
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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
