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
import javafx.scene.control.TableView;
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
    private final MenuItem shuffleList = new MenuItem("Shuffle");


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
        this.getItems().add(new SeparatorMenuItem());
        this.getItems().add(shuffleList);

        removeFile.setOnAction((ActionEvent e) -> {
            list.removeFileAt(selectedIndex);
        });

        clearQueue.setOnAction((ActionEvent e) -> {
            list.flush();
        });

        editMetadata.setOnAction((ActionEvent e) -> {
            FXMLMetadataController metadataDia = new FXMLMetadataController(list.getFileAt(selectedIndex));
            metadataDia.showAndWaitDialog();
            list.getList().set(selectedIndex,metadataDia.getUpdatedAudioFile());
        });

        convertAudio.setOnAction((ActionEvent e) -> {
            FXMLConvertController convertController = new FXMLConvertController(list.getFileAt(selectedIndex));
            convertController.showDialog();
        });

        loadPlaylist.setOnAction((ActionEvent e) -> {
            M3UPlaylistController playlistController = new M3UPlaylistController();
            playlistController.openPlaylist(list);
            playlistController = null;
        });

        savePlaylist.setOnAction((ActionEvent e) -> {
            M3UPlaylistController playlistController = new M3UPlaylistController();
            playlistController.savePlaylist(list);
            playlistController = null;
        });

        moveUp.setOnAction((ActionEvent e) -> {
            if (!list.empty() && selectedIndex != 0) {
                list.swap(selectedIndex, selectedIndex - 1);
            }
        });

        moveDown.setOnAction((ActionEvent e) -> {
            if (!list.empty() && selectedIndex != list.getSize() - 1) {
                list.swap(selectedIndex, selectedIndex + 1);
            }
        });

        shuffleList.setOnAction((ActionEvent e) -> {
            if (!list.empty()) {
                Collections.shuffle(list.getList());
            }
        });
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public void setPlaybackList(PlaybackList list) {
        this.list = list;
    }
}