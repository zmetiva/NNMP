package nnmpprototype1;

/**
 * The MediaPlayback Controller Class is used to control all operations required for the
 * Media Playback Use Case. An instance of the MediaPlayback Class is utilize to achieve all relevant
 * operations.
 */
public class MediaPlaybackController {
    /** MediaPlayback object */
    private MediaPlayback player = new MediaPlayback();

    /**
     * Default Constructor
     */
    public MediaPlaybackController() {
        
    }

    /**
     * Method used to set the current playback index.
     *
     * @param index - desired playback index
     */
    public void setPlaybackIndex(int index) {
        player.setCurrentIndex(index);
    }

    /**
     * Method used to begin playing AudioFiles stored in the PlaybackList.
     */
    public void playAudioFile() {
        player.playAudio();
    }

    /**
     * Method used to stop active audio playback.
     */
    public void stopAudioPlayback() {
        player.stopAudio();
    }

    /**
     * Method used to paused active audio playback.
     */
    public void pauseAudioPlayback() {
        player.pauseAudio();
    }

    /**
     * Method used to resume previously paused audio playback.
     */
    public void resumeAudioPlayback() {
        player.resumeAudio();
    }

    /**
     * Method used to play the next AudioFile stored in the PlaybackList.
     */
    public void queueNextAudioFile() {
        player.playNextTrack();
    }

    /**
     * Method used to play the previous AudioFile stored in the PlaybackList.
     */
    public void queuePreviousAudioFile() {
        player.playPreviousTrack();
    }

    /**
     * Accessor method used to retrieve active playback status.
     *
     * @return boolean - playback status
     */
    public boolean isPlaybackActive() {
        return player.active();
    }

    /**
     * Accessor method used to determine if audio playback has been stopped.
     *
     * @return boolean - playback stopped status
     */
    public boolean isPlaybackStopped() {
        return player.getIsStopped();
    }

    /**
     * Method used to set the volume of audio playback.
     *
     * @param level - desired volume level
     */
    public void setPlaybackVolume(float level) {
        player.setVolume(level);
    }

    /**
     * Method used to set a reference to the PlaybackQueueController.
     *
     * @param pqc - PlaybackQueueController
     */
    public void setPlaybackQueueController(PlaybackQueueController pqc) {
        player.setPlaybackQueueController(pqc);
    }

    /**
     * Accessor method used to determine if a song has been changed.
     *
     * @return boolean - song changed status
     */
    public boolean getSongChange() {
        return player.getChangeData();
    }

    /**
     * Accessor method used to retrieve the current playback index.
     *
     * @return int - current playback index
     */
    public int getDataIndex() {
        return player.getCurrentIndex();
    }

    /**
     * Accessor method used to determine if playback is paused.
     *
     * @return boolean - playback paused status
     */
    public boolean getPausedStatus() {
        return player.getIsPaused();
    }

    /**
     * Method used to seek to a particular frame in the currently active AudioFile.
     *
     * @param seekVal - position to seek to in seconds
     */
    public void seekAudio(int seekVal) {
        player.seek(seekVal);
    }

    /**
     * Method used to add an Observer to the list of observer objects.
     *
     * @param o - Observer to be added
     */
    public void addChangeObserver(Observer o) {
        player.addObserver(o);
    }

    /**
     * Accessor method used to retrieve the current volume level.
     *
     * @return float - current volume level
     */
    public float getVolumeLevel() {
        return player.getVolLevel();
    }
}
