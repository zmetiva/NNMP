/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.*;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Audio File - Contains all of the specific information from an audio file. Extends the MediaFile class.
 */
public class AudioFile extends MediaFile {

    // The metadata of the audio file
    private String artist = "";
    private String album = "";
    private String title = "";
    private String track = "";
    private String year = "";

    // The database id for the song
    private int songId = -1;

    /**
     * Default Constructor
     */
    public AudioFile() {
        super();
    }

    /**
     * Parameterized Constructor
     *
     * @param location the location of the audio file.
     * @param duration the duration of the audio file.
     * @param artist the artist of the audio file.
     * @param album the album of the audio file.
     * @param title the title of the audio file.
     * @param track the track of the audio file.
     * @param year the year of the audio file.
     * @param songId the song id of the audio file.
     */
    public AudioFile(String location, int duration, String artist, String album, String title, String track, String year, int songId) {
        super(location, duration);

        this.artist = artist;
        this.album = album;
        this.title = title;
        this.track = track;
        this.songId = songId;
        this.year = year;
    }

    /**
     * Sets the artist of the audio file.
     *
     * @param artist
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Sets the album of the audio file.
     *
     * @param album
     */
    public void setAlbum(String album) {
        this.album = album;
    }

    /**
     * Sets the title of the audio file.
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the track of the audio file.
     *
     * @param track
     */
    public void setTrack(String track) {
        this.track = track;
    }

    /**
     * Sets the year of the audio file.
     *
     * @param year
     */
    public void setYear(String year) {this.year = year; }

    /**
     * Sets the song id of the audio file.
     *
     * @param songId
     */
    public void setSongId(int songId) {
        this.songId = songId;

    }

    /**
     * Returns the duration of the audio file.
     *
     * @return the duration of the audio file.
     */
    public int getDuration() { return duration; }

    /**
     * Returns the artist of the audio file.
     *
     * @return the artist of the audio file.
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Returns the album of the audio file.
     *
     * @return the album of the audio file.
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Returns the title of the audio file.
     *
     * @return the title of the audio file.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the track of the audio file.
     *
     * @return the track of the audio file.
     */
    public String getTrack() {
        return track;
    }

    /**
     * Returns the song id of the audio file.
     *
     * @return the song id of the audio file.
     */
    public int getSongId() {
        return songId;
    }

    /**
     * Returns the year of the audio file.
     *
     * @return the year of the audio file.
     */
    public String getYear() { return year; }

    /**
     * Returns the time as a string of the audio file.
     *
     * @return the time as a string.
     */
    public String getTime() {

        // Initialize hour minute and second variables
        int hour = Math.floorDiv(duration, 3600);
        int min = (duration / 60) % 60;
        int sec = duration % 60;

        // Create a time string
        String newTime = "";

        // Update the hour of the time string
        if (hour > 0) {
            newTime += hour + ":";
        }

        // Update the minute of the time string
        if (hour > 0 && min < 10) {
            newTime += "0" + min + ":";
        }
        else {
            newTime += min + ":";
        }

        // Update the second of the time string
        if (sec < 10) {
            newTime += "0";
        }
        newTime += sec;

        // Return the time
        return newTime;
    }

    /**
     * Save the audio files metadata from the data stored in the class.
     */
    public void saveMetadata() {
        // Get the file from the location
        File audio = new File(location);

        // Create a tagger file
        org.jaudiotagger.audio.AudioFile f = null;
        try {

            // Read the audio data and get the tag info
            f = AudioFileIO.read(audio);
            Tag tag = f.getTag();

            // Set all of the metadata fields from this class
            tag.setField(FieldKey.TITLE, title);
            tag.setField(FieldKey.ARTIST, artist);
            tag.setField(FieldKey.ALBUM, album);
            tag.setField(FieldKey.TRACK, track);
            tag.setField(FieldKey.YEAR, year);

            // Commit the data to the file
            try {
                f.commit();
            } catch (CannotWriteException e) {
                e.printStackTrace();
            }

        } catch (CannotReadException | ReadOnlyFileException | IOException | InvalidAudioFrameException | TagException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the album art of the audio file.
     *
     * @return the album art of the audio file.
     */
    public Image getAlbumArt() {
        try {

            // Get the file from the location
            File file = new File(this.location);

            // Create a tagger file and read the audio data and get the tag info
            org.jaudiotagger.audio.AudioFile f = AudioFileIO.read(file);
            Tag tag = f.getTag();

            // If there is tag information
            if (tag != null) {

                // Get the artwork from the audio file
                List<Artwork> artList = tag.getArtworkList();

                // If the artwork list is not empty and initialized
                if (artList != null && !artList.isEmpty()) {

                    // Loop through the list of artwork and find the first valid instance
                    for (int i = 0; i < artList.size(); ++i) {

                        // If the artwork is valid
                        if (artList.get(i) != null && artList.get(i).getBinaryData() != null) {
                            try {

                                // Create a buffered image from the images bytes and return it as an Image
                                ByteArrayInputStream bias = new ByteArrayInputStream(artList.get(i).getBinaryData());
                                BufferedImage img = ImageIO.read(bias);
                                if (img != null) {
                                    return SwingFXUtils.toFXImage(img, null);
                                }
                            } catch (IOException | NullPointerException ex) {}
                        }
                    }
                }
            }
        } catch (CannotReadException | IOException | TagException
                | ReadOnlyFileException | InvalidAudioFrameException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Return null if no album art was found
        return null;
    }

    /**
     * Sets the album art metadata of the audio file.
     *
     * @param imgLocation the location of the image.
     */
    public void saveAlbumArt(String imgLocation) {

        // Get the file from the location
        File file = new File(this.location);

        // Create a tagger file
        org.jaudiotagger.audio.AudioFile f = null;

        try {

            // read the audio data and get the tag info
            f = AudioFileIO.read(file);
            Tag tag = f.getTag();

            // Get the image file from the passed location
            File imgFile = new File(imgLocation);

            // If the tag info is valid
            if (tag != null) {

                // Create a new artwork and replace it with the old artwork
                Artwork art = ArtworkFactory.createArtworkFromFile(imgFile);
                tag.deleteArtworkField();
                tag.setField(art);

                // Commit the changes
                f.commit();
            }

        } catch (CannotWriteException | CannotReadException | IOException | ReadOnlyFileException | TagException | InvalidAudioFrameException e) {
            e.printStackTrace();
        }

    }

    /**
     * Overriden toString method.
     *
     * @return the title and artist in one string.
     */
    @Override
    public String toString() {
        return title + " - " + artist;
    }
}