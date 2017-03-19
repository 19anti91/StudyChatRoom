package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatRooms extends AppCompatActivity {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static ViewGroup view;
    public static LinearLayout layout;
    public static Handler UIHandler;
    static boolean isActive = false;

    static {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    public ListView usersFound;
    public Activity act = this;
    private ReceiveMessageService mBoundService;
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

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    //TODO emojis
/*
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
        msgFromMe.setText(getEmojiByUnicode(0x1F60A));
        http://apps.timwhitlock.info/emoji/tables/unicode
    }*/
    public static void populateReceivedMsg(String msg, String from, Activity activity) {


        View view = LayoutInflater.from(activity).inflate(R.layout.msg_from_them, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
        String toUsername = pref.getString("currentPrivUser", "");

        if (!msg.isEmpty() && from.equals(toUsername)) {

            layout.addView(view);
            TextView msgFromMe = (TextView) view.findViewById(R.id.msgFromThemTxt);
            //int privUserIcon = pref.getInt("currentPrivUserIcon",7);
            ImageView icon = getIcon(pref.getInt("currentPrivUserIcon", 7), R.id.msgFromThemIcon, view);

            msgFromMe.setId(generateViewId());
            msgFromMe.setText(msg);
            layout.invalidate();

            final ScrollView scroll = (ScrollView) view.findViewById(R.id.scrollPriv);
            scroll.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll.fullScroll(View.FOCUS_DOWN);
                            }
                        }

            );

        }

    }

    public static ImageView getIcon(int icon, int id, View view) {
        ImageView imageView = (ImageView) view.findViewById(id);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Chat Rooms");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        getSupportActionBar().setTitle(pref.getString("currentChatRoom", ""));


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        settingsItem.setVisible(false);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_rooms, menu);
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

    public void sendMessage(View v) {

        View view = LayoutInflater.from(this).inflate(R.layout.msg_from_me, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String toGroup = pref.getString("currentChatRoom", "");

        if (!getMessage().isEmpty()) {
            new MessageAsync(getApplicationContext(), this, view, layout).execute("groupMsg", String.valueOf(pref.getInt("userid", 0)), toGroup, getMessage());
            layout.addView(view);
            TextView msgFromMe = (TextView) findViewById(R.id.msgFromMeTxt);
            msgFromMe.setId(generateViewId());
            msgFromMe.setText(getMessage());
            ImageView icon = getIcon(pref.getInt("usericon", 0), R.id.messageFromMeIcon, view);
            layout.invalidate();
            EditText msgToSend = (EditText) findViewById(R.id.msgToSend);
            msgToSend.setText("");

            final ScrollView scroll = (ScrollView) findViewById(R.id.scrollPriv);
            scroll.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll.fullScroll(View.FOCUS_DOWN);
                            }
                        }

            );

        }
    }

    public void recieveMessage(View v) {
        new MessageAsync(this.getApplicationContext(), this, view, layout).execute();
    }

    public String getMessage() {
        String msg;
        EditText msgToSend = (EditText) findViewById(R.id.msgToSend);
        msg = msgToSend.getText().toString();

        return msg;
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

            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SharedPreferences.Editor edit = pref.edit();

            final View rootView;// = inflater.inflate(R.layout.fragment_chat_rooms, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            int section = getArguments().getInt(ARG_SECTION_NUMBER);
            //Members
            if (section == 1) {
                rootView = inflater.inflate(R.layout.activity_private_message, container, false);


            } else if (section == 2) {
                rootView = inflater.inflate(R.layout.activity_private_message, container, false);

                layout = (LinearLayout) rootView.findViewById(R.id.privMsgLayout);
                view = (ViewGroup) rootView.findViewById(R.id.privMsgLayout);

                new MessageAsync(getActivity(), getActivity(), view, layout).execute("getGroupMsg", String.valueOf(pref.getInt("userid", 0)), pref.getString("currentChatRoom", ""));
                ImageView send = (ImageView) rootView.findViewById(R.id.sendMsgBtn);
                //TODO figure out listener for image
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUI(new Runnable() {
                            @Override
                            public void run() {

                                if (pref.getInt("hasMessage", 0) == 1) {

                                    populateReceivedMsg(pref.getString("message", ""), pref.getString("userFrom", ""), getActivity());
                                    Log.e("TEST", pref.getString("message", "a"));

                                    edit.putInt("hasMessage", 0);
                                    edit.apply();

                                }


                            }
                        });

                    }
                }, 0, 500);



            } else {
                rootView = inflater.inflate(R.layout.activity_private_message, container, false);

            }


            return rootView;
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
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Members";
                case 1:
                    return "Chat Room";
                case 2:
                    return "Manage Chat Room";
            }
            return null;
        }
    }
}
