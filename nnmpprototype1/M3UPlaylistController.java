package nnmpprototype1;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

/**
 * Created by Zach on 1/31/2016.
 */
public class M3UPlaylistController {

    M3UPlaylistController() {

    }

    public void savePlaylist(PlaybackList list) {
        FileChooser saveDialog = new FileChooser();
        saveDialog.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("m3u Playlist", "*.m3u"));
        File playlistFile = saveDialog.showSaveDialog(new Stage().getOwner());
        M3UPlaylistGenerator generator = new M3UPlaylistGenerator(list.getList());

        generator.generate(playlistFile.getAbsolutePath() + ".m3u");
    }

    public void openPlaylist(PlaybackList list) {
        FileChooser openDialog = new FileChooser();
        openDialog.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("m3u Playlist", "*.m3u"));
        File playlistFile = openDialog.showOpenDialog(new Stage().getOwner());
        M3UPlaylistGenerator generator = new M3UPlaylistGenerator();

        generator.read(playlistFile.getAbsolutePath(), false);

        list.flush();

        for (int i = 0; i < generator.size(); i++) {
            list.enqueueFile(generator.get(i));
        }
    }
}
