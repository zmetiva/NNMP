/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;
import nnmpprototype1.FileSystemUtils;
import nnmpprototype1.NNMPDB;


/**
 *
 * @author zmmetiva
 */
public class ImportLibraryController {

    FileSystemUtils fileUtils = new FileSystemUtils();

    public void importMusic(File path, NNMPDB db) {
        fileUtils.populateMusicMetadata(path.getPath(),db);
    }

    public boolean isProcessingComplete() {
        return fileUtils.isComplete();
    }

    public List<Integer> getArtistList() {
        return fileUtils.getArtistList();
    }

    public List<Integer> getUnknownList() {
        return fileUtils.getUnknownList();
    }

}
