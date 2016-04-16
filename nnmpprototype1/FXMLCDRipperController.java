package nnmpprototype1;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.xuggler.IStreamCoder;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by zmmetiva on 3/28/16.
 */
public class FXMLCDRipperController implements Initializable {

    @FXML ChoiceBox choiceDrive;
    @FXML TextField txtDest;
    @FXML ListView lvFiles;

    @FXML TextField txtTitle;
    @FXML TextField txtArtist;
    @FXML TextField txtAlbum;
    @FXML TextField txtTrack;
    @FXML TextField txtYear;



    @FXML Label lblStatus;
    @FXML ProgressBar prgBar;

    @FXML Button btnRipCd;
    @FXML CheckBox chkSave;

    private List<nnmpprototype1.AudioFile> cdTracks = new ArrayList<>();
    private nnmpprototype1.AudioFile audioFile = new AudioFile();

    private Stage stage = new Stage();

    public FXMLCDRipperController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLCDRipper.fxml"));
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

        lvFiles.getSelectionModel().selectedIndexProperty().addListener((listener) ->{

            if (lvFiles.getSelectionModel().getSelectedIndex() > -1) {
                audioFile.setArtist(txtArtist.getText());
                audioFile.setTitle(txtTitle.getText());
                audioFile.setAlbum(txtAlbum.getText());
                audioFile.setTrack(txtTrack.getText());
                audioFile.setYear(txtYear.getText());
            }
            audioFile = cdTracks.get(lvFiles.getSelectionModel().getSelectedIndex());

            if (!chkSave.isSelected()) {
                txtAlbum.setText(audioFile.getAlbum());
                txtArtist.setText(audioFile.getArtist());
                txtYear.setText(audioFile.getYear());
                txtTrack.setText(audioFile.getTrack());

            }

            else {
                txtTrack.setText(Integer.toString(lvFiles.getSelectionModel().getSelectedIndex() + 1));
            }

            txtTitle.setText(audioFile.getTitle());


        });

        choiceDrive.getSelectionModel().selectedIndexProperty().addListener((observableValue) -> {
            populateList();
        });

        btnRipCd.setOnAction((ActionEvent e) ->{
            startRip();
        });

        txtDest.setOnMouseClicked((mouseEvent) -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File fileForPath = chooser.showDialog(stage);
            txtDest.setText(fileForPath.getAbsolutePath());
        });


    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        /*File[] paths;
        FileSystemView fsv = FileSystemView.getFileSystemView();

// returns pathnames for files and directory
        paths = File.listRoots();

// for each pathname in pathname array
        for(File path:paths)
        {
            // prints file and directory paths
            System.out.println("Drive Name: "+path);
            System.out.println("Description: "+fsv.getSystemTypeDescription(path));
        }*/


        List<String> cdFileSystems = new ArrayList<>();

        FileSystem fs = FileSystems.getDefault();

        for (Path rootPath : fs.getRootDirectories())
        {
            try
            {
                FileStore store = Files.getFileStore(rootPath);
                if (rootPath.toString().contains("cdda")) {
                    //choiceDrive.getItems().add(rootPath);
                }


                System.out.println(rootPath);

            }
            catch (IOException e)
            {
                System.out.println(rootPath + ": " + "<error getting store details>");
            }
        }
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(stage);

        choiceDrive.getItems().add(dir.getAbsolutePath());
        choiceDrive.getSelectionModel().select(0);
        if (choiceDrive.getItems().size() > 0) {

            populateList();
        }

    }


    public void showDialog() {
        stage.show();
    }

    private void populateList() {

        System.out.print(choiceDrive.getSelectionModel().getSelectedItem().toString());
        File folder = new File(choiceDrive.getSelectionModel().getSelectedItem().toString());
        File[] listOfFiles = folder.listFiles();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                System.out.println(listOfFile.getName());
                lvFiles.getItems().add(listOfFile.getName());
                cdTracks.add(new AudioFile());
            }
        }
    }

    private void startRip() {
        Path path = Paths.get(txtDest.getText());
        if (Files.exists(path) && !txtDest.getText().equals("")) {
            if (chkSave.isSelected()) {
                for (int i = 0; i < lvFiles.getItems().size(); ++i) {
                    cdTracks.get(i).setArtist(txtArtist.getText());
                    cdTracks.get(i).setAlbum(txtAlbum.getText());
                    cdTracks.get(i).setTrack(Integer.toString(i+1));
                    cdTracks.get(i).setYear(txtYear.getText());
                }
            }
            new Thread(() -> {
                for (Integer i = 0; i < lvFiles.getItems().size(); i++) {

                    if (!cdTracks.get(i).getTrack().equals("") || !cdTracks.get(i).getTitle().equals("")) {
                        cdTracks.get(i).setLocation(txtDest.getText() + "/" + cdTracks.get(i).getTrack() + " - " + cdTracks.get(i).getTitle() + ".mp3");
                    } else {
                        cdTracks.get(i).setLocation(txtDest.getText() + "/Track" + Integer.toString(i + 1) + ".mp3");
                    }
                    // create a media reader
                    IMediaReader mediaReader = ToolFactory.makeReader(choiceDrive.getSelectionModel().getSelectedItem().toString() + "/" + lvFiles.getItems().get(i));

                    // create a media writer
                    IMediaWriter mediaWriter = ToolFactory.makeWriter(cdTracks.get(i).getLocation(), mediaReader);

                    // add a writer to the reader, to create the output file
                    mediaReader.addListener(mediaWriter);

                    // add a IMediaListner to the writer to change bit rate
                    mediaWriter.addListener(new MediaListenerAdapter() {
                        @Override
                        public void onAddStream(IAddStreamEvent event) {
                            IStreamCoder streamCoder = event.getSource().getContainer().getStream(event.getStreamIndex()).getStreamCoder();
                            streamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, false);
                            streamCoder.setBitRate(320000);
                            streamCoder.setBitRateTolerance(0);
                        }
                    });

                    final Integer finalI = i;
                    Platform.runLater(() -> {
                        lblStatus.setText("Converting " + choiceDrive.getSelectionModel().getSelectedItem().toString() + lvFiles.getItems().get(finalI) + "....");

                    });

                    try {
                        while (mediaReader.readPacket() == null) {

                        }
                    } catch (RuntimeException e) {
                        while (mediaReader.readPacket() == null) {

                        }
                    }

                    cdTracks.get(i).saveMetadata();

                    Platform.runLater(() -> {
                        prgBar.setProgress((double) finalI / (double) lvFiles.getItems().size());
                    });

                }

                Platform.runLater(() -> {
                    prgBar.setProgress((double) 1);
                    lblStatus.setText("Complete!");
                });
            }).start();
        }
        else {
            lblStatus.setText("Choose a valid directory...");
        }
    }


}
