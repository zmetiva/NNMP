
package dialogs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

/**
 * Import Progress Controller - A dialog that displays the current progress of the importing
 * of the songs.
 *
 * @author zmmetiva
 */
public class FXMLImportProgressController implements Initializable {

    @FXML Label lblSong = new Label();
    @FXML ProgressBar prgBar = new ProgressBar();
    
    Stage stage = new Stage();
    
    public FXMLImportProgressController() {

        // Set the FXML for the controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLImportProgress.fxml"));
        fxmlLoader.setController(this);

        // Set the scene to the Stage
        try {
            stage.setScene(new Scene((Parent) fxmlLoader.load()));
            prgBar.setProgress(-1);
            
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    /**
     * Method that shows the dialog.
     */
    public void showDialog() {
        stage.show();

    }

    /**
     * Method that sets the text for the label.
     *
     * @param label the text for the label
     */
    public void setLabel(String label) {
        Platform.runLater(() -> {lblSong.setText(label);});
        
    }

    /**
     * Method that sets the current progress of the progress bar.
     *
     * @param value the current progress
     */
    public void setProgress(float value) {
        Platform.runLater(() -> {prgBar.setProgress(value);});
    }

    /**
     * Method that closes the dialog.
     */
    public void closeDialog() {
        Platform.runLater(() -> {stage.close();});
    }

    /**
     * method that returns the progress from the progress bar.
     *
     * @return progress from the progress bar.
     */
    public double getProgress() {
        return prgBar.getProgress();
    }
    
}
