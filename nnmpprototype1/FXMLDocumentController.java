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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javax.imageio.ImageIO;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javafx.scene.layout.AnchorPane;
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

public class FXMLDocumentController implements Initializable, Observer {
    
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
    @FXML private MenuItem importLibraryItem;
    @FXML private MenuItem quitItem;
    @FXML private MenuItem openPlaylist;
    @FXML private MenuItem savePlaylist;
    @FXML private MenuItem ripCd;
    @FXML private MenuItem organizeLibrary;
    @FXML private Slider sldVolume;
    @FXML private Slider sldSeekBar;
    private ObservableList<nnmpprototype1.AudioFile> audioTableList = FXCollections.observableArrayList();
    private List<Integer> artistList = new ArrayList<>();
    private List<Integer> unknownList = new ArrayList<>();
    private NNMPDB db = new NNMPDB();
    private Timer timer;
    private int seekTime = 0;
    private boolean isSeeking = false;
    private boolean playlistFlag = false;
    private StringBuilder sb = new StringBuilder();
    private final TableContextMenu tblContext = new TableContextMenu();
    private final PlaybackListContextMenu plContext = new PlaybackListContextMenu();
    private final NNMPMediaPlayer mediaPlayer = new NNMPMediaPlayer();
    private final String DBNAME = "nnmpdb.db";
    private final String DEFAULTTIME = "0:00";

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initializeButtonImages();
        setTableColNames();

        listView.setItems(mediaPlayer.getObservablePlaybackList());
        initializeTableView();

        plContext.setPlaybackList(mediaPlayer.getPlaybackQueue());
        listView.setContextMenu(plContext);

        getTreeItemsFromDB();
        initializeVolumeSlider();

        sldSeekBar.setBlockIncrement(1);
        mediaPlayer.addChangeObserver(this);
    }

    @FXML protected void handlePlayButtonActionEvent(ActionEvent event) {
        if (mediaPlayer.isPaused()) {
            startSeekSlider(seekTime);
            mediaPlayer.resumeMediaPlayback();
            //listenForNextItem();
        }
        else {

            if (audioTable.getSelectionModel().getSelectedIndex() > -1) {

                playlistFlag = false;

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stopMediaPlayback();
                }

                mediaPlayer.clearPlaybackQueue();
                mediaPlayer.playFileAt(0);
                nnmpprototype1.AudioFile rowData = (nnmpprototype1.AudioFile) audioTable.getSelectionModel().getSelectedItem();
                mediaPlayer.enqueueToPlaybackQueue(rowData);

                play();
            }

            if (listView.getSelectionModel().getSelectedIndex() > -1) {

                playlistFlag = true;

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stopMediaPlayback();
                }
                mediaPlayer.playFileAt(listView.getSelectionModel().getSelectedIndex());
                play();
            }
        }
    }

    @FXML protected void handlePauseButtonActionEvent(ActionEvent event) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pauseMediaPlayback();
            seekTime = (int) sldSeekBar.getValue();

            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }

    @FXML protected void handleStopButtonActionEvent(ActionEvent event) {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stopMediaPlayback();
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        sldSeekBar.setValue(0);
        lblElapsedTime.setText(DEFAULTTIME);
    }

    @FXML protected void handleNextButtonActionEvent(ActionEvent event) {
        if (mediaPlayer.isPlaying()) {
            loadMediaUIData();
            mediaPlayer.playNextFile();
            play();
        }
    }

    @FXML protected void handlePrevButtonActionEvent(ActionEvent event) {
        if (mediaPlayer.isPlaying()) {
            loadMediaUIData();
            mediaPlayer.playPreviousFile();
            play();
        }
    }

    @FXML protected void handleListViewMouseClicked(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

            playlistFlag = true;

            if (mediaPlayer.isPlaying()) {
                if (mediaPlayer.isPaused()) {
                    mediaPlayer.resumeMediaPlayback();
                }
                mediaPlayer.stopMediaPlayback();
            }
            mediaPlayer.playFileAt(listView.getSelectionModel().getSelectedIndex());

            play();
        }
        if (mouseEvent.getClickCount() == 1) {
            audioTable.getSelectionModel().clearSelection();
        }
        if (mouseEvent.getClickCount() == 1 && mouseEvent.getButton() == MouseButton.SECONDARY) {
            plContext.setSelectedIndex(listView.getSelectionModel().getSelectedIndex());
        }
    }

    @FXML protected void handleMusicTreeMouseClicked(MouseEvent mouseEvent) {

        TreeItem<String> item = (TreeItem<String>) musicTree.getSelectionModel().getSelectedItem();

        if (musicTree.getSelectionModel().getSelectedIndex() >= 0) {

            if (mouseEvent.getClickCount() == 1) {

                if (item.getClass().equals(AlbumTreeItem.class)) {
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
                    tblContext.setAudioTableList(audioTableList);

                }

                if (item.getClass().equals(ArtistTreeItem.class)) {
                    ArtistTreeItem artistItem = (ArtistTreeItem) item;
                    List<Integer> albumList = db.getSongsByArtist(artistItem.getId());
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
                    tblContext.setAudioTableList(audioTableList);

                }

                if (item.getClass().equals(UnknownTreeItem.class)) {
                    if (mouseEvent.getClickCount() == 1) {

                        UnknownTreeItem albumItem = (UnknownTreeItem) item;
                        // List<Integer> albumList = db.getSongsByAlbum(albumItem.getId());
                        audioTableList.clear();

                        for (int i = 0; i < unknownList.size(); ++i) {
                            List<String> unknownData = db.getUnknownData(unknownList.get(i));
                            audioTableList.add(
                                    new nnmpprototype1.AudioFile(unknownData.get(1),
                                            Integer.parseInt(unknownData.get(2)),
                                            "",
                                            "",
                                            unknownData.get(0),
                                            "",
                                            "",
                                            0));
                        }
                        tblContext.setAudioTableList(audioTableList);
                    }
                }
            }
        }
        audioTable.scrollTo(0);
    }

    @FXML protected void handleSeekbarMousePressed(MouseEvent mouseEvent) {
        if (mediaPlayer.isPlaying()) {
            isSeeking = true;
        }
    }

    @FXML protected void handleSeekbarMouseReleased(MouseEvent mouseEvent) {
        if (mediaPlayer.isPlaying()) {

            seekTime = (int) sldSeekBar.getValue();
            mediaPlayer.seek(seekTime);
            isSeeking = false;
            sldSeekBar.setValue(seekTime);
            //startSeekSlider(seekTime);
            //listenForNextItem();
        }
    }

    @FXML protected void handleOpenPlaylistMenuItemOnActionEvent(ActionEvent event) {
        M3UPlaylistController playlistController = new M3UPlaylistController();
        playlistController.openPlaylist(mediaPlayer.getPlaybackQueue());
        playlistController = null;
    }

    @FXML protected void handleSavePlaylistMenuItemOnActionEvent(ActionEvent event) {
        M3UPlaylistController playlistController = new M3UPlaylistController();
        playlistController.savePlaylist(mediaPlayer.getPlaybackQueue());
        playlistController = null;
    }

    @FXML protected void handleImportLibraryMenuItemOnActionEvent(ActionEvent event) {
        openImportDialog();
    }

    @FXML protected void handleRipAudioCDItemOnActionEvent(ActionEvent event) {
        FXMLCDRipperController fxmlcdRipperController = new FXMLCDRipperController();

        fxmlcdRipperController.showDialog();
    }

    @FXML protected void handleOrganizeLibraryMenuItemOnActionEvent(ActionEvent event) {
        FXMLOrganizerController organizerController = new FXMLOrganizerController();
        organizerController.showDialog();
    }

    @FXML protected void handleQuitMenuItemOnActionEvent(ActionEvent event) {
        System.exit(0);
    }


    @FXML public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    private void getTreeItemsFromDB() {
        File fileDatabase = new File(DBNAME);
        if (fileDatabase.exists()) {
            artistList = db.getAllArtists();
            unknownList = db.getAllUnknown();
            addItemsToTree();
        }
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
                unknownList = importLibraryController.getUnknownList();

                Platform.runLater(() -> {addItemsToTree();});

            }).start();
        });
    }
    
    private void addItemsToTree() {
        
        TreeItem<String> rootItem = new TreeItem("My Music", null);
        TreeItem<String> unknownItems = new TreeItem("(Unknown Entries)", null);
        UnknownTreeItem unknownAlbum = new UnknownTreeItem("(Unknown Album)");

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
        //System.out.println(unknownList.size());

        unknownItems.getChildren().add(unknownAlbum);

        rootItem.getChildren().add(unknownItems);
        rootItem.setExpanded(true);

        musicTree.setRoot(rootItem);
    }

    private void play() {

       if (!mediaPlayer.isPlaybackQueueEmpty() && !mediaPlayer.isPlaying()) {

           loadMediaUIData();

           if (audioTable.getSelectionModel().getSelectedCells().size() > 0) {
               mediaPlayer.setPlaybackIndex(audioTable.getSelectionModel().getSelectedIndex());
           }
           mediaPlayer.startMediaPlayback();
       }
    }

    /*
    private void listenForNextItem() {
        new Thread(() -> {

            while (mediaPlayer.isPlaying() && !mediaPlayer.isPaused()) {

                if (mediaPlayer.hasSongChanged()) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Platform.runLater(() -> {
                        if (mediaPlayer.getActiveIndex() < mediaPlayer.getPlaybackQueue().getSize()) {
                            loadMediaUIData();

                            if (!mediaPlayer.isPaused()) {
                                resetSeekSlider();
                            }

                            listView.getSelectionModel().select(mediaPlayer.getActiveIndex());
                        }
                    });
                }
            }
            timer.cancel();
            timer = null;

            if (!mediaPlayer.isPaused()) {

                seekTime = 0;
                sldSeekBar.setValue(0);

                Platform.runLater(() -> {
                    lblElapsedTime.setText(DEFAULTTIME);
                });
            }

            if (!playlistFlag && mediaPlayer.getPlaybackQueue().getSize() == 1 && !mediaPlayer.isPaused() && !isSeeking &&
                    (audioTable.getSelectionModel().getSelectedIndex() + 1) < audioTable.getSelectionModel().getTableView().getItems().size() &&
                    !mediaPlayer.isStoppedPressed()) {

                    Platform.runLater(() -> {
                        mediaPlayer.clearPlaybackQueue();
                        mediaPlayer.playFileAt(0);
                        mediaPlayer.setPlaybackIndex(mediaPlayer.getAudioSourceIndex() + 1);
                        audioTable.getSelectionModel().select(mediaPlayer.getAudioSourceIndex());
                        nnmpprototype1.AudioFile rowData = (nnmpprototype1.AudioFile) audioTable.getSelectionModel().getSelectedItem();
                        mediaPlayer.enqueueToPlaybackQueue(rowData);
                        play();
                    });
            }

        }).start();
    }
*/

    private void startSeekSlider(int time) {

        final int dur = mediaPlayer.getActiveFile().getDuration();

        sldSeekBar.setMin(0);
        sldSeekBar.setMax(dur);
        sldSeekBar.setValue(seekTime);
        seekTime = time;

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    if (!mediaPlayer.isPaused() && !sldSeekBar.isValueChanging() && !isSeeking) {
                        formatTime(seekTime);
                        lblElapsedTime.setText(/*formatTime(seekTime)*/ sb.toString());
                        sb.setLength(0);
                        sldSeekBar.setValue(seekTime);
                        ++seekTime;
                    }
                });
            }
        }, 0, 1000);
    }

    private void resetSeekSlider() {
        lblElapsedTime.setText(DEFAULTTIME);
        seekTime = 0;
        sldSeekBar.setValue(0);

        if (!mediaPlayer.isPlaybackQueueEmpty()) {
            sldSeekBar.setMax(mediaPlayer.getActiveFile().getDuration());
        }
    }

    private void loadMediaUIData() {
        lblSongPlaying.setText(mediaPlayer.getActiveFile().getTitle());
        lblArtistPlaying.setText(mediaPlayer.getActiveFile().getArtist());
        lblAlbumPlaying.setText(mediaPlayer.getActiveFile().getAlbum());
        formatTime(mediaPlayer.getActiveFile().getDuration());
        lblTotalDuration.setText(/*formatTime(mediaPlayer.getActiveFile().getDuration())*/ sb.toString());
        sb.setLength(0);
        ivAlbumArtPlaying.setImage(mediaPlayer.getActiveFile().getAlbumArt());
    }

    public void formatTime(int duration) {
        int hour = Math.floorDiv(duration, 3600);
        int min = (duration / 60) % 60;
        int sec = duration % 60;

        if (hour > 0) {
            sb.append(hour + ":");
        }

        if (hour > 0 && min < 10) {
            sb.append("0" + min + ":");
        }
        else {
            sb.append(min + ":");
        }

        if (sec < 10) {
            sb.append("0");
        }
        sb.append(sec);

        //String newTime = sb.toString();
        //sb.setLength(0);

        //return newTime;
    }

    private void initializeTableView() {

        audioTable.setItems(audioTableList);

        audioTable.setRowFactory( tv -> {

            TableRow<nnmpprototype1.AudioFile> row = new TableRow<>();
            row.setContextMenu(tblContext);
            tblContext.setPlaybackList(mediaPlayer.getPlaybackQueue());

            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 1) {
                    listView.getSelectionModel().clearSelection();
                }

                if (event.getClickCount() == 1 && event.getButton() == MouseButton.SECONDARY) {
                    tblContext.setAudioFile(row.getItem());
                }

                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {

                    playlistFlag = false;

                    if (mediaPlayer.isPlaying()) {
                        if (mediaPlayer.isPaused()) {
                            mediaPlayer.resumeMediaPlayback();
                        }
                        mediaPlayer.stopMediaPlayback();
                    }

                    mediaPlayer.clearPlaybackQueue();
                    mediaPlayer.playFileAt(0);
                    nnmpprototype1.AudioFile rowData = row.getItem();
                    mediaPlayer.enqueueToPlaybackQueue(rowData);

                    play();
                }
            });
            return row ;
        });
    }

    private void setTableColNames() {
        trackCol.setCellValueFactory(new PropertyValueFactory<>("track"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
    }

    private void initializeButtonImages() {
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
    }

    private void initializeVolumeSlider() {
        sldVolume.setMax(6);
        sldVolume.setMin(-80);
        sldVolume.setValue(-37);
        sldVolume.setBlockIncrement(.05f);
        sldVolume.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
            mediaPlayer.setVolumeLevel(newVal.floatValue());
        });
    }

    @Override
    public void update() {
        if (mediaPlayer.isPlaying() && !mediaPlayer.isPaused()) {
            if (mediaPlayer.getActiveIndex() < mediaPlayer.getPlaybackQueue().getSize()) {
                Platform.runLater(() -> {
                    loadMediaUIData();
                });

                if (!mediaPlayer.isPaused()) {
                        Platform.runLater(() -> {
                            //resetSeekSlider();
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            startSeekSlider(0);
                        });
                }
                listView.getSelectionModel().select(mediaPlayer.getActiveIndex());
            }
            else {
                timer.cancel();
                timer = null;

                if (!mediaPlayer.isPaused()) {

                    seekTime = 0;
                    sldSeekBar.setValue(0);

                    Platform.runLater(() -> {
                        lblElapsedTime.setText(DEFAULTTIME);
                    });
                }
            }
        }
    }
}
