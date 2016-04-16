package nnmpprototype1;

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
 * Created by Tyler on 3/29/2016.
 */
public class FXMLOrganizerController implements Initializable {

    @FXML private TextField txtFilePath;
    @FXML private Button btnOK;
    @FXML private Label lblStatus;

    private Stage stage = new Stage();
    private File path = null;

    public FXMLOrganizerController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLOrganizer.fxml"));
        fxmlLoader.setController(this);

        try {
            stage.setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        txtFilePath.setOnMouseClicked((MouseEvent e) -> {
            DirectoryChooser dir = new DirectoryChooser();
            path = dir.showDialog(null);
            txtFilePath.setText(path.toPath().toString());
        });

        btnOK.setOnAction((ActionEvent e) -> {
            //lblStatus.setText("Starting...");
            organize();
            lblStatus.setText("Complete");
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void showDialog() {
        stage.show();
    }

    private void organize() {
        StringBuilder sb = new StringBuilder();
        String loc = path.toPath().toString();
        List<File> list = new ArrayList<>();

        try {
            Files.walk(Paths.get(loc)).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && filePath.toFile().getName().endsWith("mp3")) {
                    list.add(filePath.toFile());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            Tag tag = af.getTag();

            sb.append(loc + "/" + removeIllegal(tag.getFirst(FieldKey.ARTIST)));
            File artistDir = new File(sb.toString());
            //lblStatus.setText(artistDir.toString());

            if (Files.notExists(artistDir.toPath())) {
                artistDir.mkdir();
            }

            sb.append("/" + removeIllegal(tag.getFirst(FieldKey.ALBUM)));
            File albumDir = new File(sb.toString());

            if (Files.notExists(albumDir.toPath())) {
                albumDir.mkdir();
            }
            file.renameTo(new File(albumDir.toString() + "/" + file.getName()));
            sb.setLength(0);
        }
    }

    private String removeIllegal(String str) {
        StringBuilder sb = new StringBuilder(str);
        char[] illegal = {'.', '*', '/', '\\', '>', '<', '\"', '|', ':'};

        for (char c : illegal)
        {
            for (int i = 0; i < sb.length(); ++i)
            {
                if (sb.charAt(i) == c) {
                    sb.deleteCharAt(i--);
                }
            }
        }
        return sb.toString();
    }
}
