/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author tyler
 */
public class PlaybackListContextMenu extends ContextMenu {
    
    private final MenuItem moveUp = new MenuItem("Move Up");
    private final MenuItem moveDown = new MenuItem("Move Down");
    private final MenuItem removeFile = new MenuItem("Remove");
    private PlaybackList list;
    private int selectedIndex;
    
    PlaybackListContextMenu() {
        super();
        this.getItems().add(moveUp);
        this.getItems().add(moveDown);
        this.getItems().add(removeFile);
        
        removeFile.setOnAction((ActionEvent e) -> {
            list.removeFileAt(selectedIndex);
        });
    }
    
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
    
    public void setPlaybackList(PlaybackList list) {
        this.list = list;
    }
}
