package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;

//this class lists all the chat rooms, and lets user join them
public class ChatRoomsTwoTabs extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static String hashPass(String pass) {

        StringBuilder encPass = new StringBuilder();
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(pass.getBytes());
            byte byteData[] = md.digest();


            for (int i = 0; i < byteData.length; i++) {
                encPass.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return encPass.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms_two_tabs);


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Chat Rooms");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_rooms_two_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        settingsItem.setVisible(false);
        return false;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_chat_rooms_list, container, false);
            final int section = getArguments().getInt(ARG_SECTION_NUMBER);
            CustomListAdapterChatRooms customListAdapter;
            final Activity act = getActivity();
            final ListView chatRoomListView;
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            final SharedPreferences.Editor editor = pref.edit();
            editor.putString("currentChatRoom", "");
            editor.apply();

            new SendDataAsync(this.getActivity(), this.getActivity()).execute("getAllChatRooms", String.valueOf(pref.getInt("userid", 0)));
            chatRoomListView = (ListView) rootView.findViewById(R.id.chatRoomList);

            String[] chatRoomName = {};
            String[] chatRoomOwner = {};
            Integer[] chatRoomMembers = {};
            Integer[] chatRoomOwnerId = {};
           Integer[] isRoomPriv = {};
            String[] passwords = {};
            JSONArray chatRoomList;

            try {
                if (section == 1) {
                    chatRoomList = new JSONArray(pref.getString("allChatRoomList", ""));
                } else {
                    chatRoomList = new JSONArray(pref.getString("myChatRoomList", ""));
                }

                chatRoomName = new String[chatRoomList.length()];
                chatRoomOwner = new String[chatRoomList.length()];
                chatRoomMembers = new Integer[chatRoomList.length()];
                chatRoomOwnerId = new Integer[chatRoomList.length()];
                isRoomPriv = new Integer[chatRoomList.length()];
                passwords = new String[chatRoomList.length()];

                for (int i = 0; i < chatRoomList.length(); i++) {
                    JSONObject room = (JSONObject) chatRoomList.get(i);
                    chatRoomName[i] = room.getString("chatroomname");
                    chatRoomOwner[i] = room.getString("chatroomownerusername");
                    chatRoomOwnerId[i] = Integer.valueOf(room.getString("chatroomownerid"));
                    chatRoomMembers[i] = Integer.valueOf(room.getString("chatroommembers"));
                    isRoomPriv[i] = Integer.valueOf(room.getString("chatispriv"));
                    passwords[i] = room.getString("chatroompass");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for(int i  = 0; i < chatRoomName.length; i++){
                new SendDataAsync(getActivity(), getActivity()).execute("getAllUsersFromChatRoom", String.valueOf(pref.getInt("userid", 0)), chatRoomName[i]);

            }


            customListAdapter = new CustomListAdapterChatRooms(act, chatRoomName, chatRoomOwner, chatRoomMembers, passwords);

            chatRoomListView.setAdapter(customListAdapter);
            final Integer[] ownerId = chatRoomOwnerId;
            final String[] pass = passwords;
            final Integer[] isPriv = isRoomPriv;
            chatRoomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                    final String roomName = chatRoomListView.getItemAtPosition(position).toString();

                    if (section == 1) {
                        final EditText password = new EditText(act);
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                        if (hashPass("").equals(pass[position])) {
                            new SendDataAsync(act, act).execute("joinChatRoom", String.valueOf(pref.getInt("userid", 0)), roomName);
                            editor.putString("currentChatRoom", roomName);
                            editor.putInt("currentChatRoomPriv",isPriv[position]);
                            editor.putInt("currentChatOwner",ownerId[position]);
                            editor.putString("currentChatRoomPass",pass[position]);
                            editor.apply();
                            //new SendDataAsync(getActivity(), getActivity()).execute("getAllUsersFromChatRoom", String.valueOf(pref.getInt("userid", 0)), roomName);

                            Intent goToChatRoom = new Intent(act, ChatRooms.class);
                            startActivity(goToChatRoom);
                        } else if(pref.getString("usersFrom"+roomName,"").contains(pref.getString("username",""))){
                            editor.putString("currentChatRoom", roomName);
                            editor.putInt("currentChatRoomPriv",isPriv[position]);
                            editor.putInt("currentChatOwner",ownerId[position]);
                            editor.putString("currentChatRoomPass",pass[position]);
                            editor.apply();
                            new SendDataAsync(getActivity(), getActivity()).execute("getAllUsersFromChatRoom", String.valueOf(pref.getInt("userid", 0)), roomName);
                            Intent goToChatRoom = new Intent(act, ChatRooms.class);
                            startActivity(goToChatRoom);
                        }else{
                            if(pref.getString("type","").equals("Administrator")){
                                //new SendDataAsync(act, act).execute("joinChatRoom", String.valueOf(pref.getInt("userid", 0)), roomName);
                                editor.putString("currentChatRoom",roomName);
                                editor.putInt("currentChatRoomPriv",isPriv[position]);
                                editor.putString("currentChatRoomPass",pass[position]);
                                editor.putInt("currentChatOwner",ownerId[position]);
                                editor.apply();
                                Intent goToChatRoom = new Intent(act, ChatRooms.class);
                                startActivity(goToChatRoom);
                            }

                            new AlertDialog.Builder(act)
                                    .setTitle("Join Chat Room?")
                                    .setMessage("This room is password protected. Please type in password")
                                    .setView(password)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (hashPass(password.getText().toString()).equals(pass[position])) {
                                                Toast.makeText(act, "Password Correct", Toast.LENGTH_SHORT).show();
                                                new SendDataAsync(act, act).execute("joinChatRoom", String.valueOf(pref.getInt("userid", 0)), roomName);
                                                editor.putString("currentChatRoom",roomName);
                                                editor.putInt("currentChatRoomPriv",isPriv[position]);
                                                editor.putString("currentChatRoomPass",pass[position]);
                                                editor.putInt("currentChatOwner",ownerId[position]);
                                                editor.apply();
                                                new SendDataAsync(getActivity(), getActivity()).execute("getAllUsersFromChatRoom", String.valueOf(pref.getInt("userid", 0)), roomName);

                                                Intent goToChatRoom = new Intent(act, ChatRooms.class);
                                                startActivity(goToChatRoom);

                                            } else {
                                                Toast.makeText(act, "Password Incorrect", Toast.LENGTH_SHORT).show();
                                            }
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

                    } else {
                        editor.putString("currentChatRoom",roomName);
                        editor.putInt("currentChatRoomPriv",isPriv[position]);
                        editor.putString("currentChatRoomPass",pass[position]);
                        editor.putInt("currentChatOwner",ownerId[position]);
                        editor.apply();
                        new SendDataAsync(getActivity(), getActivity()).execute("getAllUsersFromChatRoom", String.valueOf(pref.getInt("userid", 0)), roomName);
                        Intent goToChatRoom = new Intent(act, ChatRooms.class);
                        startActivity(goToChatRoom);
                    }
                }
            });


            return rootView;
        }
    }

    /***********************************************/
    public static class CustomListAdapterChatRooms extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] chatRoomName;
        private final String[] chatRoomOwner;
        private final Integer[] chatRoomMembers;
        private final String[] chatRoomPass;


        public CustomListAdapterChatRooms(Activity context, String[] chatRoomName, String[] chatRoomOwner, Integer[] chatRoomMembers, String[] chatRoomPass) {
            super(context, R.layout.chat_room_list, chatRoomName);

            this.context = context;
            this.chatRoomName = chatRoomName;
            this.chatRoomOwner = chatRoomOwner;
            this.chatRoomMembers = chatRoomMembers;
            this.chatRoomPass = chatRoomPass;
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.chat_room_list, null, true);

            TextView roomName = (TextView) rowView.findViewById(R.id.chatRoomNameList);
            TextView roomOwner = (TextView) rowView.findViewById(R.id.chatRoomOwnerList);
            TextView roomMembers = (TextView) rowView.findViewById(R.id.chatRoomMembersList);
            TextView roomPassProt = (TextView) rowView.findViewById(R.id.chatRoomPassProt);

            roomName.setText(chatRoomName[position]);
            roomOwner.setText("Owner: " + chatRoomOwner[position]);
            roomMembers.setText("Members: " + String.valueOf(chatRoomMembers[position]));
            if (chatRoomPass[position].equals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")) {
                roomPassProt.setText("Open");
            } else {
                roomPassProt.setText("Password Protected");
            }
            return rowView;

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Chat Rooms";
                case 1:
                    return "Your Chat Rooms";

            }
            return null;
        }
    }
}
