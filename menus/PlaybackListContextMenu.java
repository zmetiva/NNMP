
package menus;

import controllers.M3UPlaylistController;
import dialogs.FXMLConvertController;
import dialogs.FXMLMetadataController;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import nnmpprototype1.PlaybackList;
import nnmpprototype1.PlaybackListCaretaker;

import java.util.Collections;

/**
 * This class is used to handle the functionality  of the context menu associated with the
 * PlaybackList. All operations needed to satisfy the PlaybackList Use Case are included.
 */
public class PlaybackListContextMenu extends ContextMenu {

    /** Caretaker used to store list Memento object */
    private PlaybackListCaretaker caretaker = new PlaybackListCaretaker();
    /** MenuItem for Move Up */
    private final MenuItem moveUp = new MenuItem("Move Up");
    /** MenuItem for MoveDown */
    private final MenuItem moveDown = new MenuItem("Move Down");
    /** MenuItem for Remove */
    private final MenuItem removeFile = new MenuItem("Remove");
    /** MenuItem for Edit Metadata */
    private final MenuItem editMetadata = new MenuItem("Edit Metadata");
    /** MenuItem for Convert Audio */
    private final MenuItem convertAudio = new MenuItem("Convert Audio");
    /** MenuItem for Remove All */
    private final MenuItem clearQueue = new MenuItem("Remove All");
    /** MenuItem for Load Playlist */
    private final MenuItem loadPlaylist = new MenuItem("Load Playlist");
    /** MenuItem for Save Playlist */
    private final MenuItem savePlaylist = new MenuItem("Save Playlist");
    /** MenuItem for Shuffle */
    private final MenuItem shuffleList = new MenuItem("Shuffle");
    /** MenuItem for Restore Queue */
    private final MenuItem restoreList = new MenuItem("Restore Queue");
    /** Reference to PlaybackList */
    private PlaybackList list;
    /** int to store selected index of PlaybackList element */
    private int selectedIndex;
    /** boolean indicating shuffle staus */
    private boolean isShuffled = false;

    /**
     * Non-Arg Constructor
     */
    public PlaybackListContextMenu() {
        // Call superclass constructor
        super();

        // Add all MenuItems to ContextMenu
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
        this.getItems().add(restoreList);

        // Handles removeFile MenuItem ActionEvent
        removeFile.setOnAction((ActionEvent e) -> {
            // Remove AudioFile at selected index
            list.removeFileAt(selectedIndex);
        });

        // Handles clearQueue MenuItem ActionEvent
        clearQueue.setOnAction((ActionEvent e) -> {
            // Clear PlaybackList
            list.flush();
        });

        // Handles editMetadata MenuItem ActionEvent
        editMetadata.setOnAction((ActionEvent e) -> {
            // Instantiate FXMLMetadataController
            FXMLMetadataController metadataDia = new FXMLMetadataController(list.getFileAt(selectedIndex));
            // Show metadata GUI
            metadataDia.showAndWaitDialog();
            // Update altered file metadata
            list.getList().set(selectedIndex,metadataDia.getUpdatedAudioFile());
        });

        // Handles convertAudio MenuItem ActionEvent
        convertAudio.setOnAction((ActionEvent e) -> {
            // Instantiate FXMLConvertController
            FXMLConvertController convertController = new FXMLConvertController(list.getFileAt(selectedIndex));
            // Show conversion controller
            convertController.showDialog();
        });

        // Handles loadPlaylist MenuItem ActionEvent
        loadPlaylist.setOnAction((ActionEvent e) -> {
            // Instantiate M3UPlaylistController
            M3UPlaylistController playlistController = new M3UPlaylistController();
            // Open playlist
            playlistController.openPlaylist(list);
            playlistController = null;
        });

        // Handles savePlaylist MenuItem ActionEvent
        savePlaylist.setOnAction((ActionEvent e) -> {
            // Instantiate M3uPlaylist Controller
            M3UPlaylistController playlistController = new M3UPlaylistController();
            // Save playlist
            playlistController.savePlaylist(list);
            playlistController = null;
        });

        // Handles moveUp MenuItem ActionEvent
        moveUp.setOnAction((ActionEvent e) -> {
            if (!list.empty() && selectedIndex != 0) {
                // Swap AudioFile / move file up
                list.swap(selectedIndex, selectedIndex - 1);
            }
        });

        // Handles moveDown MenuItem ActionEvent
        moveDown.setOnAction((ActionEvent e) -> {
            if (!list.empty() && selectedIndex != list.getSize() - 1) {
                // Swap AudioFile / move file down
                list.swap(selectedIndex, selectedIndex + 1);
            }
        });

        // Handles shuffleList MenuItem ActionEvent
        shuffleList.setOnAction((ActionEvent e) -> {
            if (!list.empty()) {
                // If list not shuffled
                if (!isShuffled) {
                    // Save list contents to caretaker
                    caretaker.save(list);
                }
                // Shuffle list
                Collections.shuffle(list.getList());
                // Toggle shuffled status
                isShuffled = true;
            }
        });

        // Handles restoreList MenuItem ActionEvent
        restoreList.setOnAction((ActionEvent e) -> {
            // Restore list with previous contents
            caretaker.undo(list);
            // Toggle shuffled status
            isShuffled = false;
        });
    }

    /**
     * Method that sets the selected index to the index it is passed.
     *
     * @param selectedIndex - desired selection index
     */
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    /**
     * Method used to set a reference to the active PlaybackList.
     *
     * @param list - active PlaybackList object
     */
    public void setPlaybackList(PlaybackList list) {
        this.list = list;
    }
}