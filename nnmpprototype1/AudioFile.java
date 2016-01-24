/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import java.util.List;

/**
 *
 * @author zmmetiva
 */
public class AudioFile extends MediaFile {
    
    private String artist;
    private String album;
    private String title;
    private String track;
    private String time;
    
    // TODO: Image for artwork
    
    private int songId;
    
    public AudioFile() {
        super();
    }
    
    public AudioFile(String location, int duration, String artist, String album, String title, String track, int songId) {
        super(location, duration);
        
        this.artist = artist;
        this.album = album;
        this.title = title;
        this.track = track;
        this.songId = songId;
    }
    
    public void setArtist(String artist) {
        this.artist = artist;
    }
    
    public void setAlbum(String album) {
        this.album = album;        
    }
    
    public void setTitle(String title) {
        this.title = title;        
    }
    
    public void setTrack(String track) {
        this.track = track;        
    }
    
    public void setSongId(int songId) {
        this.songId = songId;
        
    }
    
    public String getArtist() {
        return artist;
    }
    
    public String getAlbum() {
        return album;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getTrack() {
        return track;
    }
      
    public int getSongId() {
        return songId;
    }
    
    public String getTime() {
        int hour = Math.floorDiv(duration, 3600);
        int min = (duration / 60) % 60;
        int sec = duration % 60;
        String newTime = "";
        
        if (hour > 0) {
            newTime += hour + ":";
        }
        newTime += min + ":";
        
        if (sec < 10) {
            newTime += "0";
        }
        newTime += sec;
        return newTime;
    }
    
    @Override
    public String toString() {
        return title + " - " + artist;
    }
}
