package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceID;

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

    String rememberMe;
    protected String doInBackground(String... args) {

        String action;
        String fname;
        String lname;
        String username;
        String password;
        String emailaddress;
        String hashkey;
        String data = "";
        String usertype;
        String response = "";


        URL url;
        OutputStream outputPost;
        BufferedReader in;
        HttpURLConnection client;

        MessageDigest md;

        try {
            String link = "http://www.passtrunk.com/OOPAPI/regandsign.php";
            md = MessageDigest.getInstance("SHA-256");
            password = args[2];

            md.update(password.getBytes());
            byte byteData[] = md.digest();

            StringBuilder encPass = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                encPass.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            if (args[0] == "login") {
                action = args[0];
                username = args[1];
                rememberMe = args[3];


                data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(encPass.toString(), "UTF-8");

            } else if (args[0] == "register") {
                action = args[0];
                username = args[1];
                fname = args[3];
                lname = args[4];
                emailaddress = args[5];
                usertype = args[6];
                hashkey = new SimpleDateFormat().toString();



                data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
                data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(encPass.toString(), "UTF-8");
                data += "&" + URLEncoder.encode("fname", "UTF-8") + "=" + URLEncoder.encode(fname, "UTF-8");
                data += "&" + URLEncoder.encode("lname", "UTF-8") + "=" + URLEncoder.encode(lname, "UTF-8");
                data += "&" + URLEncoder.encode("emailAddress", "UTF-8") + "=" + URLEncoder.encode(emailaddress, "UTF-8");
                data += "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(hashkey, "UTF-8");
                data += "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(usertype, "UTF-8");
                Log.e("Link", data);
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
                final SharedPreferences pref = this.context.getApplicationContext().getSharedPreferences("StudyChatRoom", 0);
                SharedPreferences.Editor editor = pref.edit();
                final String uID = data.getString("userid");

                //user logged in fine lets save the info on the shared preferences
                //status = 0 means no errors
                //status = 1 means wrong password
                //status = 2 means user not found
                //status = 3 means username taken(on register page)
                //status = 4 means email taken (on register page)
                //status = 5 means wrong email address
                if (status == 0) {
                    int userID = Integer.valueOf(data.getString("userid"));
                    Toast.makeText(act, statusMessage, Toast.LENGTH_LONG).show();
                    editor.putInt("userid", userID);
                    editor.putString("fname", data.getString("fname"));
                    editor.putString("lname", data.getString("lname"));
                    editor.putString("emailaddress", data.getString("emailaddress"));
                    editor.putString("username", data.getString("username"));
                    editor.putString("type", data.getString("type"));
                    editor.putString("notifsettings", data.getString("notifsettings"));
                    if (rememberMe.equals("true")) {
                        editor.putInt("rememberme", 1);
                    }
                    editor.apply();

                    //Force ID refresh on sign in
                    try {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String fbinstance = InstanceID.getInstance(context).getId();

                                String authorizeEnt = "338611432116";
                                String scope = "GCM";
                                try {
                                    InstanceID.getInstance(context).deleteInstanceID();
                                    String newIID = InstanceID.getInstance(context).getId();
                                    String token = InstanceID.getInstance(context).getToken(authorizeEnt, scope);

                                    new SendDataAsync(context, act).execute("updateFireBaseToken", uID, token);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //

                    Intent goToDash = new Intent(context, DashBoard.class);
                    context.startActivity(goToDash);
                } else if (status == 1 || status == 2) {
                    Toast.makeText(act, statusMessage, Toast.LENGTH_LONG).show();
                }
                //Inform the user the login as been successful and store data on the pref settings
            } else if (action.equals("register")) {

                if (status == 0) {
                    Toast.makeText(act, statusMessage, Toast.LENGTH_LONG).show();
                    act.finish();
                } else if (status == 3) {
                    Toast.makeText(act, statusMessage, Toast.LENGTH_LONG).show();
                } else if (status == 4) {
                    Toast.makeText(act, statusMessage, Toast.LENGTH_LONG).show();
                } else if (status == 5) {
                    Toast.makeText(act, statusMessage, Toast.LENGTH_LONG).show();
                }


            }


            Log.d("data", data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

