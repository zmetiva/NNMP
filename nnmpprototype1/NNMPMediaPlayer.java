package nnmpprototype1;

import javafx.collections.ObservableList;

/**
 * The NNMPMediaPlayer Class serves as the primary media player object in NNMP. All functionality related to
 * audio playback is implemented within the controller objects NNMPMediaPlayer is composed of.
 * NNMPMediaPlayer is a Singleton object.
 */

public class NNMPMediaPlayer {
    /** Instantiate Singleton NNMPMediaPlayer object */
    private static NNMPMediaPlayer mediaPlayer = new NNMPMediaPlayer();
    /** Instantiate PlaybackQueueController */
    private final PlaybackQueueController playbackQueueController = new PlaybackQueueController();
    /** Instantiate MediaPlaybackController */
    private final MediaPlaybackController mediaPlaybackController = new MediaPlaybackController();

    /**
     * Non-Arg Constructor
     */
    private NNMPMediaPlayer() {
        mediaPlaybackController.setPlaybackQueueController(playbackQueueController);
    }

    /**
     * Method used to return the instantiated NNMPMediaPlayer object.
     *
     * @return NNMPMediaPlayer - Singleton object
     */
    public static NNMPMediaPlayer getInstance() {
        return mediaPlayer;
    }

    /**
     * Method used to add an AudioFile to the PlaybackList via the PlaybackQueueController.
     *
     * @param file - AudioFile to be added
     */
    public void enqueueToPlaybackQueue(nnmpprototype1.AudioFile file) {
        playbackQueueController.enqueueAudioFile(file);
    }

    /**
     * Method used to determine if the PlaybackList is empty.
     *
     * @return boolean - empty status of PlaybackList
     */
    public boolean isPlaybackQueueEmpty() {
        return playbackQueueController.isEmpty();
    }

    /**
     * Method used to retrieve a copy of the PlaybackList.
     *
     * @return PlaybackList - copy
     */
    public PlaybackList getPlaybackQueue() {
        return playbackQueueController.getPlaybackList();
    }

    /**
     * Method used to retrieve a copy of the PlaybackList in the form of a
     * FXML Observable ArrayList.
     *
     * @return ObservableList - copy of PlaybackList
     */
    public ObservableList<nnmpprototype1.AudioFile> getObservablePlaybackList() {
        return playbackQueueController.getObsPlaybackList();
    }

    /**
     * Method used to clear the contents of the PlaybackList.
     */
    public void clearPlaybackQueue() {
        playbackQueueController.flush();
    }

    /**
     * Method used to set the current playback index in the PlaybackList.
     *
     * @param index - desired playback index
     */
    public void playFileAt(int index) {
        mediaPlaybackController.setPlaybackIndex(index);
    }

    /**
     * Method used to begin the playback of AudioFiles stored in the PlaybackList.
     */
    public void startMediaPlayback() {
        mediaPlaybackController.playAudioFile();
    }

    /**
     * Method used to stop active audio playback.
     */
    public void stopMediaPlayback() {
        mediaPlaybackController.stopAudioPlayback();
    }

    /**
     * Method used to pause active audio playback.
     */
    public void pauseMediaPlayback() {
        mediaPlaybackController.pauseAudioPlayback();
    }

    /**
     * Method used to resume audio playback.
     */
    public void resumeMediaPlayback() {
        mediaPlaybackController.resumeAudioPlayback();
    }

    /**
     * Method used to play the next file in the PlaybackList.
     */
    public void playNextFile() {
        mediaPlaybackController.queueNextAudioFile();
    }

    /**
     * Method used to play the previous file in the PlaybackList.
     */
    public void playPreviousFile() {
        mediaPlaybackController.queuePreviousAudioFile();
    }

    /**
     * Method used to determine if audio playback is active.
     *
     * @return boolean - audio playback status
     */
    public boolean isPlaying() {
        return mediaPlaybackController.isPlaybackActive();
    }

    /**
     * Method used to set the playback volume level.
     *
     * @param vol - desired volume level
     */
    public void setVolumeLevel(float vol) {
        mediaPlaybackController.setPlaybackVolume(vol);
    }

    /**
     * Method used to set the desired playback index.
     *
     * @param playbackIndex - desired playback index.
     */
    public void setPlaybackIndex(int playbackIndex) {
        playbackQueueController.setPlaybackIndex(playbackIndex);
    }

    /**
     * Accessor method used to determine if a song has changed during playback.
     *
     * @return boolean - song changed status
     */
    public boolean hasSongChanged() {
        return mediaPlaybackController.getSongChange();
    }

    /**
     * Accessor method used to retrieve the current playback index.
     *
     * @return int - current playback index
     */
    public int getActiveIndex() {
        return mediaPlaybackController.getDataIndex();
    }

    /**
     * Accessor method used to determine if playback has been paused.
     *
     * @return boolean - playback paused status
     */
    public boolean isPaused() {
        return mediaPlaybackController.getPausedStatus();
    }

    /**
     * Accessor method used to determine if active playback has been stopped.
     *
     * @return boolean - playback stopped status
     */
    public boolean isStoppedPressed() {
        return mediaPlaybackController.isPlaybackStopped();
    }

    /**
     * Method used to seek to a particular frame of an AudioFile.
     *
     * @param val - desired position in seconds
     */
    public void seek(int val) {
        mediaPlaybackController.seekAudio(val);
    }

    /**
     * Accessor method used to retrieve the AudioFile object that is currently in use.
     *
     * @return
     */
    public nnmpprototype1.AudioFile getActiveFile() {
        return playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex());
    }

    /**
     * Method used to add an Observer to the list of Observer objects.
     *
     * @param o - Observer to be added
     */
    public void addChangeObserver(Observer o) {
        mediaPlaybackController.addChangeObserver(o);
    }

    /**
     * Accessor method used to retrieve the current volume level.
     *
     * @return float - current volume level
     */
    public float getVolumeLevel() {
        return mediaPlaybackController.getVolumeLevel();
    }

    public boolean isPrePlaySeek() {
        return mediaPlaybackController.isPrePlaySeek();
    }
}
