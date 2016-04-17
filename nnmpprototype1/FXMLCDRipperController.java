package nnmpprototype1;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.xuggler.IStreamCoder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * CD Ripper Dialog - Creates a CD Ripper dialog for the user to rip am audio CD. The user can choose the desired format
 * of the audio file, as well as the destination for the ripped audio files. The user can also add metadata to the
 * audio files that are ripped.
 */
public class FXMLCDRipperController implements Initializable {

    // The choice box for the CD Drives
    @FXML ChoiceBox choiceDrive;

    // The textbox that holds the location
    @FXML TextField txtDest;

    // The list view that holds the files
    @FXML ListView lvFiles;

    // The GUI metadata fields to export to the file
    @FXML TextField txtTitle;
    @FXML TextField txtArtist;
    @FXML TextField txtAlbum;
    @FXML TextField txtTrack;
    @FXML TextField txtYear;

    // The status label and progress bar
    @FXML Label lblStatus;
    @FXML ProgressBar prgBar;

    // The rip cd button
    @FXML Button btnRipCd;

    // The save metadata button
    @FXML CheckBox chkSave;

    // The list of CD tracks to be ripped
    private List<nnmpprototype1.AudioFile> cdTracks = new ArrayList<>();

    // The audio file
    private nnmpprototype1.AudioFile audioFile = new AudioFile();

    // The main stage for the GUI
    private Stage stage = new Stage();

    /**
     * Default Constructor - Creates the GUI for the CD Ripper Dialog.
     */
    public FXMLCDRipperController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLCDRipper.fxml"));
        fxmlLoader.setController(this);

        // Set the scene to the Stage
        try {
            stage.setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //
        lvFiles.getSelectionModel().selectedIndexProperty().addListener((listener) ->{

            // Set the metadata to the selected file on change
            if (lvFiles.getSelectionModel().getSelectedIndex() > -1) {
                audioFile.setArtist(txtArtist.getText());
                audioFile.setTitle(txtTitle.getText());
                audioFile.setAlbum(txtAlbum.getText());
                audioFile.setTrack(txtTrack.getText());
                audioFile.setYear(txtYear.getText());
            }

            // get the new audio file
            audioFile = cdTracks.get(lvFiles.getSelectionModel().getSelectedIndex());

            // If the checkbox is activated, set all of the similar metadata to each selected track
            if (!chkSave.isSelected()) {
                txtAlbum.setText(audioFile.getAlbum());
                txtArtist.setText(audioFile.getArtist());
                txtYear.setText(audioFile.getYear());
                txtTrack.setText(audioFile.getTrack());

            }

            else {
                txtTrack.setText(Integer.toString(lvFiles.getSelectionModel().getSelectedIndex() + 1));
            }

            // Set the title text to the new audio file
            txtTitle.setText(audioFile.getTitle());

        });

        // Add listener for the choicebox
        choiceDrive.getSelectionModel().selectedIndexProperty().addListener((observableValue) -> {

            // Populate the list of CD tracks
            populateList();
        });

        // Add event for the rip CD button
        btnRipCd.setOnAction((ActionEvent e) ->{

            // Start ripping CD
            startRip();
        });

        // Add mouse event for the destination textbox
        txtDest.setOnMouseClicked((mouseEvent) -> {

            // Create directory chooser for the destination and get the path
            DirectoryChooser chooser = new DirectoryChooser();
            File fileForPath = chooser.showDialog(stage);

            // Set the path for the destination textbox
            txtDest.setText(fileForPath.getAbsolutePath());
        });


    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // List of the disk file systems
        List<String> cdFileSystems = new ArrayList<>();

        // Gets the file systems
        FileSystem fs = FileSystems.getDefault();

        // For all of the filesystems
        for (Path rootPath : fs.getRootDirectories())
        {
            try
            {
                FileStore store = Files.getFileStore(rootPath);

                // Get the CD file system
                if (rootPath.toString().contains("cdda")) {
                    //choiceDrive.getItems().add(rootPath);
                }

            }
            catch (IOException e)
            {
                System.out.println(rootPath + ": " + "<error getting store details>");
            }
        }

        // Create a DirectoryChooser and set the path
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(stage);

        // Add the selected path to the choicebox
        choiceDrive.getItems().add(dir.getAbsolutePath());
        choiceDrive.getSelectionModel().select(0);

        // If the directory has files
        if (choiceDrive.getItems().size() > 0) {

            // Populate List with audio CD files
            populateList();
        }

    }


    /**
     * Show Dialog - Shows the dialog.
     */
    public void showDialog() {
        stage.show();
    }

    /**
     * Populate List Function - Populates the list with the CD Tracks.
     */
    private void populateList() {

        // Create a file for the CD path
        File folder = new File(choiceDrive.getSelectionModel().getSelectedItem().toString());

        // Get all of the files from the CD path
        File[] listOfFiles = folder.listFiles();

        // Add each file to the list
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                System.out.println(listOfFile.getName());
                lvFiles.getItems().add(listOfFile.getName());
                cdTracks.add(new AudioFile());
            }
        }
    }

    /**
     * Start Rip Function - Starts the CD Ripping process.
     */
    private void startRip() {

        // Get the path of the CD files
        Path path = Paths.get(txtDest.getText());

        // If the files exist and the location is valid
        if (Files.exists(path) && !txtDest.getText().equals("")) {

            // If the save metadata checkbox is activated
            if (chkSave.isSelected()) {

                // Loop through and set the metadata for the CD tracks
                for (int i = 0; i < lvFiles.getItems().size(); ++i) {
                    cdTracks.get(i).setArtist(txtArtist.getText());
                    cdTracks.get(i).setAlbum(txtAlbum.getText());
                    cdTracks.get(i).setTrack(Integer.toString(i+1));
                    cdTracks.get(i).setYear(txtYear.getText());
                }
            }

            // Create a rip cd thread
            new Thread(() -> {

                // For all of the CD tracks
                for (Integer i = 0; i < lvFiles.getItems().size(); i++) {

                    // Check that the CD tracks have metadata ready. If not, populate with dummy text
                    if (!cdTracks.get(i).getTrack().equals("") || !cdTracks.get(i).getTitle().equals("")) {
                        cdTracks.get(i).setLocation(txtDest.getText() + "/" + cdTracks.get(i).getTrack() + " - " + cdTracks.get(i).getTitle() + ".mp3");
                    } else {
                        cdTracks.get(i).setLocation(txtDest.getText() + "/Track" + Integer.toString(i + 1) + ".mp3");
                    }
                    // Create a media reader for reading the CD data
                    IMediaReader mediaReader = ToolFactory.makeReader(choiceDrive.getSelectionModel().getSelectedItem().toString() + "/" + lvFiles.getItems().get(i));

                    // Create a media writer for ripping the tracks
                    IMediaWriter mediaWriter = ToolFactory.makeWriter(cdTracks.get(i).getLocation(), mediaReader);

                    // Add a writer to the reader, to create the output file
                    mediaReader.addListener(mediaWriter);

                    // Add a IMediaListner to the writer to change bit rate and other data
                    mediaWriter.addListener(new MediaListenerAdapter() {
                        @Override
                        public void onAddStream(IAddStreamEvent event) {
                            IStreamCoder streamCoder = event.getSource().getContainer().getStream(event.getStreamIndex()).getStreamCoder();
                            streamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, false);
                            streamCoder.setBitRate(320000);
                            streamCoder.setBitRateTolerance(0);
                        }
                    });

                    // Create a final int to use in thread
                    final Integer finalI = i;

                    // Change status
                    Platform.runLater(() -> {
                        lblStatus.setText("Converting " + choiceDrive.getSelectionModel().getSelectedItem().toString() + lvFiles.getItems().get(finalI) + "....");

                    });

                    // Convert media
                    try {
                        while (mediaReader.readPacket() == null) {

                        }
                    } catch (RuntimeException e) {
                        while (mediaReader.readPacket() == null) {

                        }
                    }

                    // Save the metadata to the ripped file
                    cdTracks.get(i).saveMetadata();

                    // Increment the progress
                    Platform.runLater(() -> {
                        prgBar.setProgress((double) finalI / (double) lvFiles.getItems().size());
                    });

                }

                // Set progress to complete and status to done
                Platform.runLater(() -> {
                    prgBar.setProgress((double) 1);
                    lblStatus.setText("Complete!");
                });

            }).start();
        }

        // Display error message
        else {
            lblStatus.setText("Choose a valid directory...");
        }
    }


}
