package com.example.sango.thegift;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class CardActivity extends AppCompatActivity {
    private Button mCamera;
    private ImageView mImg;
    private String fileName;
    private Button btnRecord;
    private Button btnPlay;
    private Button btnsend;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private long id;
    private int recordState;
    private String ipAddr;
    private Socket socket;
    private String imgPath;
    private String voicePath;
    private String cardName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = this.getIntent().getExtras();
        id = bundle.getLong("id");
        cardName = bundle.getString("cardName");
        int status = bundle.getInt("status");

        super.onCreate(savedInstanceState);

        if(status == 0) {
            setContentView(R.layout.activity_card);
        } else if(status == 1) {
            setContentView(R.layout.activity_cardlist);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recordState = 0;

        mImg = (ImageView) findViewById(R.id.img);
        mCamera = (Button) findViewById(R.id.camera);
        btnsend = (Button) findViewById(R.id.send);

        imgPath = FileUtil.getExternalStorageDir(FileUtil.APP_DIR) + "/" + id + ".jpg";
        voicePath = FileUtil.getExternalStorageDir(FileUtil.APP_DIR) + "/" + id + ".mp4";
        File pictureFile = new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                id + ".jpg");
        Uri uri = Uri.fromFile(pictureFile);
        fileName = uri.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
        mImg.setImageBitmap(bitmap);

        if(mCamera != null) {
            mCamera.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentCamera =
                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File pictureFile = new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                            id + ".jpg");
                    Uri uri = Uri.fromFile(pictureFile);
                    fileName = uri.getPath();
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intentCamera, 66);
                }
            });
        }

        btnRecord = (Button) findViewById(R.id.record);
        btnPlay = (Button) findViewById(R.id.play);

        if(btnRecord != null) {
            btnRecord.setOnClickListener(new Button.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View view) {
                    if (recordState == 0) {
                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mediaRecorder.setOutputFile(FileUtil.getExternalStorageDir(FileUtil.APP_DIR) + "/" + id + ".mp4");
                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        recordState = 1;
                        btnRecord.setBackground(getResources().getDrawable(R.mipmap.stop));
                    } else {

                        mediaRecorder.stop();
                        mediaRecorder.release();
                        recordState = 0;
                        btnRecord.setBackground(getResources().getDrawable(R.mipmap.audio2));
                    }
                }
            });
        }

        if(btnPlay != null) {
            btnPlay.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(FileUtil.getExternalStorageDir(FileUtil.APP_DIR) + "/" + id + ".mp4");
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mediaPlayer.stop();
                                mediaPlayer.release();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if(btnsend != null) {
            btnsend.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final View addCardView = LayoutInflater.from(CardActivity.this).inflate(R.layout.add_card, null);
                    new AlertDialog.Builder(CardActivity.this)
                            .setTitle(R.string.send_card)
                            .setView(addCardView)
                            .setPositiveButton(R.string.input_ip, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText editText = (EditText) addCardView.findViewById(R.id.card_name);
                                    ipAddr = editText.getText().toString();
                                    Thread test = new Thread(clientSocket);
                                    test.start();
                                }
                            }).show();
                }
            });
        }
    }

    Runnable clientSocket = new Runnable() {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(ipAddr);

                socket = new Socket(serverAddr, 1111);

                BufferedOutputStream bof = new BufferedOutputStream(socket.getOutputStream());
                bof.write(cardName.getBytes());
                bof.flush();
                bof.close();
                Log.w("Client: ", "Send1");

                socket.close();
                socket = new Socket(serverAddr, 1111);
                OutputStream outputStream = socket.getOutputStream();

                File myFile = new File(imgPath);
                if(myFile.exists()) {
                    byte[] mybytearray = new byte[(int) myFile.length()];
                    FileInputStream fis = new FileInputStream(myFile);

                    BufferedInputStream bis = new BufferedInputStream(fis, 8*1024);
                    bis.read(mybytearray, 0, mybytearray.length);
                    outputStream.write(mybytearray, 0, mybytearray.length);
                    outputStream.flush();
                    Log.w("Client: ", "Send2");
                }
                socket.close();
                socket = new Socket(serverAddr, 1111);
                outputStream = socket.getOutputStream();

                myFile = new File(voicePath);
                if(myFile.exists()) {
                    byte[] mybytearray = new byte[(int) myFile.length()];
                    FileInputStream fis = new FileInputStream(myFile);

                    BufferedInputStream bis = new BufferedInputStream(fis, 8*1024);
                    bis.read(mybytearray, 0, mybytearray.length);
                    outputStream.write(mybytearray, 0, mybytearray.length);
                    outputStream.flush();
                    Log.w("Client: ", "Send3");
                }
                socket.close();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == 66) {
                Bitmap bitmap = BitmapFactory.decodeFile(fileName);
                mImg.setImageBitmap(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
