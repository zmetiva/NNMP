/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 *
 * @author tyler
 */
public class MediaPlaybackController {
    
    private MediaPlayback player = new MediaPlayback();
    
    public MediaPlaybackController() {
        
    }
    
    public void setPlaybackIndex(int index) {
        player.setCurrentIndex(index);
    }
    
    public void setTrackChanged(boolean changed) {
        player.setChangeData(changed);
    }
    
    public void playAudioFile() {
        player.playAudio();
    }
    
    public void stopAudioPlayback() {
        player.stopAudio();
    }

    public void pauseAudioPlayback() {
        player.pauseAudio();
    }

    public void resumeAudioPlayback() {
        player.resumeAudio();
    }

    public void queueNextAudioFile() {
        player.playNextTrack();
    }
    
    public void queuePreviousAudioFile() {
        player.playPreviousTrack();
    }
    
    public boolean isPlaybackActive() {
        return player.active();
    }

    public boolean isPlaybackStopped() {
        return player.getIsStopped();
    }

    public void setPlaybackVolume(float level) {
        player.setVolume(level);
    }
    
    public void setPlaybackQueueController(PlaybackQueueController pqc) {
        player.setPlaybackQueueController(pqc);
    }
    
    public boolean getSongChange() {
        return player.getChangeData();
    }
    
    public int getDataIndex() {
        return player.getCurrentIndex();
    }

    public boolean getPausedStatus() {
        return player.getIsPaused();
    }

    public void seekAudio(int seekVal) {
        player.seek(seekVal);
    }

    public void addChangeObserver(Observer o) {
        player.addObserver(o);
    }

    public float getVolumeLevel() {
        return player.getVolLevel();
    }
}
