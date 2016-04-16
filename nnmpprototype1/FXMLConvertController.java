package nnmpprototype1;

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
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
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

    @FXML private Label lblProgress;
    @FXML private ProgressBar prgConvert;

    @FXML private Button btnConvert;

    private Stage stage = new Stage();

    private final String[] fileTypes = {"MP3", "AAC", "FLAC", "WAV"};
    private final String[] bitRates = {"92" , "128", "192", "256", "320"};

    public FXMLConvertController(nnmpprototype1.AudioFile input)
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

        btnConvert.setOnAction((ActionEvent e) ->{
            if(cboxType.getSelectionModel().getSelectedIndex() > -1 &&
                    cboxBitRate.getSelectionModel().getSelectedIndex() > -1 &&
                    !tfLocation.getText().equals("")) {

                convertAudio(input, tfLocation.getText(), 1000*Integer.parseInt(cboxBitRate.getSelectionModel().getSelectedItem().toString()));

            }
        });
        tfLocation.setOnMouseClicked((MouseEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(stage);

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

    public void showDialog() {
        stage.show();
    }

    private void convertAudio(nnmpprototype1.AudioFile input, String output, int kbps) { //modify on your convenience
        // create a media reader
        IMediaReader mediaReader = ToolFactory.makeReader(input.getLocation());

        // create a media writer
        IMediaWriter mediaWriter = ToolFactory.makeWriter(output, mediaReader);

        // add a writer to the reader, to create the output file
        mediaReader.addListener(mediaWriter);

        // add a IMediaListner to the writer to change bit rate
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

        prgConvert.setProgress(-1);
        lblProgress.setText("Converting " + input + "....");
        mediaReader.getContainer().setReadRetryCount(200);
        new Thread(() -> {
            // read and decode packets from the source file and
            // and dispatch decoded audio and video to the writer
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

            Platform.runLater(() -> {
                prgConvert.setProgress(1);
                lblProgress.setText("Done!");
            });


            mediaReader.close();
            //mediaWriter.close();

            File audio = new File(output);
            org.jaudiotagger.audio.AudioFile f = null;
            try {
                f = AudioFileIO.read(audio);
                Tag tag = f.getTag();

                tag.setField(FieldKey.TITLE, input.getTitle());
                tag.setField(FieldKey.ARTIST, input.getArtist());
                tag.setField(FieldKey.ALBUM, input.getAlbum());
                tag.setField(FieldKey.TRACK, input.getTrack());
                tag.setField(FieldKey.YEAR, input.getYear());

                f.commit();
            }

            catch (IOException | CannotWriteException | CannotReadException | ReadOnlyFileException | TagException | InvalidAudioFrameException e) {
                e.printStackTrace();
            }

        }).start();
    }


}