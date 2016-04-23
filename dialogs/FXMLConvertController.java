package dialogs;

import com.xuggle.mediatool.*;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IWritePacketEvent;
import com.xuggle.xuggler.IStreamCoder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Convert Controller - Dialog that converts a audio file to a new audio file in a different format.
 */
public class FXMLConvertController implements Initializable {

    /** Choicebox for audio type **/
    @FXML private ChoiceBox cboxType;

    /** Choicebox for bit rate **/
    @FXML private ChoiceBox cboxBitRate;

    /** Textbox for location **/
    @FXML private TextField tfLocation;

    /** Label for progress **/
    @FXML private Label lblProgress;

    /** Progress bar for progress **/
    @FXML private ProgressBar prgConvert;

    /** Convert button **/
    @FXML private Button btnConvert;

    /** Stage for dialog **/
    private Stage stage = new Stage();

    /** Strings for file types **/
    private final String[] fileTypes = {"MP3", "AAC", "FLAC", "WAV"};

    /** Strings for bit rates **/
    private final String[] bitRates = {"92" , "128", "192", "256", "320"};

    /**
     * Parameterized Constructor - Accepts an audio file for the conversion process.
     *
     * @param input
     */
    public FXMLConvertController(nnmpprototype1.AudioFile input)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLConvert.fxml"));
        fxmlLoader.setController(this);

        // Set the scene to the Stage
        try {
            stage.setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Set event for convert button
        btnConvert.setOnAction((ActionEvent e) ->{

            // Check if all information is entered correctly
            if(cboxType.getSelectionModel().getSelectedIndex() > -1 &&
                    cboxBitRate.getSelectionModel().getSelectedIndex() > -1 &&
                    !tfLocation.getText().equals("")) {

                // Convert the audio file
                convertAudio(input, tfLocation.getText(), 1000*Integer.parseInt(cboxBitRate.getSelectionModel().getSelectedItem().toString()));

            }
        });

        // Set event for location text box
        tfLocation.setOnMouseClicked((MouseEvent e) -> {

            // Get location for newly converted file
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(stage);

            // Set the location text
            tfLocation.setText(file.getAbsolutePath() + "." + cboxType.getSelectionModel().getSelectedItem().toString().toLowerCase());


        });
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cboxType.getItems().addAll(fileTypes);
        cboxBitRate.getItems().addAll(bitRates);
    }

    /**
     * Method that shows the dialog.
     */
    public void showDialog() {
        stage.show();
    }

    /**
     * Method that converts the audio file to a new file and format.
     *
     * @param input the input location
     * @param output the output location
     * @param kbps the desired KBPS
     */
    private void convertAudio(nnmpprototype1.AudioFile input, String output, int kbps) { //modify on your convenience
        // Create a media reader
        IMediaReader mediaReader = ToolFactory.makeReader(input.getLocation());

        // Create a media writer
        IMediaWriter mediaWriter = ToolFactory.makeWriter(output, mediaReader);

        // Add a writer to the reader to create the output file
        mediaReader.addListener(mediaWriter);

        // Add a IMediaListner to the writer to change properties of file
        mediaWriter.addListener(new MediaListenerAdapter() {
            @Override
            public void onWritePacket(IWritePacketEvent event) {
                //super.onWritePacket(event);
                IStreamCoder streamCoder = event.getSource().getContainer().getStream(event.getStreamIndex()).getStreamCoder();
                System.out.println(event.getPacket().getStreamIndex());
            }

            @Override
            public void onAddStream(IAddStreamEvent event) {
                IStreamCoder streamCoder = event.getSource().getContainer().getStream(event.getStreamIndex()).getStreamCoder();
                streamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, false);
                streamCoder.setBitRate(kbps);
                streamCoder.setBitRateTolerance(0);

            }
        });

        // Set the progress to pulse
        prgConvert.setProgress(-1);

        // Set the status text
        lblProgress.setText("Converting " + input + "....");
        mediaReader.getContainer().setReadRetryCount(200);

        // Convert audio file
        new Thread(() -> {
            int i = 0;
            try {
                while (mediaReader.readPacket() != null) {
                    i++;
                }

                while (mediaReader.readPacket() == null) {

                }
            }

            catch (RuntimeException e) {
                while (mediaReader.readPacket() == null) {

                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // Set the satus to complete
            Platform.runLater(() -> {
                prgConvert.setProgress(1);
                lblProgress.setText("Done!");
            });


            // Close the media reader
            mediaReader.close();

            // Read in converted file
            File audio = new File(output);
            org.jaudiotagger.audio.AudioFile f = null;

            // Set all of the metadata from previous file
            try {
                f = AudioFileIO.read(audio);
                Tag tag = f.getTag();

                // Set the metadata
                tag.setField(FieldKey.TITLE, input.getTitle());
                tag.setField(FieldKey.ARTIST, input.getArtist());
                tag.setField(FieldKey.ALBUM, input.getAlbum());
                tag.setField(FieldKey.TRACK, input.getTrack());
                tag.setField(FieldKey.YEAR, input.getYear());

                // Save the changes
                f.commit();
            }

            catch (IOException | CannotWriteException | CannotReadException | ReadOnlyFileException | TagException | InvalidAudioFrameException e) {
                e.printStackTrace();
            }

        }).start();
    }


}