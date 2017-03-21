package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatRoomsList extends AppCompatActivity {

    public CustomListAdapterChatRooms customListAdapter;
    public Activity act = this;
    public ListView chatRoomListView;

    //TODO fix having to go to dashboard to refresh listings
    //TODO fix navigation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms_list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select Chat Room");
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("currentChatRoom", "");
        editor.apply();
        new SendDataAsync(this, this).execute("getAllChatRooms", String.valueOf(pref.getInt("userid", 0)));
        chatRoomListView = (ListView) findViewById(R.id.chatRoomList);

        String[] chatRoomName = {};
        String[] chatRoomOwner = {};
        Integer[] chatRoomMembers = {};
        Integer[] chatRoomOwnerId = {};

        JSONArray chatRoomList;

        try {
            chatRoomList = new JSONArray(pref.getString("chatRoomList", ""));
            chatRoomName = new String[chatRoomList.length()];
            chatRoomOwner = new String[chatRoomList.length()];
            chatRoomMembers = new Integer[chatRoomList.length()];
            chatRoomOwnerId = new Integer[chatRoomList.length()];

            for (int i = 0; i < chatRoomList.length(); i++) {
                JSONObject room = (JSONObject) chatRoomList.get(i);
                chatRoomName[i] = room.getString("chatroomname");
                chatRoomOwner[i] = room.getString("chatroomownerusername");
                chatRoomOwnerId[i] = Integer.valueOf(room.getString("chatroomownerid"));
                chatRoomMembers[i] = Integer.valueOf(room.getString("chatroommembers"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        customListAdapter = new CustomListAdapterChatRooms(this, chatRoomName, chatRoomOwner, chatRoomMembers);

        chatRoomListView.setAdapter(customListAdapter);

        chatRoomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String roomName = chatRoomListView.getItemAtPosition(position).toString();
                Log.d("roomname", roomName);
                //TODO check if user is already on that room, if not do alert and ask
                new AlertDialog.Builder(act)
                        .setTitle("Join Chat Room?")
                        .setMessage("Do you want to join this Chat Room?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(R.drawable.ic_gear)
                        .show();

            }
        });

    }


    public class CustomListAdapterChatRooms extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] chatRoomName;
        private final String[] chatRoomOwner;
        private final Integer[] chatRoomMembers;


        public CustomListAdapterChatRooms(Activity context, String[] chatRoomName, String[] chatRoomOwner, Integer[] chatRoomMembers) {
            super(context, R.layout.chat_room_list, chatRoomName);

            this.context = context;
            this.chatRoomName = chatRoomName;
            this.chatRoomOwner = chatRoomOwner;
            this.chatRoomMembers = chatRoomMembers;
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.chat_room_list, null, true);

            TextView roomName = (TextView) rowView.findViewById(R.id.chatRoomNameList);
            TextView roomOwner = (TextView) rowView.findViewById(R.id.chatRoomOwnerList);
            TextView roomMembers = (TextView) rowView.findViewById(R.id.chatRoomMembersList);

            roomName.setText(chatRoomName[position]);
            roomOwner.setText("Owner: " + chatRoomOwner[position]);
            roomMembers.setText("Members: " + String.valueOf(chatRoomMembers[position]));

            return rowView;

        }
    }
}
