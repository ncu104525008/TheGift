package com.example.sango.thegift;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private ImageButton sendBtn,receiveBtn,listBtn;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = MyDBHelper.getDatabase(WelcomeActivity.this);
        sendBtn = (ImageButton) findViewById(R.id.ButtonSend);
        receiveBtn = (ImageButton) findViewById(R.id.ButtonReceive);
        listBtn = (ImageButton) findViewById(R.id.ButtonList);
        sendBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCard();
            }
        });
        receiveBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "receive");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        listBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "list");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void addCard() {
        final View addCardView = LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.add_card, null);
        new AlertDialog.Builder(WelcomeActivity.this)
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

                            long newCardId = db.insert(MyDBHelper.TABLE_NAME, null, cv);
                            Intent intent = new Intent(WelcomeActivity.this, CardActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putLong("id", newCardId);
                            bundle.putString("cardName", cardName);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                }).show();
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
}
