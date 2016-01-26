package nnmpprototype1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Zach on 1/25/2016.
 */
public class FXMLMetadataController implements Initializable {
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


    /**
     * FXML Controller class
     *
     * @author zmmetiva
     */


    @FXML TextField txtTitle = new TextField();
    @FXML TextField txtArtist = new TextField();
    @FXML TextField txtAlbum = new TextField();
    @FXML TextField txtTrack = new TextField();
    @FXML TextField txtYear = new TextField();

    @FXML ImageView ivArtwork = new ImageView();

    @FXML Button btnSave = new Button();

    nnmpprototype1.AudioFile audioFile;


    Stage stage = new Stage();

    public FXMLMetadataController(nnmpprototype1.AudioFile file) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLMetadata.fxml"));
        fxmlLoader.setController(this);

        audioFile = file;

        // Nice to have this in a load() method instead of constructor, but this seems to be the convention.
        try {
            stage.setScene(new Scene((Parent) fxmlLoader.load()));

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
            audioFile.setTitle(txtTitle.getText());
            audioFile.setArtist(txtArtist.getText());
            audioFile.setAlbum(txtAlbum.getText());
            audioFile.setTrack(txtTrack.getText());
            audioFile.setYear(txtYear.getText());

            audioFile.saveMetadata();

            stage.close();

            //NNMPDB db;

            //db.updateSong();
        });
    }

    public void showDialog() {
        stage.show();
    }

    public void showAndWaitDialog() {
        txtTitle.setText(audioFile.getTitle());
        txtArtist.setText(audioFile.getArtist());
        txtAlbum.setText(audioFile.getAlbum());
        txtTrack.setText(audioFile.getTrack());
        txtYear.setText(audioFile.getYear());
        //ivArtwork.setImage(audioFile.get);
        stage.showAndWait();
    }

    public void closeDialog() {
        Platform.runLater(() -> {
            stage.close();
        });
    }

}