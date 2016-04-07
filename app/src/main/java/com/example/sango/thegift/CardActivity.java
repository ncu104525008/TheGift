package com.example.sango.thegift;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class CardActivity extends AppCompatActivity {
    private Button mCamera;
    private ImageView mImg;
    private String fileName;
    private Button btnRecord;
    private Button btnPlay;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private long id;
    private int recordState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recordState = 0;

        mImg = (ImageView) findViewById(R.id.img);
        mCamera = (Button) findViewById(R.id.camera);

        Bundle bundle = this.getIntent().getExtras();
        id = bundle.getLong("id");
        File pictureFile = new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                id + ".jpg");
        Uri uri = Uri.fromFile(pictureFile);
        fileName = uri.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
        mImg.setImageBitmap(bitmap);

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

        btnRecord = (Button) findViewById(R.id.record);
        btnPlay = (Button) findViewById(R.id.play);

        btnRecord.setOnClickListener(new Button.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                if(recordState == 0) {
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
