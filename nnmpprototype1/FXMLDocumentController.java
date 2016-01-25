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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;

public class FXMLDocumentController implements Initializable {
    
    @FXML private Label lblSongPlaying;
    @FXML private Label lblArtistPlaying;
    @FXML private Label lblAlbumPlaying;
    @FXML private ImageView ivAlbumArtPlaying;
    @FXML private Label lblTotalDuration;
    @FXML private TreeView musicTree;
    @FXML private TableView audioTable;
    @FXML private TableColumn trackCol;
    @FXML private TableColumn titleCol;
    @FXML private TableColumn artistCol;
    @FXML private TableColumn albumCol;
    @FXML private TableColumn timeCol;
    @FXML private Button btnPlay;
    @FXML private Button btnStop;
    @FXML private Button btnNext;
    @FXML private Button btnPrev;
    @FXML private ListView<nnmpprototype1.AudioFile> listView;
    @FXML private MenuItem importLibraryItem = new MenuItem();
    @FXML private MenuItem quitItem = new MenuItem();
    @FXML private Slider sldVolume;
    
    private ObservableList<nnmpprototype1.AudioFile> audioTableList = FXCollections.observableArrayList();
    private List<Integer> artistList = new ArrayList<>();
    private NNMPDB db = new NNMPDB();
    private final MediaPlaybackController mediaPlaybackController = new MediaPlaybackController();
    
    
    private final TableContextMenu tblContext = new TableContextMenu();
    private final PlaybackListContextMenu plContext = new PlaybackListContextMenu();
    
    private final PlaybackQueueController playbackQueueController = new PlaybackQueueController(); 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        Image imagePlay = new Image(getClass().getResourceAsStream("play.png"));
        Image imageStop = new Image(getClass().getResourceAsStream("stop.png"));
        Image imageNext = new Image(getClass().getResourceAsStream("next.png"));
        Image imagePrev = new Image(getClass().getResourceAsStream("prev.png"));

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
                
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    
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
        
        btnStop.setOnAction((ActionEvent actionEvent) -> {
            if (mediaPlaybackController.isPlaybackActive()) {
                mediaPlaybackController.stopAudioPlayback();                
            }
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
            nnmpprototype1.AudioFile file = (nnmpprototype1.AudioFile) audioTable.getSelectionModel().getSelectedItem();
            play();
        });
        
        plContext.setPlaybackList(playbackQueueController.getPlaybackList());
        listView.setContextMenu(plContext);
        listView.setOnMouseClicked((MouseEvent mouseEvent) -> {
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
                                            albumList.get(i)));
                            }
                        }
                    }
                }
        });
        
        sldVolume.setMax(6);
        sldVolume.setMin(-80);
        sldVolume.setValue(-37);
        sldVolume.setBlockIncrement(1);
        sldVolume.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
            mediaPlaybackController.setPlaybackVolume(newVal.floatValue());
        });
        
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
            mediaPlaybackController.playAudioFile();

            Thread t = new Thread() {
                @Override
                public void run() {
                    while (mediaPlaybackController.isPlaybackActive()) {
                        if (mediaPlaybackController.getSongChange()) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            Platform.runLater(() -> {
                                if (mediaPlaybackController.getDataIndex() < playbackQueueController.getPlaybackList().getSize()) {
                                    loadMediaUIData();
                                }
                            });
                        }
                    }
                }
            };
            t.start();
       }
   }
   
   private void loadMediaUIData() {
        lblSongPlaying.setText(playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex()).getTitle());
        lblArtistPlaying.setText(playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex()).getArtist());
        lblAlbumPlaying.setText(playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex()).getAlbum());
        ivAlbumArtPlaying.setImage(getAlbumArt(playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex()).getLocation()));
        lblTotalDuration.setText(playbackQueueController.getPlaybackList().getFileAt(mediaPlaybackController.getDataIndex()).getTime());
   }
   
}
