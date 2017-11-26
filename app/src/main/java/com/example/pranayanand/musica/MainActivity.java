package com.example.pranayanand.musica;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.jar.Manifest;


import static com.example.pranayanand.musica.R.id.up;

public class MainActivity extends AppCompatActivity {

    static ArrayList<SongInfo> songs = new ArrayList<SongInfo>();
    RecyclerView recyclerView;
    static SeekBar seekBar;
    SongAdapter songAdapter;
    static Thread thread;
    static MediaPlayer mediaPlayer;
    ImageView albumArt;
    static SharedPreferences sf;
    static SharedPreferences.Editor editor;
    static ImageView backgroundAlbumMain;
    TextView songNameCard;
    CardView card;
    TextView artistNameCard;
    ImageButton playpauseCard;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sf = getSharedPreferences("positionsf", MODE_PRIVATE);

        playpauseCard = (ImageButton) findViewById(R.id.playpauseCard);











        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        albumArt = (ImageView) findViewById(R.id.albumArt);
        songNameCard = (TextView) findViewById(R.id.songNameCard);
        songNameCard.setSelected(true);//for focus for marquee
        songNameCard.setSingleLine();
        artistNameCard = (TextView) findViewById(R.id.artistNameCard);
        backgroundAlbumMain = (ImageView) findViewById(R.id.backgroundAlbumMain);
        int[] wall = {R.drawable.wall1, R.drawable.wall2, R.drawable.wall3};



        Integer i = new Random().nextInt(wall.length);
        Glide.with(getApplicationContext()).load(wall[i]).into(backgroundAlbumMain);






        card = (CardView) findViewById(R.id.card);

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
                songNameCard.setText(obj.songName.replace(".mp3", ""));
                artistNameCard.setText(obj.artistName);
                playpauseCard.setBackgroundResource(R.drawable.pause);


                editor = sf.edit();

                editor.putInt("position", position);
                editor.apply();

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

                        }
                    });




                }

            }
        });


        CheckPermissions();
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NowPlaying.class);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_to_right, R.anim.slide_to_left);



            }
        });
        playpauseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer!=null && mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    playpauseCard.setBackgroundResource(R.drawable.play);
                }


                else {

                    if (mediaPlayer==null){
                        return;
                    }
                    else {
                        mediaPlayer.start();
                        playpauseCard.setBackgroundResource(R.drawable.pause);

                    }



                }

            }
        });







    }







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
