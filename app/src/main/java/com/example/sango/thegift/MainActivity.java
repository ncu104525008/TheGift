package com.example.sango.thegift;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private ListView listView;
    private List<MyCard> myCards;
    private String[] list;
    private ArrayAdapter<String> listAdapter;
    private String cardName;
    private MyHandler myHandler;
    private MyServerSocket myThread;
    Notification mNotification = null;
    final int NOTIFICATION_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myCards = new ArrayList<>();
        db = MyDBHelper.getDatabase(MainActivity.this);

        Bundle bundle = this.getIntent().getExtras();
        final String type = bundle.getString("type");

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectCard(position+1);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                delCard(position, type);
                return true;
            }
        });

        showCard(type);

        if(type.equals("receive"))
        {
            myHandler = new MyHandler(MainActivity.this);
            myThread = new MyServerSocket(myHandler);
            new Thread(myThread).start();
        }
    }

    public static void start123() {

    }

    private class MyServerSocket implements Runnable {
        private MyHandler threadHandler;

        public MyServerSocket(MyHandler myHandler) {
            super();
            this.threadHandler = myHandler;
        }

        @Override
        public void run() {
            try {
                ServerSocket myServer = new ServerSocket(1111);
                while (true) {
                    Log.w("Server: ", "Connecting...");
                    BufferedInputStream in = null;
                    Log.w("Server: ", "Receiving1 start");

                    String data = "";
                    try {
                        Socket client = myServer.accept();
                        in = new BufferedInputStream(client.getInputStream());
                        byte[] b = new byte[1024];
                        int length;
                        while ((length = in.read(b)) > 0) {
                            Log.w("Server: ", "length:" + length);
                            data += new String(b, 0, length);
                        }
                        in.close();
                        client.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cardName = data;
                    Log.w("Server: ", "Receiving1 complete");
                    long newCardId = addCard();

                    String imgPath = FileUtil.getExternalStorageDir(FileUtil.APP_DIR) + "/" + newCardId + ".jpg";
                    String voicePath = FileUtil.getExternalStorageDir(FileUtil.APP_DIR) + "/" + newCardId + ".mp4";


                    try {
                        Socket client = myServer.accept();
                        Log.w("Server: ", "Receiving2 start");
                        OutputStream out = new FileOutputStream(imgPath);
                        byte buff[] = new byte[1024];
                        int len;

                        InputStream inputStream = client.getInputStream();
                        while ((len = inputStream.read(buff)) != -1) {
                            out.write(buff, 0, len);
                        }
                        out.close();
                        inputStream.close();
                        Log.w("Server: ", "Receiving2 complete");

                        Socket client2 = myServer.accept();
                        Log.w("Server: ", "Receiving3 start");
                        OutputStream out2 = new FileOutputStream(voicePath);
                        byte buff2[] = new byte[1024];
                        int len2;

                        InputStream inputStream2 = client2.getInputStream();
                        while ((len2 = inputStream2.read(buff2)) != -1) {
                            out2.write(buff2, 0, len2);
                        }
                        out2.close();
                        inputStream2.close();
                        Log.w("Server: ", "Receiving3 complete");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        Message msg = this.threadHandler.obtainMessage();
                        msg.getData().putString("msg", "receive");
                        threadHandler.sendMessage(msg);
                        setUpAsForeground("接收" + cardName);
                        Log.w("Server: ", "Done.");

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    void setUpAsForeground(String text) {

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification = new Notification.Builder(getApplicationContext())
                .setAutoCancel(true)
                .setContentTitle("Receive")
                .setContentText(text)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.giftcard)
                .setWhen(System.currentTimeMillis())
                .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, mNotification);


    }
    Runnable serverSocket = new Runnable() {
        @Override
        public void run() {
            try {
                Log.w("Server: ", "Connecting...");
                BufferedInputStream in = null;
                ServerSocket myServer = new ServerSocket(1111);
                    Log.w("Server: ", "Receiving1 start");

                    String data = "";
                    try {
                        Socket client = myServer.accept();
                        in = new BufferedInputStream(client.getInputStream());
                        byte[] b = new byte[1024];
                        int length;
                        while ((length = in.read(b)) > 0) {
                            Log.w("Server: ", "length:" + length);
                            data += new String(b, 0, length);
                        }
                        in.close();
                        client.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cardName = data;
                Log.w("Server: ", "Receiving1 complete");
                    long newCardId = addCard();

                    String imgPath = FileUtil.getExternalStorageDir(FileUtil.APP_DIR) + "/" + newCardId + ".jpg";
                    String voicePath = FileUtil.getExternalStorageDir(FileUtil.APP_DIR) + "/" + newCardId + ".mp4";


                    try {
                        Socket client = myServer.accept();
                        Log.w("Server: ", "Receiving2 start");
                        OutputStream out = new FileOutputStream(imgPath);
                        byte buff[] = new byte[1024];
                        int len;

                        InputStream inputStream = client.getInputStream();
                        while((len = inputStream.read(buff)) != -1) {
                            out.write(buff, 0, len);
                        }
                        out.close();
                        inputStream.close();
                        Log.w("Server: ", "Receiving2 complete");

                        Socket client2 = myServer.accept();
                        Log.w("Server: ", "Receiving3 start");
                        OutputStream out2 = new FileOutputStream(voicePath);
                        byte buff2[] = new byte[1024];
                        int len2;

                        InputStream inputStream2 = client2.getInputStream();
                        while((len2 = inputStream2.read(buff2)) != -1) {
                            out2.write(buff2, 0, len2);
                        }
                        out2.close();
                        inputStream2.close();
                        Log.w("Server: ", "Receiving3 complete");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        Log.w("Server: ", "Done.");
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private long addCard() {
        ContentValues cv = new ContentValues();
        cv.put(MyDBHelper.NAME_COLUMN, cardName);
        cv.put(MyDBHelper.STATUS_COLUMN, 1);

        long newCardId = db.insert(MyDBHelper.TABLE_NAME, null, cv);

        return newCardId;
    }

    public void showCard(String type) {
        Log.w("showCard: ", type);
        myCards.clear();
        String where = "";
        if(type.equals("list")) {
            where = MyDBHelper.STATUS_COLUMN + "=" + 0;
        } else if(type.equals("receive")) {
            where = MyDBHelper.STATUS_COLUMN + "=" + 1;
        }
        Cursor cursor = db.query(MyDBHelper.TABLE_NAME, null, where, null, null, null, null, null);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int status = cursor.getInt(2);

            MyCard card = new MyCard(id, name, status);
            myCards.add(card);
        }
        list = getCardList();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(listAdapter);
        Log.w("showCard: ", "end");
    }

    private String[] getCardList() {
        int size = myCards.size();
        String[] cardList;
        cardList = new String[size];

        for(int i=0;i<size;i++) {
            cardList[i] = myCards.get(i).getName();
        }

        return cardList;
    }

    private void delCard(int id, final String type) {
        final MyCard card = myCards.get(id);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("刪除卡片?")
                .setMessage("你確定要刪除 " + card.getName() + " 嗎?")
                .setPositiveButton("確定刪除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues cv = new ContentValues();

                        cv.put(MyDBHelper.STATUS_COLUMN, -1);
                        String where = MyDBHelper.KEY_ID + "=" + card.getId();
                        db.update(MyDBHelper.TABLE_NAME, cv, where, null);
                        showCard(type);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void selectCard(int id) {
        Intent intent = new Intent(MainActivity.this, CardActivity.class);
        Bundle bundle = new Bundle();
        int cid = myCards.get(id-1).getId();
        bundle.putLong("id", cid);
        cardName = myCards.get(id-1).getName();
        bundle.putString("cardName", cardName);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
