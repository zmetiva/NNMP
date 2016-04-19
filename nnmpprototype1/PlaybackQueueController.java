/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import javafx.collections.ObservableList;

/**
 * The PlaybackQueueController class acts as a controller that handles all operations
 * associated with the PlaybackList.
 */
public class PlaybackQueueController {
    /** Instantiate new PlaybackList object */
    private PlaybackList playbackList = new PlaybackList();

    /**
     * Default constructor
     */
    public PlaybackQueueController() {
        
    }

    /**
     * Method that sets the value of the desired playback index.
     *
     * @param playbackIndex - desired index for file in playback list
     */
    public void setPlaybackIndex(int playbackIndex) {
        playbackList.setPlaybackIndex(playbackIndex);
    }

    /**
     * Accessor method that returns the current playback index.
     *
     * @return int - active playback index
     */
    public int getPlaybackIndex() {
        return playbackList.getPlaybackIndex();
    }

    /**
     * Method that enqueues an AudioFile to the playback list.
     *
     * @param file - AudioFile to be added
     */
    public void enqueueAudioFile(nnmpprototype1.AudioFile file) {
        playbackList.enqueueFile(file);
    }

    /**
     * Method that dequeues an AudioFile from the playback list.
     */
    public void dequeueAudioFile() {
        playbackList.dequeueFile();
    }

    /**
     * Method that allows the removal of a particular AudioFile in the playback list.
     *
     * @param index - index of file to be removed
     */
    public void removeFileFromQueue(int index) {
        playbackList.removeFileAt(index);
    }

    /**
     * Method used to determine if the playback list is empty.
     *
     * @return boolean - true if empty, false otherwise
     */
    public boolean isEmpty() {
        return playbackList.empty();
    }

    /**
     * Method that returns the playback list in the form of an FXML ObservableList.
     *
     * @return ObservableList - copy of playback list
     */
    public ObservableList<nnmpprototype1.AudioFile> getObsPlaybackList() {
        return playbackList.getList();
    }

    /**
     * Method that returns a reference to the playback list.
     *
     * @return PlaybackList - the playback list
     */
    public PlaybackList getPlaybackList() {
        return playbackList;
    }

    /**
     * Method that removes all elements in the playback list.
     */
    public void flush() {
        playbackList.flush();
    }
}
