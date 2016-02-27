package nnmpprototype1;

import javafx.collections.ObservableList;

/**
 * Created by Tyler on 2/2/2016.
 */

public class NNMPMediaPlayer {

    private final PlaybackQueueController playbackQueueController = new PlaybackQueueController();
    private final MediaPlaybackController mediaPlaybackController = new MediaPlaybackController();

    public NNMPMediaPlayer() {
        mediaPlaybackController.setPlaybackQueueController(playbackQueueController);
    }

    public void enqueueToPlaybackQueue(nnmpprototype1.AudioFile file) {
        playbackQueueController.enqueueAudioFile(file);
    }

    public void removeFromPlaybackQueue(int index) {
        playbackQueueController.removeFileFromQueue(index);
    }

    public boolean isPlaybackQueueEmpty() {
        return playbackQueueController.isEmpty();
    }

    public PlaybackList getPlaybackQueue() {
        return playbackQueueController.getPlaybackList();
    }

    public ObservableList<nnmpprototype1.AudioFile> getObservablePlaybackList() {
        return playbackQueueController.getObsPlaybackList();
    }

    public void clearPlaybackQueue() {
        playbackQueueController.flush();
    }

    public void playFileAt(int index) {
        mediaPlaybackController.setPlaybackIndex(index);
    }

    public void startMediaPlayback() {
        mediaPlaybackController.playAudioFile();
    }

    public void stopMediaPlayback() {
        mediaPlaybackController.stopAudioPlayback();
    }

    public void pauseMediaPlayback() {
        mediaPlaybackController.pauseAudioPlayback();
    }

    public void resumeMediaPlayback() {
        mediaPlaybackController.resumeAudioPlayback();
    }

    public void playNextFile() {
        mediaPlaybackController.queueNextAudioFile();
    }

    public void playPreviousFile() {
        mediaPlaybackController.queuePreviousAudioFile();
    }

    public boolean isPlaying() {
        return mediaPlaybackController.isPlaybackActive();
    }

    public void setVolumeLevel(float vol) {
        mediaPlaybackController.setPlaybackVolume(vol);
    }

    public void setPlaybackIndex(int playbackIndex) {
        playbackQueueController.setPlaybackIndex(playbackIndex);
    }

    public int getAudioSourceIndex() {
        return playbackQueueController.getPlaybackIndex();
    }

    public boolean hasSongChanged() {
        return mediaPlaybackController.getSongChange();
    }

    public int getActiveIndex() {
        return mediaPlaybackController.getDataIndex();
    }

    public boolean isPaused() {
        return mediaPlaybackController.getPausedStatus();
    }

    public boolean isStoppedPressed() {
        return mediaPlaybackController.isPlaybackStopped();
    }

    public void seek(int val) {
        mediaPlaybackController.seekAudio(val);
    }

    public nnmpprototype1.AudioFile getActiveFile() {
        return playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex());
    }

    public void addChangeObserver(Observer o) {
        mediaPlaybackController.addChangeObserver(o);
    }

    public float getVolumeLevel() {
        return mediaPlaybackController.getVolumeLevel();
    }
}
