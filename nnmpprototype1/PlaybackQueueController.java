/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import javafx.collections.ObservableList;

/**
 *
 * @author tyler
 */
public class PlaybackQueueController {
 
    private PlaybackList playbackList = new PlaybackList();
    
    public PlaybackQueueController() {
        
    }
    
    public void enqueueAudioFile(nnmpprototype1.AudioFile file) {
        playbackList.enqueueFile(file);
    }
    
    public void dequeueAudioFile() {
        playbackList.dequeueFile();
    }
    
    public void removeFileFromQueue(int index) {
        playbackList.removeFileAt(index);
    }
    
    public boolean isEmpty() {
        return playbackList.empty();
    }
    
    public ObservableList<nnmpprototype1.AudioFile> getObsPlaybackList() {
        return playbackList.getList();
    }
    
    public PlaybackList getPlaybackList() {
        return playbackList;
    }
    
    public void flush() {
        playbackList.flush();
    }
}
