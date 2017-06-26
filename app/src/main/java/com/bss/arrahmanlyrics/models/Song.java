package com.bss.arrahmanlyrics.models;

/**
 * Created by mohan on 5/20/17.
 */

public class Song {
    String songName;
    String trackNo;
    String lyricistNames;

    public Song(String songName, String trackNo, String lyricistNames) {
        this.songName = songName;
        this.trackNo = trackNo;
        this.lyricistNames = lyricistNames;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(String trackNo) {
        this.trackNo = trackNo;
    }

    public String getLyricistNames() {
        return lyricistNames;
    }

    public void setLyricistNames(String lyricistNames) {
        this.lyricistNames = lyricistNames;
    }

}
