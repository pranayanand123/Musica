package com.example.pranayanand.musica;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.commit451.nativestackblur.NativeStackBlur;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.jar.Manifest;


import static com.example.pranayanand.musica.R.id.sliding_layout;
import static com.example.pranayanand.musica.R.id.up;

public class MainActivity extends AppCompatActivity {

    static ArrayList<SongInfo> songs = new ArrayList<SongInfo>();
    private SlidingUpPanelLayout slidingUpPanelLayout;
    RecyclerView recyclerView;
    static SeekBar seekBar;
    SongAdapter songAdapter;

    static MediaPlayer mediaPlayer;
    ImageView albumArt;
    static SharedPreferences sf;
    static SharedPreferences.Editor editor;
    static ImageView backgroundAlbumMain;
    TextView songNameCard;
    CardView card;
    TextView artistNameCard;
    ImageButton playpauseCard;

    //NowPlaying//
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








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        sf = getSharedPreferences("positionsf", MODE_PRIVATE);



        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);











        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        albumArt = (ImageView) findViewById(R.id.albumArt);



        backgroundAlbumMain = (ImageView) findViewById(R.id.backgroundAlbumMain);
        musicTitle = (TextView) findViewById(R.id.musicTitle);
        backgroundAlbum = (ImageView) findViewById(R.id.backgroundAlbum);

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
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/VarelaRound-Regular.ttf");
        musicTitle.setTypeface(typeface);
        musicArtistName.setTypeface(typeface);
        songCurrentDuration.setTypeface(typeface);
        songTotalDuration.setTypeface(typeface);




        utils = new Utilities();

        
        
        









//        SongInfo s = new SongInfo("Cheap Thrills", "Sia", "https://sd.ganamirchi.in/files/sfd82/40651/Sia%20-%20Cheap%20Thrills%20(ft.%20Sean%20Paul)%20-%20128%20Kbps.mp3");
//        songs.add(s);
        songAdapter = new SongAdapter(this, songs);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songAdapter);

        songAdapter.setOnitemClickListener(new SongAdapter.OnitemClickListener() {
            @Override
            public void onItemClick(final LinearLayout b, View v, final SongInfo obj, int position) {
                songIndex=position;

                editor = sf.edit();

                editor.putInt("position", songIndex);
                editor.commit();
                loadScreen(songIndex);

                if (mediaPlayer!=null && mediaPlayer.isPlaying()){

                    mediaPlayer.pause();

                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(obj.songUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();

                            updateProgressBar();

                        }
                    });





                }else {



                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(obj.songUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                            btnPlay.setBackgroundResource(R.drawable.pauseround);
                            updateProgressBar();


                        }
                    });







                }


            }
        });












        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
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
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    btnPlay.setBackgroundResource(R.drawable.playround);


                }
                else{
                    mediaPlayer.start();
                    btnPlay.setBackgroundResource(R.drawable.pauseround);

                }
            }
        });
        btnForward.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                // get current song position
                int currentPosition = mediaPlayer.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()) {
                    // forward song
                    mediaPlayer.seekTo(currentPosition + seekForwardTime);
                } else {
                    // forward to end position
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                }

                return false;
            }
        });

        btnRewind.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    mediaPlayer.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    mediaPlayer.seekTo(0);
                }
                return false;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer = new MediaPlayer();
                if (songIndex+1<=songs.size()){
                    songIndex= songIndex+1;

                }


                try {
                    mediaPlayer.setDataSource(songs.get(songIndex).songUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        btnPlay.setBackgroundResource(R.drawable.pauseround);

                        mediaPlayer.start();
                        updateProgressBar();

                    }
                });
                loadScreen(songIndex);

            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer = new MediaPlayer();
                if (songIndex-1>=0){
                    songIndex= songIndex-1;

                }



                try {
                    mediaPlayer.setDataSource(songs.get(songIndex).songUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        btnPlay.setBackgroundResource(R.drawable.pauseround);

                        mediaPlayer.start();
                        updateProgressBar();

                    }
                });
                loadScreen(songIndex);


            }
        });


        CheckPermissions();







        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                loadScreen(songIndex);


            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {


            }
        });








    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final ArrayList<SongInfo> tempList= new ArrayList<SongInfo>();
                for (SongInfo temp:songs){
                    if (temp.songName.toLowerCase().contains(newText.toLowerCase())){
                        tempList.add(temp);
                    }
                }

                SongAdapter adapter = new SongAdapter(getApplicationContext(), tempList);
                recyclerView.setAdapter(adapter);
                adapter.setOnitemClickListener(new SongAdapter.OnitemClickListener() {
                    @Override
                    public void onItemClick(final LinearLayout b, View v, final SongInfo obj, int position) {
                        songIndex=songs.indexOf(tempList.get(position));

                        editor = sf.edit();

                        editor.putInt("position", songIndex);
                        editor.commit();
                        loadScreen(songIndex);

                        if (mediaPlayer!=null && mediaPlayer.isPlaying()){

                            mediaPlayer.pause();

                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(obj.songUrl);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    mediaPlayer.start();

                                    updateProgressBar();

                                }
                            });





                        }else {



                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(obj.songUrl);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    mediaPlayer.start();
                                    btnPlay.setBackgroundResource(R.drawable.pauseround);
                                    updateProgressBar();


                                }
                            });







                        }


                    }
                });



                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (slidingUpPanelLayout!=null && (slidingUpPanelLayout.getPanelState()==SlidingUpPanelLayout.PanelState.EXPANDED)|| slidingUpPanelLayout.getPanelState()==SlidingUpPanelLayout.PanelState.ANCHORED){
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else {
            super.onBackPressed();

        }



    }

    public void loadScreen(int position){
        Glide.with(getApplicationContext()).load(songs.get(position).albumUri).error(R.mipmap.ic_default).into(nowPlayingImage);
        musicTitle.setText(songs.get(position).songName.replace(".mp3", ""));
        musicArtistName.setText(songs.get(position).artistName);









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
            int totalDuration = mediaPlayer.getDuration();
            int currentDuration = mediaPlayer.getCurrentPosition();




            songCurrentDuration.setText(utils.milliSecondsToTimer(currentDuration));
            songTotalDuration.setText(utils.milliSecondsToTimer(totalDuration));







            //Log.d("Progress", ""+progress);
            songProgressBar.setMax(totalDuration);
            songProgressBar.setProgress(currentDuration);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };







    public void CheckPermissions(){
        if (Build.VERSION.SDK_INT>23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                return;
            }
            }else {
            loadSongs();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case 123:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    loadSongs();
                }else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    private void loadSongs(){

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                do {String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    Long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    String duration = String.valueOf(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    Uri albumUri = ContentUris.withAppendedId(sArtworkUri, albumId);



                    SongInfo s = new SongInfo(name, artist, url , albumUri, duration);
                    songs.add(s);

                }while (cursor.moveToNext());


            }
            cursor.close();
            songAdapter = new SongAdapter(this, songs);

        }


    }
}
