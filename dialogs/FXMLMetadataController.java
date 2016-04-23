package dialogs;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import nnmpprototype1.NNMPDB;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Metadata Controller - Dialog that allows the user to edit the metadata of an audio file.
 */
public class FXMLMetadataController implements Initializable {

    /** Textbox for the title of the audio tag **/
    @FXML TextField txtTitle = new TextField();

    /** Textbox for the artist of the audio tag **/
    @FXML TextField txtArtist = new TextField();

    /** Textbox for the album of the audio tag **/
    @FXML TextField txtAlbum = new TextField();

    /** Textbox for the track of the audio tag **/
    @FXML TextField txtTrack = new TextField();

    /** Textbox for the year of the audio tag **/
    @FXML TextField txtYear = new TextField();

    /** Image view for the album art of the audio tag **/
    @FXML ImageView ivArtwork = new ImageView();

    /** Button to save the metadata **/
    @FXML Button btnSave = new Button();

    /** Image location of the new album art **/
    private String newImgLoc;

    /** The audio file for the metadata **/
    private nnmpprototype1.AudioFile audioFile;

    /** The stage of the dialog **/
    private Stage stage = new Stage();

    /**
     * Parameterized Constructor - Sets the dialog up and uses the audio file for the metadata.
     *
     * @param file the audio file
     */
    public FXMLMetadataController(nnmpprototype1.AudioFile file) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLMetadata.fxml"));
        fxmlLoader.setController(this);

        // Set the audio file
        audioFile = file;

        // Set the scene to the Stage
        try {
            stage.setScene(new Scene((Parent) fxmlLoader.load()));
            stage.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnSave.setOnAction(event -> {
            // Set all information to the textboxes
            audioFile.setTitle(txtTitle.getText());
            audioFile.setArtist(txtArtist.getText());
            audioFile.setAlbum(txtAlbum.getText());
            audioFile.setTrack(txtTrack.getText());
            audioFile.setYear(txtYear.getText());

            // Save the metadata to the file
            audioFile.saveMetadata();

            // Save the album art
            if (newImgLoc != null) {
                audioFile.saveAlbumArt(newImgLoc);
            }

            // Close the dialog
            stage.close();

            // Update the song in the DB
            NNMPDB db = NNMPDB.getInstance();
            db.updateSong(audioFile.getSongId(), txtTrack.getText(), txtTitle.getText(), txtAlbum.getText(), txtArtist.getText(), txtYear.getText());
        });

        // Set the event ehn Image view is clicked
        ivArtwork.setOnMouseClicked((MouseEvent e) -> {

            // Create a file chooser to allow the user to pick an image file
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("JPEG Images (*.jpg, *jpeg)", "*.jpg", "*.JPG");
            fileChooser.getExtensionFilters().add(extensionFilter);

            // Get the image from the file chooser
            File imgFile = fileChooser.showOpenDialog(stage);

            // Set the image to the dialog and save its location
            ivArtwork.setImage(new Image("file://" + imgFile.getAbsolutePath()));
            newImgLoc = imgFile.getAbsolutePath();


        });
    }

    /**
     * Method that shows the dialog.
     */
    public void showDialog() {

        // Set all information to the textboxes
        txtTitle.setText(audioFile.getTitle());
        txtArtist.setText(audioFile.getArtist());
        txtAlbum.setText(audioFile.getAlbum());
        txtTrack.setText(audioFile.getTrack());
        txtYear.setText(audioFile.getYear());
        ivArtwork.setImage(audioFile.getAlbumArt());

        // Show the dialog
        stage.show();
    }

    /**
     * Method that shows and waits the dialog.
     */
    public void showAndWaitDialog() {

        // Set all information to the textboxes
        txtTitle.setText(audioFile.getTitle());
        txtArtist.setText(audioFile.getArtist());
        txtAlbum.setText(audioFile.getAlbum());
        txtTrack.setText(audioFile.getTrack());
        txtYear.setText(audioFile.getYear());
        ivArtwork.setImage(audioFile.getAlbumArt());

        // Show the dialog
        stage.showAndWait();
    }

    /**
     * Method that closes the dialog.
     */
    public void closeDialog() {
        Platform.runLater(() -> {
            stage.close();
        });
    }

    /**
     * Method that returns the new audio file.
     *
     * @return the audio file
     */
    public nnmpprototype1.AudioFile getUpdatedAudioFile() {
        return audioFile;
    }
}