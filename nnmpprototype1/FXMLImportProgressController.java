/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

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
 * FXML Controller class
 *
 * @author zmmetiva
 */
public class FXMLImportProgressController implements Initializable {

    @FXML Label lblSong = new Label();
    @FXML ProgressBar prgBar = new ProgressBar();
    
    Stage stage = new Stage();
    
    public FXMLImportProgressController()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLImportProgress.fxml"));
        fxmlLoader.setController(this);

        // Nice to have this in a load() method instead of constructor, but this seems to be the convention.
        try
        {
            stage.setScene(new Scene((Parent) fxmlLoader.load()));
            prgBar.setProgress(0);
            
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
        // TODO
    }


    public void showDialog() {
        stage.show();

    }
    
    public void setLabel(String label) {
        Platform.runLater(() -> {lblSong.setText(label);});
        
    }
    
    public void setProgress(float value) {
        Platform.runLater(() -> {prgBar.setProgress(value);});
    }
    
    public void closeDialog() {
        Platform.runLater(() -> {stage.close();});
    }
    
    public double getProgress() {
        return prgBar.getProgress();
    }
    
}
