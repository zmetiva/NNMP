
package menus;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import nnmpprototype1.PlaybackList;

/**
 * Table Context Menu - The entire table view context menu. Creates and holds all functions of the context menu,
 * including the menu items and what happens when they are selected.
 */
public class TableContextMenu extends ContextMenu {

    /** Menu item for add to playback queue **/
    private final MenuItem addToPlaylist = new MenuItem("Add to Playback Queue");

    /** Menu item for add to playback queue **/
    private final MenuItem addAllToPlaylist = new MenuItem("Add All to Playback Queue");

    /** Menu item for edit metadata **/
    private final MenuItem editMetadata = new MenuItem("Edit Metadata");

    /** Menu item for convert audio **/
    private final MenuItem convertAudio = new MenuItem("Convert Audio");

    /** The current selected audio file **/
    private nnmpprototype1.AudioFile file;

    /** The playback list **/
    private PlaybackList list;

    /** The list for the table view **/
    ObservableList<nnmpprototype1.AudioFile> audioTableList;

    /**
     * Default Constructor
     */
    public TableContextMenu() {

        // Call super
        super();

        // Add all menu items to the menu
        this.getItems().add(addToPlaylist);
        this.getItems().add(addAllToPlaylist);
        this.getItems().add(new SeparatorMenuItem());
        this.getItems().add(editMetadata);
        this.getItems().add(convertAudio);

        // Set click event for the add to playlist button
        addToPlaylist.setOnAction((ActionEvent e) -> {
            list.enqueueFile(file);
        });

        // Set click event fot the add all to playlist
        addAllToPlaylist.setOnAction((ActionEvent e) -> {
            if (audioTableList != null) {
                for (int i = 0; i < audioTableList.size(); ++i) {
                    list.enqueueFile(audioTableList.get(i));
                }
            }
        });
    }

    /**
     * Method that sets the audio file from the table.
     *
     * @param file the audio file from the table
     */
    public void setAudioFile(nnmpprototype1.AudioFile file) {
        this.file = file;
    }

    /**
     * Method that sets the playback list.
     *
     * @param list the playback list to be set
     */
    public void setPlaybackList(PlaybackList list) {
        this.list = list;
    }

    /**
     * Method that sets the table view list.
     *
     * @param audioTableList the table view list.
     */
    public void setAudioTableList(ObservableList<nnmpprototype1.AudioFile> audioTableList) {
        this.audioTableList = audioTableList;
    }
}
