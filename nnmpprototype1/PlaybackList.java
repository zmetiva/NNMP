package nnmpprototype1;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tyler
 */
public class PlaybackList {
    
    private ObservableList<nnmpprototype1.AudioFile> playQueue = FXCollections.observableArrayList();
    
    public PlaybackList() {
        
    }
    
    public void enqueueFile(nnmpprototype1.AudioFile file) {
        playQueue.add(file);
    }
    
    public void dequeueFile() {
        if (!playQueue.isEmpty()) {
            playQueue.remove(0);
        }
    }
    
    public void removeFileAt(int index) {
        playQueue.remove(index);
    }
    
    public nnmpprototype1.AudioFile getFileAt(int index) {
        return playQueue.get(index);
    }
    
    public boolean empty() {
        return playQueue.isEmpty();
    }
    
    public ObservableList<nnmpprototype1.AudioFile> getList() {
        return playQueue;
    }
    
    public int getSize() {
        return playQueue.size();
    }

    public void swap(int x, int y) {
        nnmpprototype1.AudioFile temp = playQueue.get(x);
        playQueue.set(x, playQueue.get(y));
        playQueue.set(y, temp);
    }
    
    public void flush() {
        playQueue.clear();
    }
}
