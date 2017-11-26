package com.example.pranayanand.musica;

import android.net.Uri;

/**
 * Created by Pranay Anand on 27-10-2017.
 */

public class SongInfo {

    public String songName, artistName, songUrl;
    public Uri albumUri;
    public String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public SongInfo() {
    }

    public SongInfo(String songName, String artistName, String songUrl, Uri albumUri, String duration) {
        this.songName = songName;
        this.artistName = artistName;
        this.songUrl = songUrl;
        this.albumUri = albumUri;
        this.duration = duration;
    }

    public Uri getAlbumUri() {
        return albumUri;
    }

    public void setAlbumUri(Uri albumUri) {
        this.albumUri = albumUri;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }
}
