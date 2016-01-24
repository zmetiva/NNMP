/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 *
 * @author zmmetiva
 */
public class TableContextMenu extends ContextMenu {
    
    private final MenuItem addToPlaylist = new MenuItem("Add to Playlist");
    private final MenuItem editMetadata = new MenuItem("Edit Metadata");
    private final MenuItem convertAudio = new MenuItem("Convert Audio");
    private nnmpprototype1.AudioFile file;
    private PlaybackList list;
    
    TableContextMenu() {
        super();
        this.getItems().add(addToPlaylist);
        this.getItems().add(editMetadata);
        this.getItems().add(new SeparatorMenuItem());
        this.getItems().add(convertAudio);
        
        addToPlaylist.setOnAction((ActionEvent e) -> {
            list.enqueueFile(file);
            System.out.println(file.toString());
        });
    }
    
    public void setAudioFile(nnmpprototype1.AudioFile file) {
        this.file = file;
    }
    
    public void setPlaybackList(PlaybackList list) {
        this.list = list;
    }
}
