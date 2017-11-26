package com.example.pranayanand.musica;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pranay Anand on 27-10-2017.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {

    Utilities utilities = new Utilities();



    static ArrayList<SongInfo> songs;
    Context context;
    OnitemClickListener onitemClickListener;

    public SongAdapter(Context context, ArrayList<SongInfo> songs) {
        this.songs = songs;
        this.context = context;
    }




    public interface OnitemClickListener{
        void onItemClick(LinearLayout b, View v, SongInfo obj, int position);
    }

    public void setOnitemClickListener(OnitemClickListener onitemClickListener){
        this.onitemClickListener = onitemClickListener;
    }


    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_song, parent, false);
        return new SongHolder(view);
    }


    @Override
    public void onBindViewHolder(final SongHolder holder, final int position) {
        final SongInfo songInfo = songs.get(position);
        holder.songName.setText(songInfo.songName.replace(".mp3", ""));

        holder.artistName.setText(songInfo.artistName);
        holder.duration.setText(utilities.milliSecondsToTimer(Long.parseLong(songInfo.duration)));

        Glide.with(context)
                .load(songInfo.albumUri).placeholder(R.mipmap.ic_default).into(holder.albumArt);

        holder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onitemClickListener != null) {
                    onitemClickListener.onItemClick(holder.action, view, songInfo, position);
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class SongHolder extends RecyclerView.ViewHolder {

        TextView songName, artistName, duration;
        LinearLayout action;
        ImageView albumArt;
        public SongHolder(View itemView) {
            super(itemView);

            songName = (TextView) itemView.findViewById(R.id.songName);
            duration = (TextView) itemView.findViewById(R.id.duration);

            artistName = (TextView) itemView.findViewById(R.id.artistName);
            action = (LinearLayout) itemView.findViewById(R.id.playorstop);
            albumArt = (ImageView) itemView.findViewById(R.id.albumArt);
        }
    }
}
