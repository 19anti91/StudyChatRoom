package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * Created by no717490 on 3/7/2017.
 */

public class SendDataAsync extends AsyncTask<String, Void, String> {
    String action;
    private Context context;
    private Activity act;
    public SendDataAsync(Context context, Activity act) {
        this.context = context;
        this.act = act;
    }

    @Override
    protected String doInBackground(String... args) {

        action = args[0];
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
        MessageDigest md;
        String response = "";
        String link;
        try {
            link = "http://www.passtrunk.com/OOPAPI/general.php";
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
            } else if (action.equals("createRoom")) {
                md = MessageDigest.getInstance("SHA-256");

                value1 = args[2];
                value2 = args[3];
                value3 = args[4];
                md.update(value2.getBytes());
                byte byteData[] = md.digest();

                StringBuilder encPass = new StringBuilder();
                for (int i = 0; i < byteData.length; i++) {
                    encPass.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                }

                data += "&" + URLEncoder.encode("roomName", "UTF-8") + "=" + value1;
                data += "&" + URLEncoder.encode("roomPassword", "UTF-8") + "=" + encPass.toString();
                data += "&" + URLEncoder.encode("isPrivate", "UTF-8") + "=" + value3;

            } else if (action.equals("joinChatRoom")) {
                value1 = args[2];

                data += "&" + URLEncoder.encode("roomName", "UTF-8") + "=" + value1;


            } else if (action.equals("getAllUsersFromChatRoom")) {
                value1 = args[2];
                data += "&" + URLEncoder.encode("roomName", "UTF-8") + "=" + value1;
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
    protected void onPostExecute(String response) {

        JSONObject returnValues;

        int status;
        String statusMessage;

        JSONObject data = null;
        JSONArray userList;
        JSONArray myChatRoomList;
        JSONArray allChatRoomList;
        JSONObject allRooms;
        JSONArray allUsersFromRoom;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = pref.edit();
        try {
            returnValues = new JSONObject(response);
            status = Integer.valueOf(returnValues.getString("status"));
            statusMessage = returnValues.getString("statusMessage");


            if (action.equals("createRoom")) {
                data = returnValues.getJSONObject("data");
                if (status == 0) {
                    Toast.makeText(act, "Room Created Successfully", Toast.LENGTH_LONG).show();
                    act.finish();
                } else if (status == 1) {
                    Toast.makeText(act, "The Room name is taken", Toast.LENGTH_LONG).show();
                }
            } else if (action.equals("getAllUsers")) {
                userList = returnValues.getJSONArray("data");
                editor.putString("userList", userList.toString());
                editor.apply();
            } else if (action.equals("getAllChatRooms")) {
                allRooms = new JSONObject(returnValues.getString("data"));
                myChatRoomList = allRooms.getJSONArray("myChatRooms");
                allChatRoomList = allRooms.getJSONArray("allChatRooms");
                editor.putString("myChatRoomList", myChatRoomList.toString());
                editor.putString("allChatRoomList", allChatRoomList.toString());
                editor.apply();
            } else if (action.equals("getAllUsersFromChatRoom")) {
                allUsersFromRoom = new JSONArray(returnValues.getString("data"));
                editor.putString("usersFromChatRoom", allUsersFromRoom.toString());
                editor.apply();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

