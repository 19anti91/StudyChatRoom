package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;

/**
 * Created by no717490 on 3/1/2017.
 */

public class SubmitLoginAndSignup extends AsyncTask<String, Void, String> {

    private Context context;
    private Activity act;

    public SubmitLoginAndSignup(Context context, Activity act) {
        this.context = context;
        this.act = act;
    }

    protected String doInBackground(String... args) {

        String action = "";
        String fname = "";
        String lname = "";
        String username = "";
        String password = "";
        String emailaddress = "";
        String hashkey = "";
        String data = "";
        String usertype = "";
        String response = "";


        URL url = null;
        OutputStream outputPost = null;
        BufferedReader in = null;
        HttpURLConnection client = null;

        try {
            String link = "http://www.passtrunk.com/OOPAPI/test.php";
            MessageDigest md = MessageDigest.getInstance("SHA-256");


            if (args[0] == "login") {
                action = args[0];
                username = args[1];
                password = args[2];


                md.update(password.getBytes());
                byte byteData[] = md.digest();

                StringBuffer encPass = new StringBuffer();
                for (int i = 0; i < byteData.length; i++) {
                    encPass.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                }

                data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(encPass.toString(), "UTF-8");
            } else if (args[0] == "register") {
                action = args[0];
                fname = args[1];
                lname = args[2];
                username = args[3];
                emailaddress = args[4];
                password = args[5];
                usertype = args[6];
                hashkey = new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss'Z'").toString();

                if (username.equals("") || password.equals("") || fname.equals("") || lname.equals("") || emailaddress.equals("") || password.equals("")) {

                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(act, "Please fill all the fields", Toast.LENGTH_LONG).show();
                        }
                    });

                }
                md.update(password.getBytes());
                byte byteData[] = md.digest();

                StringBuffer encPass = new StringBuffer();
                for (int i = 0; i < byteData.length; i++) {
                    encPass.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                }

                data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(encPass.toString(), "UTF-8");
                data += "&" + URLEncoder.encode("fname", "UTF-8") + "=" + URLEncoder.encode(fname, "UTF-8");
                data += "&" + URLEncoder.encode("lname", "UTF-8") + "=" + URLEncoder.encode(lname, "UTF-8");
                data += "&" + URLEncoder.encode("emailAddress", "UTF-8") + "=" + URLEncoder.encode(emailaddress, "UTF-8");
                data += "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(hashkey, "UTF-8");
                data += "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(usertype, "UTF-8");
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


    protected void onPostExecute(String response) {
        JSONObject returnValues;

        int status;
        String statusMessage;
        String action;
        JSONObject data;
        try {
            returnValues = new JSONObject(response);
            status = Integer.valueOf(returnValues.getString("status"));
            statusMessage = returnValues.getString("statusMessage");
            data = returnValues.getJSONObject("data");
            action = data.getString("action");


            if (action.equals("login")) {
                SharedPreferences pref = this.context.getApplicationContext().getSharedPreferences("StudyChatRoom", 0);
                SharedPreferences.Editor editor = pref.edit();
                //int userID = Integer.valueOf(data.getString("userid"));

                //user logged in fine lets save the info on the shared preferences
                //status = 0 means no errors
                //status = 1 means wrong password
                //status = 3 means user not found
                if (status == 0) {
                    int userID = Integer.valueOf(data.getString("userid"));
                    Toast.makeText(act, statusMessage, Toast.LENGTH_LONG).show();
                    editor.putInt("userid", userID);
                    editor.putString("fname", data.getString("fname"));
                    editor.putString("lname", data.getString("lname"));
                    editor.putString("emailaddress", data.getString("emailaddress"));
                    editor.putString("username", data.getString("username"));
                    editor.putString("type", data.getString("type"));
                    editor.commit();
                } else if (status == 1 || status == 3) {
                    Toast.makeText(act, statusMessage, Toast.LENGTH_LONG).show();
                }
                //Inform the user the login as been sucessfull and store data on the pref settings
            } else if (action.equals("register")) {
                //get data back and make sure the registration was successfully
            }
            //TODO Preferences cookies
            //TODO check for status and do toast with info accordingly

            Log.d("data", data.toString());
        } catch (Exception e) {
            Log.d("Error onPostexecute", e.toString());
        }
    }


}

