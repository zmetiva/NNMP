/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

/**
 *
 * @author zmmetiva, tmetiva
 */

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javax.imageio.ImageIO;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;

public class FXMLDocumentController implements Initializable {
    
    @FXML private Label lblSongPlaying;
    @FXML private Label lblArtistPlaying;
    @FXML private Label lblAlbumPlaying;
    @FXML private ImageView ivAlbumArtPlaying;
    @FXML private Label lblTotalDuration;
    @FXML private Label lblElapsedTime;
    @FXML private TreeView musicTree;
    @FXML private TableView audioTable;
    @FXML private TableColumn trackCol;
    @FXML private TableColumn titleCol;
    @FXML private TableColumn artistCol;
    @FXML private TableColumn albumCol;
    @FXML private TableColumn timeCol;
    @FXML private Button btnPause;
    @FXML private Button btnPlay;
    @FXML private Button btnStop;
    @FXML private Button btnNext;
    @FXML private Button btnPrev;
    @FXML private ListView<nnmpprototype1.AudioFile> listView;
    @FXML private MenuItem importLibraryItem = new MenuItem();
    @FXML private MenuItem quitItem = new MenuItem();
    @FXML private Slider sldVolume;
    @FXML private Slider sldSeekBar;

    private ObservableList<nnmpprototype1.AudioFile> audioTableList = FXCollections.observableArrayList();
    private List<Integer> artistList = new ArrayList<>();
    private NNMPDB db = new NNMPDB();
    private final MediaPlaybackController mediaPlaybackController = new MediaPlaybackController();

    private Timer timer;
    private int seekTime = 0;
    private Boolean isSeeking = false;
    private StringBuilder sb = new StringBuilder();
    
    private final TableContextMenu tblContext = new TableContextMenu();
    private final PlaybackListContextMenu plContext = new PlaybackListContextMenu();
    
    private final PlaybackQueueController playbackQueueController = new PlaybackQueueController();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Image imagePause = new Image(getClass().getResourceAsStream("pause.png"));
        Image imagePlay = new Image(getClass().getResourceAsStream("play.png"));
        Image imageStop = new Image(getClass().getResourceAsStream("stop.png"));
        Image imageNext = new Image(getClass().getResourceAsStream("next.png"));
        Image imagePrev = new Image(getClass().getResourceAsStream("prev.png"));

        btnPause.setGraphic(new ImageView(imagePause));
        btnPlay.setGraphic(new ImageView(imagePlay));
        btnStop.setGraphic(new ImageView(imageStop));
        btnNext.setGraphic(new ImageView(imageNext));
        btnPrev.setGraphic(new ImageView(imagePrev));
        
        mediaPlaybackController.setPlaybackQueueController(playbackQueueController);
        
        trackCol.setCellValueFactory(new PropertyValueFactory<>("track"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        listView.setItems(playbackQueueController.getObsPlaybackList());

        audioTable.setItems(audioTableList);
        
        audioTable.setRowFactory( tv -> {
            
            TableRow<nnmpprototype1.AudioFile> row = new TableRow<>();
            row.setContextMenu(tblContext);
            tblContext.setPlaybackList(playbackQueueController.getPlaybackList());
            
            row.setOnMouseClicked(event -> {
                
                if (event.getClickCount() == 1) {
                    listView.getSelectionModel().clearSelection();
                }
                
                if (event.getClickCount() == 1 && event.getButton() == MouseButton.SECONDARY) {
                    tblContext.setAudioFile(row.getItem());
                }
                
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {

                    if (mediaPlaybackController.isPlaybackActive()) {
                        mediaPlaybackController.stopAudioPlayback();
                    }
                    
                    playbackQueueController.flush();
                    mediaPlaybackController.setPlaybackIndex(0);
                    nnmpprototype1.AudioFile rowData = row.getItem();
                    playbackQueueController.enqueueAudioFile(rowData);
                    
                    play();
                }
            });
            return row ;
        });

        btnPause.setOnAction((ActionEvent e) -> {
            if (mediaPlaybackController.isPlaybackActive()) {
                mediaPlaybackController.pauseAudioPlayback();
            }
        });

        btnStop.setOnAction((ActionEvent actionEvent) -> {
            if (mediaPlaybackController.isPlaybackActive()) {
                mediaPlaybackController.stopAudioPlayback();
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            sldSeekBar.setValue(0);
            lblElapsedTime.setText("0:00");
        });
        
        btnNext.setOnAction((ActionEvent actionEvent) -> {
            if (mediaPlaybackController.isPlaybackActive()) {
                loadMediaUIData();
                mediaPlaybackController.queueNextAudioFile();
                play();
            }
        });
        
        btnPrev.setOnAction((ActionEvent actionEvent) -> {
            if (mediaPlaybackController.isPlaybackActive()) {
                loadMediaUIData();
                mediaPlaybackController.queuePreviousAudioFile();
                play();
            }
        });
        
        btnPlay.setOnAction((ActionEvent actionEvent) -> {
            if (mediaPlaybackController.getPausedStatus()) {
                mediaPlaybackController.resumeAudioPlayback();
                startSeekSlider(seekTime);
                listenForNextItem();
            }
            else {

                if (audioTable.getSelectionModel().getSelectedIndex() > -1) {
                    if (mediaPlaybackController.isPlaybackActive()) {
                        mediaPlaybackController.stopAudioPlayback();
                    }

                    playbackQueueController.flush();
                    mediaPlaybackController.setPlaybackIndex(0);
                    nnmpprototype1.AudioFile rowData = (nnmpprototype1.AudioFile) audioTable.getSelectionModel().getSelectedItem();
                    playbackQueueController.enqueueAudioFile(rowData);

                    play();
                }

                if (listView.getSelectionModel().getSelectedIndex() > -1) {
                    if (mediaPlaybackController.isPlaybackActive()) {
                        mediaPlaybackController.stopAudioPlayback();
                    }
                    mediaPlaybackController.setPlaybackIndex(listView.getSelectionModel().getSelectedIndex());
                    play();
                }
            }
        });
        
        plContext.setPlaybackList(playbackQueueController.getPlaybackList());

        listView.setContextMenu(plContext);

        listView.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if(mouseEvent.getClickCount() == 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

                if (mediaPlaybackController.isPlaybackActive()) {
                    mediaPlaybackController.stopAudioPlayback();
                }

                mediaPlaybackController.setPlaybackIndex(listView.getSelectionModel().getSelectedIndex());
                //nnmpprototype1.AudioFile rowData = listView.getSelectionModel().getSelectedItem();

                play();
            }
            if (mouseEvent.getClickCount() == 1) {
                audioTable.getSelectionModel().clearSelection();
            }
            if (mouseEvent.getClickCount() == 1 && mouseEvent.getButton() == MouseButton.SECONDARY) {
                plContext.setSelectedIndex(listView.getSelectionModel().getSelectedIndex());
            }
        });
        
        File fileDatabase = new File("nnmpdb.db");
        if (fileDatabase.exists()) {
            artistList = db.getAllArtists();
            addItemsToTree();
        }
        
        musicTree.setOnMouseClicked((MouseEvent mouseEvent) -> {

            TreeItem<String> item = (TreeItem<String>) musicTree.getSelectionModel().getSelectedItem();

            if (musicTree.getSelectionModel().getSelectedIndex() >= 0) {

                if (item.getClass().equals(AlbumTreeItem.class)) {

                    if (mouseEvent.getClickCount() == 1) {

                        AlbumTreeItem albumItem = (AlbumTreeItem) item;
                        List<Integer> albumList = db.getSongsByAlbum(albumItem.getId());
                        audioTableList.clear();

                        for (int i = 0; i < albumList.size(); ++i) {

                            List<String> songData = db.getSongData(albumList.get(i));
                            audioTableList.add(
                                    new nnmpprototype1.AudioFile(songData.get(0),
                                            Integer.parseInt(songData.get(1)),
                                            songData.get(2),
                                            songData.get(3),
                                            songData.get(4),
                                            songData.get(5),
                                            songData.get(6),
                                            albumList.get(i)));
                        }
                    }
                }
            }
        });
        
        sldVolume.setMax(6);
        sldVolume.setMin(-80);
        sldVolume.setValue(-37);
        sldVolume.setBlockIncrement(.10);
        sldVolume.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
            mediaPlaybackController.setPlaybackVolume(newVal.floatValue());
        });

        sldSeekBar.setBlockIncrement(1);

        sldSeekBar.setOnMousePressed((MouseEvent event) -> {
            if (mediaPlaybackController.isPlaybackActive()) {
                isSeeking = true;
            }
        });

        sldSeekBar.setOnMouseReleased((MouseEvent event) -> {
            if (mediaPlaybackController.isPlaybackActive()) {

                seekTime = (int) sldSeekBar.getValue();
                mediaPlaybackController.seekAudio(seekTime);
                isSeeking = false;
                startSeekSlider(seekTime);
                listenForNextItem();
            }
        });

        /*
        sldSeekBar.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
            if (isSeeking) {
                seekTime = newVal.intValue();
            }
        }); */

        importLibraryItem.setOnAction((ActionEvent t) -> {
            openImportDialog();
        });
        
        quitItem.setOnAction((ActionEvent t) -> { 
            System.exit(0);
        });
    }

    private Image getAlbumArt(String fileLocation) {
        try {
            File file = new File(fileLocation);
            org.jaudiotagger.audio.AudioFile f = AudioFileIO.read(file);
            Tag tag = f.getTag();
            
            if (tag != null) {
                List<Artwork> artList = tag.getArtworkList();

                if (artList != null && !artList.isEmpty()) {
                    for (int i = 0; i < artList.size(); ++i) {
                        if (artList.get(i) != null && artList.get(i).getBinaryData() != null) {
                            try {
                                ByteArrayInputStream bias = new ByteArrayInputStream(artList.get(i).getBinaryData());
                                BufferedImage img = ImageIO.read(bias);
                                if (img != null) {
                                    return SwingFXUtils.toFXImage(img, null);
                                }
                            } catch (IOException | NullPointerException ex) {}
                        }
                    }
                }
            }
        } catch (CannotReadException | IOException | TagException 
                | ReadOnlyFileException | InvalidAudioFrameException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void openImportDialog() {
        DirectoryChooser dir = new DirectoryChooser();
        dir.setTitle("Import a Library...");

        File path = dir.showDialog(null);

        ImportLibraryController importLibraryController = new ImportLibraryController();
        Platform.runLater(() -> {
            importLibraryController.importMusic(path, db);
            new Thread(() -> {
                while (importLibraryController.isProcessingComplete() == false) {

                }
                artistList = importLibraryController.getArtistList();
                Platform.runLater(() -> {addItemsToTree();});

            }).start();
        });
    }
    
    private void addItemsToTree() {
        
        TreeItem<String> rootItem = new TreeItem("My Music", null);
        TreeItem<String> unknownItems = new TreeItem("Unknown Artist", null);

        for (int i = 0; i < artistList.size(); ++i) {
            ArtistTreeItem artists = new ArtistTreeItem(artistList.get(i));
            List<Integer> albumsByArtist = db.getAlbumsByArtist(artistList.get(i));

            //TODO albumsByArtist.sort() :: By Album Title

            for (int j = 0; j < albumsByArtist.size(); ++j) {
                AlbumTreeItem albums = new AlbumTreeItem(albumsByArtist.get(j));
                artists.getChildren().add(albums);
            }
            rootItem.getChildren().add(artists);
        }
        
        rootItem.setExpanded(true);
        musicTree.setRoot(rootItem);
    }
    
    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    private void play() {

       if (!playbackQueueController.isEmpty() && !mediaPlaybackController.isPlaybackActive()) {

           /*
            if (mediaPlaybackController.isPlaybackActive() && audioTable.getSelectionModel().getSelectedItem() != null) {
                mediaPlaybackController.stopAudioPlayback();
            }
            */

           loadMediaUIData();

           if (timer == null) {
               startSeekSlider(0);
           }
           else {
               resetSeekSlider();
           }

           mediaPlaybackController.playAudioFile();
           listenForNextItem();
       }
    }

    private void listenForNextItem() {
        new Thread(() -> {
            while (mediaPlaybackController.isPlaybackActive() && !mediaPlaybackController.getPausedStatus()) {
                if (mediaPlaybackController.getSongChange()) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Platform.runLater(() -> {
                        if (mediaPlaybackController.getDataIndex() < playbackQueueController.getPlaybackList().getSize()) {
                            loadMediaUIData();

                            if (!mediaPlaybackController.getPausedStatus()) {
                                resetSeekSlider();
                            }
                        }
                    });
                }
            }
            timer.cancel();
            timer = null;

            if (!mediaPlaybackController.getPausedStatus()) {

                seekTime = 0;
                sldSeekBar.setValue(0);

                Platform.runLater(() -> {
                    lblElapsedTime.setText("0:00");
                });
            }
        }).start();
    }

    private void startSeekSlider(int time) {

        final int dur = playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex()).getDuration();

        sldSeekBar.setMin(0);
        sldSeekBar.setMax(dur);
        sldSeekBar.setValue(seekTime);
        seekTime = time;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    if (!mediaPlaybackController.getPausedStatus() && !sldSeekBar.isValueChanging() && !isSeeking) {
                        lblElapsedTime.setText(formatTime(seekTime));
                        sldSeekBar.setValue(seekTime);
                        ++seekTime;
                    }
                });
            }
        }, 0, 1000);
    }

    private void resetSeekSlider() {
        lblElapsedTime.setText("0:00");
        seekTime = 0;
        sldSeekBar.setValue(0);

        if (!playbackQueueController.isEmpty()) {
            sldSeekBar.setMax(playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex()).getDuration());
        }
    }

    private void loadMediaUIData() {
        lblSongPlaying.setText(playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex()).getTitle());
        lblArtistPlaying.setText(playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex()).getArtist());
        lblAlbumPlaying.setText(playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex()).getAlbum());
        lblTotalDuration.setText(formatTime(playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex()).getDuration()));
        ivAlbumArtPlaying.setImage(getAlbumArt(playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex()).getLocation()));
    }

    public String formatTime(int duration) {
        int hour = Math.floorDiv(duration, 3600);
        int min = (duration / 60) % 60;
        int sec = duration % 60;
        //String newTime = "";

        if (hour > 0) {
            sb.append(hour + ":");
           // newTime += hour + ":";
        }
        sb.append(min + ":");
        //newTime += min + ":";

        if (sec < 10) {
            sb.append("0");
            //newTime += "0";
        }
        sb.append(sec);
        //newTime += sec;

        String newTime = sb.toString();
        sb.setLength(0);

        return newTime;
    }

}
