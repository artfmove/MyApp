package com.android.artem.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
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


import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

import java.io.IOException;

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
    //private CreateMpAsyncTask createMpAsyncTask;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private PlayerView playerView;
    private SimpleExoPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);



        /*titleTextView = findViewById(R.id.titleTextView);
        groupTextView = findViewById(R.id.groupTextView);*/
        /*elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);
        imageView = findViewById(R.id.imageView);*/

        playerView = findViewById(R.id.video_view);

        /*playButton = findViewById(R.id.playButton);
        positionBar = findViewById(R.id.positionBar);*/
        /*mp = new MediaPlayer();*/

        Intent intent = getIntent();
        urlSong = intent.getStringExtra("Id");
        /*titleTextView.setText(intent.getStringExtra("Title"));
        groupTextView.setText(intent.getStringExtra("Group"));*/
        /*Picasso.get().load(intent.getStringExtra("Image")).fit().centerInside()
                .into(imageView);*/

        //CreateMpAsyncTask createMpAsyncTask = (CreateMpAsyncTask) new CreateMpAsyncTask().execute();



    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        Uri uri = Uri.parse(urlSong);
        MediaSource mediaSource = buildMediaSource(uri);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                /*| View.SYSTEM_UI_FLAG_FULLSCREEN*/
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }


    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    public void createMediaPlayer() throws IOException {



                /*totalTime = mp.getDuration();
                if (!isClosed) {*/

                    /*mp.setLooping(true);
                    mp.seekTo(0);
                    mp.setVolume(0.5f, 0.5f);*/
        /*float speed = 0.75f;
        mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(speed));*/
/*
                    remainingTimeLabel.setText(totalTime(totalTime));
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
                    setCountDownTimer();

                } else {

                }*/









    }

    /*public void setCountDownTimer() {
        countDownTimer = new CountDownTimer(totalTime, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isClosed) {
                    positionBar.setProgress(mp.getCurrentPosition());
                    Log.e("timer", "timer");
                } else {
                    countDownTimer.cancel();
                }

            }

            @Override
            public void onFinish() {

            }
        };

        countDownTimer.start();

    }*/


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


    /*@Override
    protected void onStop() {
        super.onStop();

        isClosed = true;
        if (mp != null) {
            mp.release();
        }
        finish();


    }*/

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        isClosed = true;
        if (mp != null) {
            mp.release();
        }
        finish();
    }*/

    /*public void playButton(View view) {

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


    private class CreateMpAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                createMediaPlayer();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }*/

}
