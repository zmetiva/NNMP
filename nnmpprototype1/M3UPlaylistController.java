package nnmpprototype1;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * M3U Playlist Controller - Imports and exports M3U Playlist files from the application.
 */
public class M3UPlaylistController {

    /**
     * Default Constructor
     */
    M3UPlaylistController() {

    }

    /**
     * Saves the playlist to a user specified file system location.
     *
     * @param list the playback list
     */
    public void savePlaylist(PlaybackList list) {

        // Create a file chooser with extension filters of M3U
        FileChooser saveDialog = new FileChooser();
        saveDialog.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("m3u Playlist", "*.m3u"));

        // Create a file from the user selected location
        File playlistFile = saveDialog.showSaveDialog(new Stage().getOwner());

        // Create a playlist generator and save the playlist to the file
        M3UPlaylistGenerator generator = new M3UPlaylistGenerator(list.getList());
        generator.generate(playlistFile.getAbsolutePath() + ".m3u");
    }

    /**
     * Opens the playlist into the applications playback list
     *
     * @param list te playback list
     */
    public void openPlaylist(PlaybackList list) {

        // Create a file chooser with extension filters of M3U
        FileChooser openDialog = new FileChooser();
        openDialog.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("m3u Playlist", "*.m3u"));

        // Create a file from the user selected location
        File playlistFile = openDialog.showOpenDialog(new Stage().getOwner());

        // Create a playlist generator and open the playlist file
        M3UPlaylistGenerator generator = new M3UPlaylistGenerator();
        generator.read(playlistFile.getAbsolutePath(), false);

        // Empty the list
        list.flush();

        // Enqueue all of the files from the playlist file
        for (int i = 0; i < generator.size(); i++) {
            list.enqueueFile(generator.get(i));
        }
    }
}
