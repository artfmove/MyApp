package com.android.artem.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class SongActivity extends AppCompatActivity {


    private TextView titleTextView, groupTextView, elapsedTimeLabel, remainingTimeLabel;
    private ImageView imageView;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String urlSong;
    private MediaPlayer mp;

    private SeekBar positionBar;
    private ImageView playButton;
    private int totalTime;

    private boolean isClosed;

    private CountDownTimer countDownTimer;
    private CreateMpAsyncTask createMpAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        titleTextView = findViewById(R.id.titleTextView);
        groupTextView = findViewById(R.id.groupTextView);
        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);
        imageView = findViewById(R.id.imageView);


        playButton = findViewById(R.id.playButton);
        positionBar = findViewById(R.id.positionBar);
        mp = new MediaPlayer();

        Intent intent = getIntent();
        urlSong = intent.getStringExtra("Id");
        titleTextView.setText(intent.getStringExtra("Title"));
        groupTextView.setText(intent.getStringExtra("Group"));
        Picasso.get().load(intent.getStringExtra("Image")).fit().centerInside()
                .into(imageView);

        CreateMpAsyncTask createMpAsyncTask = (CreateMpAsyncTask) new CreateMpAsyncTask().execute();



    }


    public void createMediaPlayer() {
        mp = MediaPlayer.create(getApplicationContext(), Uri.parse(urlSong));
        totalTime = mp.getDuration();
        if(!isClosed){
            mp.start();
            Log.e("media", "create");
            mp.setLooping(true);
            mp.seekTo(0);
            mp.setVolume(0.5f, 0.5f);

            Log.e("media", "start");
            //mp.pause();
            Log.e("media", "pause");
        /*float speed = 0.75f;
        mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(speed));*/

            remainingTimeLabel.setText(totalTime(totalTime));

            // Position Bar
            positionBar = findViewById(R.id.positionBar);
            positionBar.setMax(totalTime);
            positionBar.setOnSeekBarChangeListener(
                    new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                mp.seekTo(progress);
                                positionBar.setProgress(progress);
                            }
                            long progressInMillis = progress;
                            updateTimer(progressInMillis);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    }
            );
        }else{

            mp.release();
        }


    }

    public void setCountDownTimer(){
        countDownTimer = new CountDownTimer(totalTime, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!isClosed){
                    positionBar.setProgress(mp.getCurrentPosition());
                    Log.e("timer", "timer");
                }else{
                    countDownTimer.cancel();
                }

            }

            @Override
            public void onFinish() {

            }
        };

        countDownTimer.start();

    }


    private void updateTimer(long millisUntilFinished) {
        long minutes = millisUntilFinished / 1000 / 60;
        long seconds = millisUntilFinished / 1000 - (minutes * 60);

        String minutesString = "";
        String secondsString = "";

        if (minutes < 10) minutesString = "0" + minutes;
        else minutesString = String.valueOf(minutes);

        if (seconds < 10) secondsString = "0" + seconds;
        else secondsString = String.valueOf(seconds);

        elapsedTimeLabel.setText(minutesString + ":" + secondsString);
    }

    private String totalTime(long millisUntilFinished) {
        long minutes = millisUntilFinished / 1000 / 60;
        long seconds = millisUntilFinished / 1000 - (minutes * 60);

        String minutesString = "";
        String secondsString = "";

        if (minutes < 10) minutesString = "0" + minutes;
        else minutesString = String.valueOf(minutes);

        if (seconds < 10) secondsString = "0" + seconds;
        else secondsString = String.valueOf(seconds);

        return (minutesString + ":" + secondsString);
    }


    @Override
    protected void onStop() {
        super.onStop();

        isClosed=true;
        if(mp!=null){
            mp.release();
        }
        finish();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isClosed=true;
        if(mp!=null) {
            mp.release();
        }
        finish();
    }

    public void playButton(View view) {

        if (mp != null) {
            if (!mp.isPlaying()) {
                // Stopping
                mp.start();
                playButton.setImageResource(R.drawable.stop);

            } else {
                mp.pause();
                playButton.setImageResource(R.drawable.play);
            }
        }

    }


    private class CreateMpAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            createMediaPlayer();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setCountDownTimer();
        }
    }

}
