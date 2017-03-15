package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChatRoomsList extends AppCompatActivity {

    public CustomListAdapterChatRooms customListAdapter;
    public Activity act = this;
    public ListView chatRoomListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms_list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select Chat Room");
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        new SendDataAsync(this, this).execute("getAllChatRooms");

        chatRoomListView = (ListView) findViewById(R.id.chatRoomList);
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
            roomOwner.setText(chatRoomOwner[position]);
            roomMembers.setText(chatRoomMembers[position]);

            return rowView;

        }
    }
}
