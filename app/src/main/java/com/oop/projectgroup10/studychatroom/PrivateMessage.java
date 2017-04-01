package com.oop.projectgroup10.studychatroom;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

//this class shows the private message activity and handles all related to it
public class PrivateMessage extends AppCompatActivity {


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static Context context;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    static boolean isActive = false;
    public ListView usersFound;
    ViewGroup view;

    Activity act = this;
    LinearLayout layout;


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
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }


    private static final int READ_REQUEST_CODE = 42;

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {


        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            String displayName = "";
            if (resultData != null) {
                uri = resultData.getData();
                Cursor cursor = this.getContentResolver()
                        .query(uri, null, null, null, null, null);

                try {

                    if (cursor != null && cursor.moveToFirst()) {

                        displayName = cursor.getString(
                                cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                    }
                } finally {
                    cursor.close();
                }
                File fileToUpload = new File(getPath(this,uri));

                uploadToS3(fileToUpload,displayName);
                Log.i("?????????", "Uri: " + uri.toString());

            }
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_message);
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor edit = pref.edit();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Private Conversation with " + pref.getString("currentPrivUser", ""));
        actionBar.setDisplayHomeAsUpEnabled(true);

        verifyStoragePermissions(this);


        layout = (LinearLayout) findViewById(R.id.privMsgLayout);
        view = (ViewGroup) findViewById(R.id.privMsgLayout);

        new MessageAsync(this.getApplicationContext(), this, view, layout).execute("getPrivMsg", String.valueOf(pref.getInt("userid", 0)), pref.getString("currentPrivUser", ""));
        ImageView attach = (ImageView)findViewById(R.id.attach);
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });

        final ImageView sendMessage = (ImageView)findViewById(R.id.sendMsgBtn);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(view,"");
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (pref.getInt("hasMessage", 0) == 1) {

                            populateReceivedMsg(pref.getString("message", ""), pref.getString("userFrom", ""));
                            Log.e("TEST", pref.getString("message", "a"));

                            edit.putInt("hasMessage", 0);
                            edit.apply();

                        }


                    }
                });

            }
        }, 0, 500);

        SimpleImageArrayAdapter adapter =new SimpleImageArrayAdapter(this,new Integer[]{
                0x0,
                0x1F602,
                0x1F603,
                0x1F606,
                0x1F60B,
                0x1F61D,
                0x1F61C,
                0x1F620,
                0x2705,
                0x270C,
                0x1F601
        });

        final Spinner emojiSpinner = (Spinner)findViewById(R.id.emojiSpinner);

        emojiSpinner.setAdapter(adapter);
        emojiSpinner.setSelection(-1);
        emojiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer emojis [] = new Integer[11];
                emojis[0] = 0x0;
                emojis[1] = 0x1F602;
                emojis[2] = 0x1F603;
                emojis[3] = 0x1F606;
                emojis[4] = 0x1F60B;
                emojis[5] = 0x1F61D;
                emojis[6] = 0x1F61C;
                emojis[7] = 0x1F620;
                emojis[8] = 0x2705;
                emojis[9] = 0x270C;
                emojis[10] = 0x1F601;
                EditText msg = (EditText)findViewById(R.id.msgToSend);
                msg.setText(msg.getText().toString()+ new String(Character.toChars(emojis[position])));
                emojiSpinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }


            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    void uploadToS3(File file, String filename){

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:6bad921a-feca-4b70-b3d0-0d217a6b1d2c", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));

        TransferObserver observer = transferUtility.upload(
                "studychatroom","documents/"+filename,file

        );
        String path = "https://s3.amazonaws.com/studychatroom/documents/"+filename;

        sendMessage(view, path);


    }

    public class SimpleImageArrayAdapter extends ArrayAdapter<Integer> {
        private Integer[] images;

        public SimpleImageArrayAdapter(Context context, Integer[] images) {
            super(context, android.R.layout.simple_spinner_item, images);
            this.images = images;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getImageForPosition(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getImageForPosition(position);
        }

        private View getImageForPosition(int position) {
            TextView emoji = new TextView(getBaseContext());
            emoji.setTextSize(25);
            emoji.setText(new String (Character.toChars(images[position])));
            return emoji;
        }

    }

    public void sendMessage(View v, final String path) {

        View view = LayoutInflater.from(this).inflate(R.layout.msg_from_me, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String toUsername = pref.getString("currentPrivUser", "");

        if (!getMessage().isEmpty() && path.equals("")) {
            new MessageAsync(getApplicationContext(), this, view, layout).execute("privMsg", String.valueOf(pref.getInt("userid", 0)), toUsername, getMessage());
            layout.addView(view);
            TextView msgFromMe = (TextView) findViewById(R.id.msgFromMeTxt);
            msgFromMe.setId(generateViewId());
            msgFromMe.setText(getMessage());
            ImageView icon = getIcon(pref.getInt("usericon", 0), R.id.messageFromMeIcon);
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
        if(!path.equals("")){
            String name = path.split("/")[5];
            new MessageAsync(getApplicationContext(), this, view, layout).execute("privMsg", String.valueOf(pref.getInt("userid", 0)), toUsername, path);
            layout.addView(view);
            TextView msgFromMe = (TextView) findViewById(R.id.msgFromMeTxt);
            msgFromMe.setId(generateViewId());
            msgFromMe.setText(name + " has been attached" + new String(Character.toChars(0x1F4CE)));
            msgFromMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new getFileFromAmazonTask().execute(path);
                }
            });
            ImageView icon = getIcon(pref.getInt("usericon", 0), R.id.messageFromMeIcon);
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


    private class getFileFromAmazonTask extends AsyncTask<String, Void,Void>{
        @Override
        protected Void doInBackground(String ...params){
            String name = params[0].split("/")[5];
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try{
                URL urlData = new URL(params[0]);

                connection = (HttpURLConnection) urlData.openConnection();
                connection.connect();

                input = connection.getInputStream();
                output = new FileOutputStream(Environment.getExternalStorageDirectory()+"/studychatroom/"+name);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {

                    output.write(data, 0, count);
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                try{
                    if(input!=null){
                        input.close();
                    }
                    if(output!=null){
                        output.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
    }
    }


    public void populateReceivedMsg(final String msg, String from) {


        View view = LayoutInflater.from(this).inflate(R.layout.msg_from_them, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String toUsername = pref.getString("currentPrivUser", "");

        if (!msg.isEmpty() && from.equals(toUsername)) {

            layout.addView(view);
            TextView msgFromMe = (TextView) findViewById(R.id.msgFromThemTxt);
            //int privUserIcon = pref.getInt("currentPrivUserIcon",7);
            ImageView icon = getIcon(pref.getInt("currentPrivUserIcon", 7), R.id.msgFromThemIcon);

            final String message = URLDecoder.decode(msg);
            Log.e("AAAAA", message);
            msgFromMe.setId(generateViewId());

            if(message.split("/")[0].equals("https:") && message.split("/")[2].equals("s3.amazonaws.com")){
                msgFromMe.setText(message.split("/")[5] + " has been attached" + new String(Character.toChars(0x1F4CE)));
                msgFromMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new getFileFromAmazonTask().execute(message);
                    }
                });
            }else{
            msgFromMe.setText(msg);
            }
            layout.invalidate();

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

    public ImageView getIcon(int icon, int id) {
        ImageView imageView = (ImageView) findViewById(id);
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



    public void recieveMessage(View v) {
        new MessageAsync(this.getApplicationContext(), this, this.view, layout).execute();
    }

    public String getMessage() {
        String msg;
        EditText msgToSend = (EditText) findViewById(R.id.msgToSend);
        msg = msgToSend.getText().toString();

        return msg;
    }

}