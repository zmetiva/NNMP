package nnmpprototype1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The PlaybackList Class is used as a modified playback queue. AudioFiles are added to the list via the application's
 * main GUI form. Various modification operations that make playback possible are included.
 */
public class PlaybackList {

    /** Holds index of currently active AudioFile */
    int playbackIndex = 0;
    /** Collection of AudioFiles to be played */
    private ObservableList<nnmpprototype1.AudioFile> playQueue = FXCollections.observableArrayList();

    /**
     * Default Constructor
     */
    public PlaybackList() {
        
    }

    /**
     * Sets current playback index to the value passed.
     *
     * @param playbackIndex - desired playback index
     */
    public void setPlaybackIndex(int playbackIndex) {
        this.playbackIndex = playbackIndex;
    }

    /**
     * Returns index of current playback index.
     *
     * @return int - current playback index
     */
    public int getPlaybackIndex() {
        return playbackIndex;
    }

    /**
     * Method used to add a new AudioFile to the playback list.
     *
     * @param file - new AudioFile
     */
    public void enqueueFile(nnmpprototype1.AudioFile file) {
        playQueue.add(file);
    }

    /**
     * Removes the active AudioFile from the playback list.
     */
    public void dequeueFile() {
        if (!playQueue.isEmpty()) {
            // Remove first AudioFile
            playQueue.remove(0);
        }
    }

    /**
     * Allows for the removal of an AudioFile at the index passed.
     *
     * @param index - location of AudioFile to be removed
     */
    public void removeFileAt(int index) {
        playQueue.remove(index);
    }

    /**
     * Returns the AudioFile object located at the passed index.
     *
     * @param index - location of desired AudioFile
     * @return AudioFile - desired AudioFile object
     */
    public nnmpprototype1.AudioFile getFileAt(int index) {
        return playQueue.get(index);
    }

    /**
     * Method that reports if the playback list is populated.
     *
     * @return boolean - population status
     */
    public boolean empty() {
        return playQueue.isEmpty();
    }

    /**
     * Method that returns the current PlaybackList.
     *
     * @return ObservableList<AudioFile> - PlaybackList
     */
    public ObservableList<nnmpprototype1.AudioFile> getList() {
        return playQueue;
    }

    /**
     * Method that returns the current number of AudioFile objects
     * stored in the PlaybackList.
     *
     * @return int - size of PlaybackList
     */
    public int getSize() {
        return playQueue.size();
    }

    /**
     * Simple utility method that swaps the position of two elements in
     * the PlaybackList.
     *
     * @param x - element to be swapped
     * @param y - element to be swapped
     */
    public void swap(int x, int y) {
        nnmpprototype1.AudioFile temp = playQueue.get(x);
        playQueue.set(x, playQueue.get(y));
        playQueue.set(y, temp);
    }

    /**
     * Wrapper method used to empty the PlaybackList.
     */
    public void flush() {
        playQueue.clear();
    }

    /**
     * Method that returns a Memento housing the current state of the PlaybackList.
     *
     * @return Memento - current state of PlaybackList
     */
    public Memento save() {
        return new Memento(playQueue);
    }

    /**
     * Method used to restore the the list to a previously saved state.
     *
     * @param obj - previously saved state
     */
    public void undoLastSave(Object obj) {
        // Retrieve Memento
        Memento memento = (Memento) obj;

        // Store Memento state
        ObservableList<nnmpprototype1.AudioFile> c = memento.getState();

        // Clear current list and populate with previous state
        playQueue.clear();
        for (AudioFile a : c) {
            playQueue.add(a);
        }
    }
}
