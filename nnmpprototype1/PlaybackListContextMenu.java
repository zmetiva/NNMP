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
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
    private final MenuItem loadPlaylist = new MenuItem("Load Playlist");
    private final MenuItem savePlaylist = new MenuItem("Save Playlist");


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
        this.getItems().add(new SeparatorMenuItem());
        this.getItems().add(loadPlaylist);
        this.getItems().add(savePlaylist);
        
        removeFile.setOnAction((ActionEvent e) -> {
            list.removeFileAt(selectedIndex);
        });

        clearQueue.setOnAction((ActionEvent e) -> {
            list.flush();
        });

        editMetadata.setOnAction((ActionEvent e) -> {
            FXMLMetadataController metadataDia = new FXMLMetadataController(list.getFileAt(selectedIndex));
            metadataDia.showAndWaitDialog();
        });

        convertAudio.setOnAction((ActionEvent e) -> {
            FXMLConvertController convertController = new FXMLConvertController();
            convertController.showDialog();
        });

        loadPlaylist.setOnAction((ActionEvent e) -> {
            FileChooser openDialog = new FileChooser();
            openDialog.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("m3u Playlist", "*.m3u"));
            File playlistFile = openDialog.showOpenDialog(this.getOwnerWindow());
            M3UPlaylistGenerator generator = new M3UPlaylistGenerator();

            generator.read(playlistFile.getAbsolutePath(), false);

            list.flush();

            for (int i = 0; i < generator.size(); i++) {
                list.enqueueFile(generator.get(i));
            }
        });
        savePlaylist.setOnAction((ActionEvent e) -> {
            FileChooser saveDialog = new FileChooser();
            saveDialog.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("m3u Playlist", "*.m3u"));
            File playlistFile = saveDialog.showSaveDialog(this.getOwnerWindow());
            M3UPlaylistGenerator generator = new M3UPlaylistGenerator(list.getList());

            generator.generate(playlistFile.getAbsolutePath());

        });
    }
    
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
    
    public void setPlaybackList(PlaybackList list) {
        this.list = list;
    }
}
