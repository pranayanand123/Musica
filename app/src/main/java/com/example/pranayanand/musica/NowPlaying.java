package com.example.pranayanand.musica;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeUnit;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.commit451.nativestackblur.NativeStackBlur;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;



public class NowPlaying extends AppCompatActivity {

    TextView musicTitle;
    TextView musicArtistName;
    ImageView nowPlayingImage;
    ImageButton btnRewind;
    ImageButton btnPlay;
    ImageButton btnForward;
    ImageButton next;
    ImageButton previous;
    static SeekBar songProgressBar;
    static Thread thread;
    private static Handler mHandler = new Handler();
    static TextView songCurrentDuration;
    static TextView songTotalDuration;
    private static Utilities utils;
    ImageView backgroundAlbum;
    private static int seekForwardTime=5000;
    private static int seekBackwardTime=5000;
    Integer songIndex;
    Bitmap bm;
    ImageButton playpauseCard;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.now_playing);
        updateProgressBar();


        musicTitle = (TextView) findViewById(R.id.musicTitle);
        backgroundAlbum = (ImageView) findViewById(R.id.backgroundAlbum);
        playpauseCard = (ImageButton) findViewById(R.id.playpauseCard);
        songCurrentDuration = (TextView) findViewById(R.id.songCurrentDuration);
        songTotalDuration = (TextView) findViewById(R.id.songTotalDuration);
        musicArtistName = (TextView) findViewById(R.id.musicArtistName);
        nowPlayingImage = (ImageView) findViewById(R.id.nowPlayingImage);
        btnRewind = (ImageButton) findViewById(R.id.btnRewind);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        next = (ImageButton) findViewById(R.id.next);
        previous = (ImageButton) findViewById(R.id.previous);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);




        utils = new Utilities();

        Intent intent = getIntent();

        songIndex = MainActivity.sf.getInt("position", 0);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/VarelaRound-Regular.ttf");
        musicTitle.setTypeface(typeface);
        musicArtistName.setTypeface(typeface);
        songCurrentDuration.setTypeface(typeface);
        songTotalDuration.setTypeface(typeface);
        loadScreen(songIndex);



        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    MainActivity.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.mediaPlayer.isPlaying()){
                    MainActivity.mediaPlayer.pause();
                    btnPlay.setBackgroundResource(R.drawable.playround);


                }
                else{
                    MainActivity.mediaPlayer.start();
                    btnPlay.setBackgroundResource(R.drawable.pauseround);

                }
            }
        });
        btnForward.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                // get current song position
                int currentPosition = MainActivity.mediaPlayer.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if (currentPosition + seekForwardTime <= MainActivity.mediaPlayer.getDuration()) {
                    // forward song
                    MainActivity.mediaPlayer.seekTo(currentPosition + seekForwardTime);
                } else {
                    // forward to end position
                    MainActivity.mediaPlayer.seekTo(MainActivity.mediaPlayer.getDuration());
                }

                return false;
            }
        });

        btnRewind.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int currentPosition = MainActivity.mediaPlayer.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    MainActivity.mediaPlayer.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    MainActivity.mediaPlayer.seekTo(0);
                }
                return false;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.mediaPlayer.stop();
                MainActivity.mediaPlayer = new MediaPlayer();
                if (songIndex+1<=MainActivity.songs.size()){
                    songIndex= songIndex+1;

                }


                try {
                    MainActivity.mediaPlayer.setDataSource(MainActivity.songs.get(songIndex).songUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MainActivity.mediaPlayer.prepareAsync();
                loadScreen(songIndex);
                btnPlay.setBackgroundResource(R.drawable.pauseround);
                playpauseCard.setBackgroundResource(R.drawable.pause);
                MainActivity.mediaPlayer.start();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.mediaPlayer.stop();
                MainActivity.mediaPlayer = new MediaPlayer();
                if (songIndex-1>=0){
                    songIndex= songIndex-1;

                }



                try {
                    MainActivity.mediaPlayer.setDataSource(MainActivity.songs.get(songIndex).songUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MainActivity.mediaPlayer.prepareAsync();
                loadScreen(songIndex);
                btnPlay.setBackgroundResource(R.drawable.pauseround);
                playpauseCard.setBackgroundResource(R.drawable.pause);
                MainActivity.mediaPlayer.start();

            }
        });











    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    public void loadScreen(int position){
        musicTitle.setText(MainActivity.songs.get(position).songName.replace(".mp3", ""));
        musicArtistName.setText(MainActivity.songs.get(position).artistName);

        Glide.with(getApplicationContext()).load(MainActivity.songs.get(position).albumUri).error(R.mipmap.ic_default).into(nowPlayingImage);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            bm = NativeStackBlur.process(MediaStore.Images.Media.getBitmap(this.getContentResolver(), MainActivity.songs.get(position).albumUri), 90);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);

        Glide.with(getApplicationContext())
                .load(stream.toByteArray())
                .asBitmap()
                .error(R.mipmap.ic_default)
                .into(backgroundAlbum);




    }

    /**
     * Update timer on seekbar
     * */
    public static void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private static Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            int totalDuration = MainActivity.mediaPlayer.getDuration();
            int currentDuration = MainActivity.mediaPlayer.getCurrentPosition();




            songCurrentDuration.setText(utils.milliSecondsToTimer(currentDuration));
            songTotalDuration.setText(utils.milliSecondsToTimer(totalDuration));







            //Log.d("Progress", ""+progress);
            songProgressBar.setMax(totalDuration);
            songProgressBar.setProgress(currentDuration);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };





}
