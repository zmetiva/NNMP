package nnmpprototype1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Tyler on 1/26/2016.
 */
public class FXMLConvertController implements Initializable {

    @FXML private ChoiceBox cboxType;
    @FXML private ChoiceBox cboxBitRate;
    @FXML private TextField tfLocation;

    Stage stage = new Stage();

    public FXMLConvertController()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLConvert.fxml"));
        fxmlLoader.setController(this);

        // Nice to have this in a load() method instead of constructor, but this seems to be the convention.
        try
        {
            stage.setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cboxType.setValue("MP3");
        cboxBitRate.setValue("320 kbps");
    }

    public void showDialog() {
        stage.show();
    }
}
