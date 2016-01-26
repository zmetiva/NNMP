/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;

import java.io.File;
import java.io.IOException;
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
    private String year;
    
    // TODO: Image for artwork
    
    private int songId;
    
    public AudioFile() {
        super();
    }
    
    public AudioFile(String location, int duration, String artist, String album, String title, String track, String year, int songId) {
        super(location, duration);
        
        this.artist = artist;
        this.album = album;
        this.title = title;
        this.track = track;
        this.songId = songId;
        this.year = year;
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

    public void setYear(String year) {this.year = year; }

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

    public String getYear() { return year; }
    
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

    public void saveMetadata() {
        File audio = new File(location);
        org.jaudiotagger.audio.AudioFile f = null;
        try {
            f = AudioFileIO.read(audio);
            Tag tag = f.getTag();

            tag.setField(FieldKey.TITLE, title);
            tag.setField(FieldKey.ARTIST, artist);
            tag.setField(FieldKey.ALBUM, album);
            tag.setField(FieldKey.TRACK, track);
            tag.setField(FieldKey.YEAR, year);

            try {
                f.commit();
            } catch (CannotWriteException e) {
                e.printStackTrace();
            }

        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}
