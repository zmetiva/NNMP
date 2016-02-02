/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

/**
 *
 * @author zmmetiva
 */
public class TableContextMenu extends ContextMenu {
    
    private final MenuItem addToPlaylist = new MenuItem("Add to Playback Queue");
    private final MenuItem addAllToPlaylist = new MenuItem("Add All to Playback Queue");
    private final MenuItem editMetadata = new MenuItem("Edit Metadata");
    private final MenuItem convertAudio = new MenuItem("Convert Audio");
    private nnmpprototype1.AudioFile file;
    private PlaybackList list;
    ObservableList<nnmpprototype1.AudioFile> audioTableList;
    
    TableContextMenu() {
        super();
        this.getItems().add(addToPlaylist);
        this.getItems().add(addAllToPlaylist);
        this.getItems().add(new SeparatorMenuItem());
        this.getItems().add(editMetadata);
        this.getItems().add(convertAudio);
        
        addToPlaylist.setOnAction((ActionEvent e) -> {
            list.enqueueFile(file);
        });

        addAllToPlaylist.setOnAction((ActionEvent e) -> {
            if (audioTableList != null) {
                for (int i = 0; i < audioTableList.size(); ++i) {
                    list.enqueueFile(audioTableList.get(i));
                }
            }
        });
    }
    
    public void setAudioFile(nnmpprototype1.AudioFile file) {
        this.file = file;
    }
    
    public void setPlaybackList(PlaybackList list) {
        this.list = list;
    }

    public void setAudioTableList(ObservableList<nnmpprototype1.AudioFile> audioTableList) {
        this.audioTableList = audioTableList;
    }
}
