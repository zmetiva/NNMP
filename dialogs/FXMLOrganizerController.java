package dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This controller class is used to control the functionality of the organization tool
 * available to the user of NNMP. All major operations required to complete the Organization Use Case
 * are implemented.
 */
public class FXMLOrganizerController implements Initializable {

    /** TextField for directory path */
    @FXML private TextField txtFilePath;
    /** Button for confirming organization operation */
    @FXML private Button btnOK;
    /** Label to indicate processing status */
    @FXML private Label lblStatus;
    /** FXML Stage */
    private Stage stage = new Stage();
    /** File used to store file from chosen directory */
    private File path = null;

    /**
     * Non-Arg Constructor
     */
    public FXMLOrganizerController() {
        // Instantiate FXMLLoader and set controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLOrganizer.fxml"));
        fxmlLoader.setController(this);

        // Set scene
        try {
            stage.setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Handles file path TextField click event
        txtFilePath.setOnMouseClicked((MouseEvent e) -> {
            // Instantiate Directory Chooser
            DirectoryChooser dir = new DirectoryChooser();
            // Show DirectoryChooser
            path = dir.showDialog(null);
            // Set TextField text to chosen path
            txtFilePath.setText(path.toPath().toString());
        });

        // Handles OK Button click event
        btnOK.setOnAction((ActionEvent e) -> {
            // Call organize operation
            organize();
            // Set completion text
            lblStatus.setText("Complete");
        });
    }

    /**
     * Overridden initialize method from implemented Interface. Intentionally without implementation.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Method used to display Organization GUI.
     */
    public void showDialog() {
        stage.show();
    }

    /**
     * Method used to organize the audio files in a chosen directory. The directory is scanned for relevant
     * file types and organized using metadata tag information.
     */
    private void organize() {
        // String builder used to build path string
        StringBuilder sb = new StringBuilder();
        // File location
        String loc = path.toPath().toString();
        // List to collect Files
        List<File> list = new ArrayList<>();

        try {
            // Collect all relevant files stored in chosen directory
            Files.walk(Paths.get(loc)).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && filePath.toFile().getName().endsWith("mp3")) {
                    list.add(filePath.toFile());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert files to AudioFile type
        for (File file : list) {
            AudioFile af = null;
            try {
                af = AudioFileIO.read(file);
            } catch (CannotReadException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TagException e) {
                e.printStackTrace();
            } catch (ReadOnlyFileException e) {
                e.printStackTrace();
            } catch (InvalidAudioFrameException e) {
                e.printStackTrace();
            }
            // Retrieve metadata from AudioFile
            Tag tag = af.getTag();

            // Append artist information to path
            sb.append(loc + "/" + removeIllegal(tag.getFirst(FieldKey.ARTIST)));
            File artistDir = new File(sb.toString());

            // Add artist directory
            if (Files.notExists(artistDir.toPath())) {
                artistDir.mkdir();
            }

            // Append album information to path
            sb.append("/" + removeIllegal(tag.getFirst(FieldKey.ALBUM)));
            File albumDir = new File(sb.toString());

            // Add album directory
            if (Files.notExists(albumDir.toPath())) {
                albumDir.mkdir();
            }
            // Move file into correct directory
            file.renameTo(new File(albumDir.toString() + "/" + file.getName()));

            // Refresh StringBuilder
            sb.setLength(0);
        }
    }

    /**
     * Simple utility method used to remove illegal symbols from file paths.
     *
     * @param str - file path
     * @return String - modified file path without illegal characters
     */
    private String removeIllegal(String str) {
        // String builder to hold file path
        StringBuilder sb = new StringBuilder(str);
        // Illegal characters
        char[] illegal = {'.', '*', '/', '\\', '>', '<', '\"', '|', ':'};

        for (char c : illegal)
        {
            for (int i = 0; i < sb.length(); ++i)
            {
                // If illegal character found
                if (sb.charAt(i) == c) {
                    // Remove
                    sb.deleteCharAt(i--);
                }
            }
        }
        return sb.toString();
    }
}
