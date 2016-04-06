package com.example.sango.thegift;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private ListView listView;
    private List<MyCard> myCards;
    private String[] list;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myCards = new ArrayList<>();
        db = MyDBHelper.getDatabase(MainActivity.this);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    addCard();
                } else {
                    selectCard(position-1);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0) {
                    delCard(position - 1);
                }
                return true;
            }
        });

        showCard();
    }

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

    private void addCard() {
        final View addCardView = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_card, null);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.input_card_name)
                .setView(addCardView)
                .setPositiveButton(R.string.add_card, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) addCardView.findViewById(R.id.card_name);
                        String cardName = editText.getText().toString();
                        if (cardName.length() > 0) {
                            ContentValues cv = new ContentValues();
                            cv.put(MyDBHelper.NAME_COLUMN, cardName);
                            cv.put(MyDBHelper.STATUS_COLUMN, 0);

                            db.insert(MyDBHelper.TABLE_NAME, null, cv);
                            showCard();
                        }
                    }
                }).show();
    }

    private void showCard() {
        myCards.clear();
        String where = MyDBHelper.STATUS_COLUMN + "=" + 0;
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
    }

    private String[] getCardList() {
        int size = myCards.size() + 1;
        String[] cardList;
        cardList = new String[size];

        cardList[0] = "建立新卡片";

        for(int i=1;i<size;i++) {
            cardList[i] = myCards.get(i-1).getName();
        }

        return cardList;
    }

    private void delCard(int id) {
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
                        showCard();
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
        MyCard card = myCards.get(id);
        Log.w("selectCard: ", "id:" + card.getId() + ", name:" + card.getName());
    }
}
