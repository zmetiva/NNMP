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

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

import controllers.ImportLibraryController;
import controllers.M3UPlaylistController;
import dialogs.FXMLCDRipperController;
import dialogs.FXMLOrganizerController;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.DirectoryChooser;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import menus.PlaybackListContextMenu;
import menus.TableContextMenu;

/**
 *
 */
public class FXMLDocumentController implements Initializable, Observer {

    /** Label used to display the title of the currently playing song */
    @FXML private Label lblSongPlaying;
    /** Label used to display the artist of the currently playing song */
    @FXML private Label lblArtistPlaying;
    /** Label used to display the album of the currently playing song */
    @FXML private Label lblAlbumPlaying;
    /** ImageView used to display the album art of the currently playing song */
    @FXML private ImageView ivAlbumArtPlaying;
    /** Label used to display the duration of the currently playing song */
    @FXML private Label lblTotalDuration;
    /** Label used to display the elapsed time of the currently playing song */
    @FXML private Label lblElapsedTime;
    /** TreeView used to display the user's media library */
    @FXML private TreeView musicTree;
    /** TableView used to display all audio files on a particular album */
    @FXML private TableView audioTable;
    /** Table column used to display track number */
    @FXML private TableColumn trackCol;
    /** Table column used to audio file title */
    @FXML private TableColumn titleCol;
    /** Table column used to display audio file artist */
    @FXML private TableColumn artistCol;
    /** Table column used to display audio file album */
    @FXML private TableColumn albumCol;
    /** Table column used to display track duration */
    @FXML private TableColumn timeCol;
    /** Button used to control pause functionality */
    @FXML private Button btnPause;
    /** Button used to control play functionality */
    @FXML private Button btnPlay;
    /** Button used to control stop functionality */
    @FXML private Button btnStop;
    /** Button used to control skip to next track functionality */
    @FXML private Button btnNext;
    /** Button used to control skip to previous track functionality */
    @FXML private Button btnPrev;
    /** ListView used to display user's playback queue */
    @FXML private ListView<nnmpprototype1.AudioFile> listView;
    /** MenuItem used to handle import library functionality */
    @FXML private MenuItem importLibraryItem;
    /** MenuItem used to handle exit functionality */
    @FXML private MenuItem quitItem;
    /** MenuItem used to handle open playlist functionality */
    @FXML private MenuItem openPlaylist;
    /** MenuItem used to handle save playlist functionality */
    @FXML private MenuItem savePlaylist;
    /** MenuItem used to handle rip CD functionality */
    @FXML private MenuItem ripCd;
    /** MenuItem used to handle organize library functionality */
    @FXML private MenuItem organizeLibrary;
    /** Slider that controls the volume level of audio playback */
    @FXML private Slider sldVolume;
    /** Slider that acts as a seekbar */
    @FXML private Slider sldSeekBar;
    /** ObservableList used to store audio files displayed via the TableView object */
    private ObservableList<nnmpprototype1.AudioFile> audioTableList = FXCollections.observableArrayList();
    /** List used to store all artists */
    private List<Integer> artistList = new ArrayList<>();
    /** List used to store all unknown items */
    private List<Integer> unknownList = new ArrayList<>();
    /** NNMPDB reference to singleton NNMPDB object */
    private NNMPDB db = NNMPDB.getInstance();
    /** Timer used to update elapsed time */
    private Timer timer;
    /** int holding the desired seek time */
    private int seekTime = 0;
    /** boolean indicating if the user has requested a seek operation */
    private volatile boolean isSeeking = false;
    /** boolean indicating if the playlist is in a valid state */
    private boolean playlistFlag = false;
    /** StringBuilder used to update elapsed time label */
    private StringBuilder sb = new StringBuilder();
    /** TableContextMenu used to display context menu with valid operations */
    private final TableContextMenu tblContext = new TableContextMenu();
    /** PlaybackListContextMenu used to display context menu with valid operations */
    private final PlaybackListContextMenu plContext = new PlaybackListContextMenu();
    /** NNMPMediaPlayer reference to singleton NNMPMediaPlayer object */
    private final NNMPMediaPlayer mediaPlayer = NNMPMediaPlayer.getInstance();
    /** String storing title of generated database file */
    private final String DBNAME = "nnmpdb.db";
    /** String storing the default elapsed time value */
    private final String DEFAULTTIME = "0:00";

    /**
     * Initialize method used to set the initial state of the main NNMP GUI.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Initialize all button images
        initializeButtonImages();
        // Set all column names in TableView
        setTableColNames();

        // Populate playback queue
        listView.setItems(mediaPlayer.getObservablePlaybackList());
        // Initialize table view
        initializeTableView();

        // Bind context menu to playback queue
        plContext.setPlaybackList(mediaPlayer.getPlaybackQueue());
        listView.setContextMenu(plContext);

        // Populate TreeView with data from database
        getTreeItemsFromDB();
        // Initialize volume slider to default value
        initializeVolumeSlider();

        // Set seekbar increment to 1
        sldSeekBar.setBlockIncrement(1);
        // Set GUI as observer to media player
        mediaPlayer.addChangeObserver(this);
    }

    /**
     * Method that handles the functionality of the play button on the main NNMP GUI.
     *
     * @param event - triggered ActionEvent
     */
    @FXML protected void handlePlayButtonActionEvent(ActionEvent event) {
        // If playback paused
        if (mediaPlayer.isPaused()) {
            // Resume seek slider
            startSeekSlider(seekTime);
            // Resume audio playback
            mediaPlayer.resumeMediaPlayback();
        }
        else {
            // If valid file in audio table selected
            if (audioTable.getSelectionModel().getSelectedIndex() > -1) {
                // Toggle playlist flag
                playlistFlag = false;

                // If playback active
                if (mediaPlayer.isPlaying()) {
                    // Stop current playback
                    mediaPlayer.stopMediaPlayback();
                }

                // Flush playback queue
                mediaPlayer.clearPlaybackQueue();
                // Play first file in queue
                mediaPlayer.playFileAt(0);
                // Retrieve audio file object from table
                nnmpprototype1.AudioFile rowData = (nnmpprototype1.AudioFile) audioTable.getSelectionModel().getSelectedItem();
                // Add file to playback queue
                mediaPlayer.enqueueToPlaybackQueue(rowData);

                // Start playback
                play();
            }
            // If valid file in playback queue selected
            if (listView.getSelectionModel().getSelectedIndex() > -1) {
                // Toggle playlist flag
                playlistFlag = true;

                // If playback active
                if (mediaPlayer.isPlaying()) {
                    // Stop playback
                    mediaPlayer.stopMediaPlayback();
                }
                // Queue selected file in playlist
                mediaPlayer.playFileAt(listView.getSelectionModel().getSelectedIndex());

                // Start playback
                play();
            }
        }
    }

    /**
     * Method that handles the functionality of the pause button on the main NNMP GUI.
     *
     * @param event - triggered ActionEvent
     */
    @FXML protected void handlePauseButtonActionEvent(ActionEvent event) {
        // If playback active
        if (mediaPlayer.isPlaying()) {
            // Pause playback
            mediaPlayer.pauseMediaPlayback();

            // Save current elapsed time as seek value
            seekTime = (int) sldSeekBar.getValue();

            // If timer exists
            if (timer != null) {
                // Destroy timer
                timer.cancel();
                timer = null;
            }
        }
    }

    /**
     * Method that handles the functionality of the stop button on the main NNMP GUI.
     *
     * @param event - triggered ActionEvent
     */
    @FXML protected void handleStopButtonActionEvent(ActionEvent event) {
        // If playback active
        if (mediaPlayer.isPlaying() || mediaPlayer.isPaused()) {
            // Stop playback
            mediaPlayer.stopMediaPlayback();
        }

        // If timer exists
        if (timer != null) {
            // Destroy timer
            timer.cancel();
            timer = null;
        }
        // Reset seek value and elapsed time to default
        sldSeekBar.setValue(0);
        lblElapsedTime.setText(DEFAULTTIME);
    }

    /**
     * Method that handles the functionality of the skip next button on the main NNMP GUI.
     *
     * @param event - triggered ActionEvent
     */
    @FXML protected void handleNextButtonActionEvent(ActionEvent event) {
        // If playback active
        if (mediaPlayer.isPlaying()) {
            // Load updated UI data
            loadMediaUIData();
            // Queue next file in playback queue
            mediaPlayer.playNextFile();
            // Start playback
            play();
        }
    }

    /**
     * Method that handles the functionality of the skip previous button on the main NNMP GUI.
     *
     * @param event - triggered ActionEvent
     */
    @FXML protected void handlePrevButtonActionEvent(ActionEvent event) {
        // If playbackactive
        if (mediaPlayer.isPlaying()) {
            // Load updated UI data
            loadMediaUIData();
            // Queue previous file in playback queue
            mediaPlayer.playPreviousFile();
            // Start playback
            play();
        }
    }

    /**
     * Method that handles the functionality of the single, double and right clicks on the playback queue.
     *
     * @param mouseEvent - triggered MouseEvent
     */
    @FXML protected void handleListViewMouseClicked(MouseEvent mouseEvent) {
        // If item double clicked (left mouse button)
        if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            // Toggle playlist flag
            playlistFlag = true;

            // If playback active
            if (mediaPlayer.isPlaying()) {
                // If playback paused
                if (mediaPlayer.isPaused()) {
                    // Resume playback
                    mediaPlayer.resumeMediaPlayback();
                }
                // Stop playback
                mediaPlayer.stopMediaPlayback();
            }
            // Queue file clicked in playback queue
            mediaPlayer.playFileAt(listView.getSelectionModel().getSelectedIndex());

            // Start playback
            play();
        }
        // If single clicked (left mouse button)
        if (mouseEvent.getClickCount() == 1) {
            // Remove focus from previously focused item
            audioTable.getSelectionModel().clearSelection();
        }
        // If single clicked (right mouse button)
        if (mouseEvent.getClickCount() == 1 && mouseEvent.getButton() == MouseButton.SECONDARY) {
            // Show playback queue context menu
            plContext.setSelectedIndex(listView.getSelectionModel().getSelectedIndex());
        }
    }

    /**
     * Method that handles the functionality of single left clicks on the music tree.
     *
     * @param mouseEvent - triggered MouseEvent
     */
    @FXML protected void handleMusicTreeMouseClicked(MouseEvent mouseEvent) {

        // Create a TreeItem from the selected item
        TreeItem<String> item = (TreeItem<String>) musicTree.getSelectionModel().getSelectedItem();

        // If the TreeItem is valid
        if (musicTree.getSelectionModel().getSelectedIndex() >= 0) {

            // If it is a single click
            if (mouseEvent.getClickCount() == 1) {

                // Check to see if the clicked item is an AlbumTreeItem
                if (item.getClass().equals(AlbumTreeItem.class)) {

                    // Convert the TreeItem to an AlbumTreeItem and get all songs from album
                    AlbumTreeItem albumItem = (AlbumTreeItem) item;
                    List<Integer> albumList = db.getSongsByAlbum(albumItem.getId());

                    // Clear the table of songs
                    audioTableList.clear();

                    // Populate the table of songs
                    for (int i = 0; i < albumList.size(); ++i) {

                        // Get the song data from the database and add it to the table list
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

                    // Set the list to the table
                    tblContext.setAudioTableList(audioTableList);

                }

                // Check to see if the clicked item is an ArtistTreeItem
                if (item.getClass().equals(ArtistTreeItem.class)) {

                    // Convert the TreeItem to an ArtistTreeItem and get all albums from artist
                    ArtistTreeItem artistItem = (ArtistTreeItem) item;
                    List<Integer> albumList = db.getSongsByArtist(artistItem.getId());

                    // Clear the table of songs
                    audioTableList.clear();

                    // Populate the table of songs
                    for (int i = 0; i < albumList.size(); ++i) {

                        // Get the song data from the database and add it to the table list
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

                    // Set the list to the table
                    tblContext.setAudioTableList(audioTableList);

                }

                // Check to see if the clicked item is an UnknownTreeItem
                if (item.getClass().equals(UnknownTreeItem.class)) {

                    // If it is a single click
                    if (mouseEvent.getClickCount() == 1) {

                        // Clear the table of songs
                        audioTableList.clear();

                        // Populate the table of songs
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

                        // Set the list to the table
                        tblContext.setAudioTableList(audioTableList);
                    }
                }
            }
        }

        // Scroll to the top of the table
        audioTable.scrollTo(0);
    }

    /**
     * Method that handles the mouse pressed functionality on the seekbar on the main NNMP GUI.
     *
     * @param mouseEvent - triggered MouseEvent
     */
    @FXML protected void handleSeekbarMousePressed(MouseEvent mouseEvent) {
        // If playback queue contains files
        if (!mediaPlayer.isPlaybackQueueEmpty()) {
            // Toggle seek boolean
            isSeeking = true;
        }
    }

    /**
     * Method that handles the mouse released functionality on the seekbar on the main NNMP GUI.
     *
     * @param mouseEvent - triggered MouseEvent
     */
    @FXML protected void handleSeekbarMouseReleased(MouseEvent mouseEvent) {
        // If playback queue contains files
        if (!mediaPlayer.isPlaybackQueueEmpty()) {
            // Store current seek position
            seekTime = (int) sldSeekBar.getValue();

            // Seek to desired position
            mediaPlayer.seek(seekTime);

            // Update seek slider to requested position
            sldSeekBar.setValue(seekTime);
            // Toggle seek boolean
            isSeeking = false;
        }
    }

    /**
     * Method than handles the functionality of the Open Playlist menu item.
     *
     * @param event - triggered ActionEvent
     */
    @FXML protected void handleOpenPlaylistMenuItemOnActionEvent(ActionEvent event) {
        // Instantiate M3UPlaylistController
        M3UPlaylistController playlistController = new M3UPlaylistController();

        // Open playlist and populate the playback queue
        playlistController.openPlaylist(mediaPlayer.getPlaybackQueue());
        playlistController = null;
    }

    /**
     * Method than handles the functionality of the Save Playlist menu item.
     *
     * @param event - triggered ActionEvent
     */
    @FXML protected void handleSavePlaylistMenuItemOnActionEvent(ActionEvent event) {
        // Instantiate M3UPlaylistController
        M3UPlaylistController playlistController = new M3UPlaylistController();

        // Save current files in playback queue to M3U playlist
        playlistController.savePlaylist(mediaPlayer.getPlaybackQueue());
        playlistController = null;
    }

    /**
     * Method than handles the functionality of the Import Library menu item.
     *
     * @param event - triggered ActionEvent
     */
    @FXML protected void handleImportLibraryMenuItemOnActionEvent(ActionEvent event) {
        // Open importation dialog box
        openImportDialog();
    }

    /**
     * Method than handles the functionality of the Rip CD menu item.
     *
     * @param event - triggered ActionEvent
     */
    @FXML protected void handleRipAudioCDItemOnActionEvent(ActionEvent event) {
        // Instantiate FXMLCDRipperController
        FXMLCDRipperController fxmlcdRipperController = new FXMLCDRipperController();

        // Show CD Ripper UI
        fxmlcdRipperController.showDialog();
    }

    /**
     * Method than handles the functionality of the Organize Library menu item.
     *
     * @param event - triggered ActionEvent
     */
    @FXML protected void handleOrganizeLibraryMenuItemOnActionEvent(ActionEvent event) {
        // Instantiate FXMLOrganizerController instance
        FXMLOrganizerController organizerController = new FXMLOrganizerController();

        // Show Organizer UI
        organizerController.showDialog();
    }

    /**
     * Method than handles the functionality of the Quit menu item.
     *
     * @param event - triggered ActionEvent
     */
    @FXML protected void handleQuitMenuItemOnActionEvent(ActionEvent event) {
        // Exit
        System.exit(0);
    }


    @FXML public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Method that retrieves audio file fields from the NNMP database. The audio files are
     * added to the Music Tree after they are collected.
     */
    private void getTreeItemsFromDB() {
        // Retrieve .db file
        File fileDatabase = new File(DBNAME);

        // If database exists
        if (fileDatabase.exists()) {
            // Collect all artist fields stored
            artistList = db.getAllArtists();
            // Collect all unknown files stored
            unknownList = db.getAllUnknown();

            // Add collected items to Music Tree
            addItemsToTree();
        }
    }

    /**
     * Method that pens a DirectoryChooser to allow the user to import the songs into the music player,
     * then adds the songs into the database.
     */
    private void openImportDialog() {

        // Create a DirectoryChooser
        DirectoryChooser dir = new DirectoryChooser();
        dir.setTitle("Import a Library...");

        // Get the path from the DirectoryChooser
        File path = dir.showDialog(null);

        // Create an Import Library Controller
        ImportLibraryController importLibraryController = new ImportLibraryController();

        // Import songs to database
        Platform.runLater(() -> {
            importLibraryController.importMusic(path, db);
            new Thread(() -> {

                // Make sure that the import process is completed
                while (importLibraryController.isProcessingComplete() == false) {

                }

                // Get all of the artists and unknown entries
                artistList = importLibraryController.getArtistList();
                unknownList = importLibraryController.getUnknownList();

                // Add the items to the tree
                Platform.runLater(() -> {addItemsToTree();});

            }).start();
        });
    }

    /**
     * Method that adds all of the imported items into the tree.
     */
    private void addItemsToTree() {

        // Create tree items for default tree view
        TreeItem<String> rootItem = new TreeItem("My Music", null);
        TreeItem<String> unknownItems = new TreeItem("(Unknown Entries)", null);
        UnknownTreeItem unknownAlbum = new UnknownTreeItem("(Unknown Album)");

        // Populate all albums from all artists into tree
        for (int i = 0; i < artistList.size(); ++i) {

            // Create an ArtistTreeItem and get all albums from artist
            ArtistTreeItem artists = new ArtistTreeItem(artistList.get(i));
            List<Integer> albumsByArtist = db.getAlbumsByArtist(artistList.get(i));

            // Create all AlbumTreeItems and add them to the artist
            for (int j = 0; j < albumsByArtist.size(); ++j) {
                AlbumTreeItem albums = new AlbumTreeItem(albumsByArtist.get(j));
                artists.getChildren().add(albums);
            }

            // Add the ArtistTreeItem to the tree
            rootItem.getChildren().add(artists);
        }

        // Add the unknown entries to tree
        unknownItems.getChildren().add(unknownAlbum);
        rootItem.getChildren().add(unknownItems);

        // Expand the and set the root
        rootItem.setExpanded(true);
        musicTree.setRoot(rootItem);
    }

    /**
     * Method that updates GUI components reliant on the currently queued audio file.
     * This portion of code is required for all cases of play functionality.
     */
    private void play() {

        // If playback queue contains files and playback is not currently active
       if (!mediaPlayer.isPlaybackQueueEmpty() && !mediaPlayer.isPlaying()) {
           // Update UI components
           loadMediaUIData();

           // If TableView cell selected
           if (audioTable.getSelectionModel().getSelectedCells().size() > 0) {
               // Set playback index of selected file
               mediaPlayer.setPlaybackIndex(audioTable.getSelectionModel().getSelectedIndex());
           }
           // Start playback
           mediaPlayer.startMediaPlayback();
       }
    }

    /**
     * Method that is responsible for activating the seek slider that tracks the
     * playback progress of a particular audio file. The seek slider will start at the
     * time it is passed (seconds).
     *
     * @param time - desired start of seek slider in seconds
     */
    private void startSeekSlider(int time) {

        // Get audio file duration
        final int dur = mediaPlayer.getActiveFile().getDuration();

        // Set lower bound to 0
        sldSeekBar.setMin(0);
        // Set upper bound to track duration
        sldSeekBar.setMax(dur);
        // Set starting slider to seek value
        sldSeekBar.setValue(seekTime);
        seekTime = time;

        // Instantiate new timer object
        timer = new Timer();

        // Schedules seek slider to update every second
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    if (!mediaPlayer.isPaused() && !sldSeekBar.isValueChanging() && !isSeeking) {
                        // Format time to String representation
                        formatTime(seekTime);
                        lblElapsedTime.setText(sb.toString());

                        // Reset StringBuilder
                        sb.setLength(0);
                        sldSeekBar.setValue(seekTime);
                        // Increment seek value
                        ++seekTime;
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * Method responsible for updating the Label objects with String representations of
     * an audio file's relevant metadata. An audio file's album art is also rendered.
     */
    private void loadMediaUIData() {
        // Set Label text for title, artist, album, duration
        lblSongPlaying.setText(mediaPlayer.getActiveFile().getTitle());
        lblArtistPlaying.setText(mediaPlayer.getActiveFile().getArtist());
        lblAlbumPlaying.setText(mediaPlayer.getActiveFile().getAlbum());
        formatTime(mediaPlayer.getActiveFile().getDuration());
        lblTotalDuration.setText(sb.toString());

        // Clear StringBuilder
        sb.setLength(0);

        // Render album art
        ivAlbumArtPlaying.setImage(mediaPlayer.getActiveFile().getAlbumArt());
    }

    /**
     * Method responsible for formatting a time value passed into a String representation
     * to be displayed on the NNMP main GUI.
     *
     * @param duration - time to be formatted
     */
    public void formatTime(int duration) {
        // Calculate hour, minute, seconds
        int hour = Math.floorDiv(duration, 3600);
        int min = (duration / 60) % 60;
        int sec = duration % 60;

        // If duration is one hour or longer
        if (hour > 0) {
            // Append needed colon
            sb.append(hour + ":");
        }

        // If minute value less than 10
        if (hour > 0 && min < 10) {
            // Append 0 to minute value
            sb.append("0" + min + ":");
        }
        else {
            // Simply append colon
            sb.append(min + ":");
        }

        // If second value less than 10
        if (sec < 10) {
            // Append needed 0
            sb.append("0");
        }
        sb.append(sec);
    }

    /**
     * Method initializes the table rows and handles mouse click events from the selected row
     */
    private void initializeTableView() {

        // Set the items to the table
        audioTable.setItems(audioTableList);

        audioTable.setRowFactory( tv -> {

            // Create a table row and set the context menu to it
            TableRow<nnmpprototype1.AudioFile> row = new TableRow<>();
            row.setContextMenu(tblContext);

            // Set the playback list context to the playback queue
            tblContext.setPlaybackList(mediaPlayer.getPlaybackQueue());

            // Set events for mouse click
            row.setOnMouseClicked(event -> {

                // Clear all selected values if clicked
                if (event.getClickCount() == 1) {
                    listView.getSelectionModel().clearSelection();
                }

                // Create context menu for selected item if right mouse click
                if (event.getClickCount() == 1 && event.getButton() == MouseButton.SECONDARY) {
                    tblContext.setAudioFile(row.getItem());
                }

                // If the row is double clicked
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {

                    // Set flag to false
                    playlistFlag = false;

                    // Check is media player is playing
                    if (mediaPlayer.isPlaying()) {
                        if (mediaPlayer.isPaused()) {
                            mediaPlayer.resumeMediaPlayback();
                        }
                        mediaPlayer.stopMediaPlayback();
                    }

                    // Clear the playback queue and queue the selected file
                    mediaPlayer.clearPlaybackQueue();
                    mediaPlayer.playFileAt(0);
                    nnmpprototype1.AudioFile rowData = row.getItem();
                    mediaPlayer.enqueueToPlaybackQueue(rowData);

                    // Play the selected item
                    play();
                }
            });

            // Return the row
            return row ;
        });
    }

    /**
     * Method responsible for setting the names of each column in the TableView.
     */
    private void setTableColNames() {
        // Set column names
        trackCol.setCellValueFactory(new PropertyValueFactory<>("track"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
    }

    /**
     * Method responsible for setting custom Button graphic images.
     */
    private void initializeButtonImages() {
        // Instantiate Images with descriptive Button graphics
        Image imagePause = new Image(getClass().getResourceAsStream("pause.png"));
        Image imagePlay = new Image(getClass().getResourceAsStream("play.png"));
        Image imageStop = new Image(getClass().getResourceAsStream("stop.png"));
        Image imageNext = new Image(getClass().getResourceAsStream("next.png"));
        Image imagePrev = new Image(getClass().getResourceAsStream("prev.png"));

        // Assign each graphic to its corresponding Button
        btnPause.setGraphic(new ImageView(imagePause));
        btnPlay.setGraphic(new ImageView(imagePlay));
        btnStop.setGraphic(new ImageView(imageStop));
        btnNext.setGraphic(new ImageView(imageNext));
        btnPrev.setGraphic(new ImageView(imagePrev));
    }

    /**
     * Method responsible for initializing the volume slider. The default volume level is
     * 50%.
     */
    private void initializeVolumeSlider() {
        // Set minimum, maximum and default volume levels
        sldVolume.setMax(6);
        sldVolume.setMin(-80);
        sldVolume.setValue(-37);

        // Set volume increment
        sldVolume.setBlockIncrement(.05f);

        // Add functionality to alter volume level
        sldVolume.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
            // Set updated volume level
            mediaPlayer.setVolumeLevel(newVal.floatValue());
        });
    }

    /**
     * The update method is required to be implemented as the class implements the observer interface.
     * This method handles the case where an audio file has finished playback and the file in the
     * queue starts playing. Various GUI components are required to be updated.
     */
    @Override
    public void update() {
        // If playback active
        if (mediaPlayer.isPlaying() && !mediaPlayer.isPaused()) {
            // If playback index is valid
            if (mediaPlayer.getActiveIndex() < mediaPlayer.getPlaybackQueue().getSize()) {
                Platform.runLater(() -> {
                    // Update UI components
                    loadMediaUIData();
                });

                if (!mediaPlayer.isPaused()) {
                        Platform.runLater(() -> {
                            if (timer != null) {
                                // Destroy timer
                                timer.cancel();
                                timer = null;
                            }
                            // If user adjusts seekbar before playback starts
                            if (mediaPlayer.isPrePlaySeek()) {
                                // Start seek slider at desired time
                                startSeekSlider(seekTime);
                            }
                            else {
                                // Refresh seek slider
                                startSeekSlider(0);
                            }
                        });
                }
                // Retrieve active index
                listView.getSelectionModel().select(mediaPlayer.getActiveIndex());
            }
            else {
                // Destroy timer
                timer.cancel();
                timer = null;

                if (!mediaPlayer.isPaused()) {

                    // Refresh seek slider
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
