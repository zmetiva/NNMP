/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 *
 * @author tyler
 */
public class PlaybackListContextMenu extends ContextMenu {
    
    private final MenuItem moveUp = new MenuItem("Move Up");
    private final MenuItem moveDown = new MenuItem("Move Down");
    private final MenuItem removeFile = new MenuItem("Remove");
    private final MenuItem editMetadata = new MenuItem("Edit Metadata");
    private final MenuItem convertAudio = new MenuItem("Convert Audio");
    private final MenuItem clearQueue = new MenuItem("Remove All");

   //FXMLMetadataController metadataDia = new FXMLMetadataController();

    private PlaybackList list;
    private int selectedIndex;
    
    PlaybackListContextMenu() {
        super();
        this.getItems().add(moveUp);
        this.getItems().add(moveDown);
        this.getItems().add(new SeparatorMenuItem());
        this.getItems().add(editMetadata);
        this.getItems().add(convertAudio);
        this.getItems().add(new SeparatorMenuItem());
        this.getItems().add(removeFile);
        this.getItems().add(clearQueue);
        
        removeFile.setOnAction((ActionEvent e) -> {
            list.removeFileAt(selectedIndex);
        });

        editMetadata.setOnAction((ActionEvent e) -> {
            FXMLMetadataController metadataDia = new FXMLMetadataController(list.getFileAt(selectedIndex));
            metadataDia.showAndWaitDialog();
        });
    }
    
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
    
    public void setPlaybackList(PlaybackList list) {
        this.list = list;
    }
}
