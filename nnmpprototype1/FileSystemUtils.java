/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

/**
 *
 * @author zmmetiva
 */
public class FileSystemUtils {

    final private String[] EXTENTIONS = {"mp3", "ogg", "aac", "mp4", "wav", "m4a"};
    private ExecutorService threads = Executors.newCachedThreadPool(); /*newFixedThreadPool(Runtime.getRuntime().availableProcessors());*/
    Collection<Callable<nnmpprototype1.AudioFile>> tasks = new ArrayList<Callable<nnmpprototype1.AudioFile>>();

    public Vector<Tag> tags = new Vector<>();
    public Vector<AudioFile> unknowns = new Vector<>();

    public Vector<String> paths = new Vector<>();
    public Vector<Integer> lengths = new Vector<>();
    public List<Integer> artistList;
    public List<Integer> unknownList;

    public FXMLImportProgressController prgDialog = new FXMLImportProgressController();
    private AtomicBoolean processingCompleted = new AtomicBoolean(false);
    /**
     * Populates all songs from the directory name into the database
     *
     * @param directoryName
     * @param ext
     * @param db
     */
    private void getFiles(String directoryName, String ext, NNMPDB db) {
        File directory = new File(directoryName);
        Vector<File> fileList = new Vector(Arrays.asList(directory.listFiles()));

       // Future fun = threads.submit(()-> {
            for (File file : fileList) {

                if (file.isFile() && file.getName().endsWith(ext)) {
                    try {

                        AudioFile f = AudioFileIO.read(file);
                        Tag tag = f.getTag();

                        if (tag != null) {
                            //int artist = db.addArtist(tag.getFirst(FieldKey.ARTIST));
                            // int album = db.addAlbum(tag.getFirst(FieldKey.ALBUM), artist);
                            if (tag.getFirst(FieldKey.ARTIST).equals("") || tag.getFirst(FieldKey.TITLE).equals("") || tag.getFirst(FieldKey.ARTIST).equals("") || tag.getFirst(FieldKey.TRACK).equals("")) {
                                unknowns.add(f);
                            }
                            else {
                                tags.add(tag);
                                paths.add(file.getAbsolutePath());
                                lengths.add(f.getAudioHeader().getTrackLength());

                                if (tag.getFirst(FieldKey.TRACK).equals("")) {
                                } else {
                                    //int song = db.addSong(tag.getFirst(FieldKey.TRACK), tag.getFirst(FieldKey.TITLE), f.getAudioHeader().getTrackLength(), file.getAbsolutePath(), album);
                                }
                                //Platform.runLater(() -> {prgDialog.setLabel("Reading: " + tag.getFirst(FieldKey.TITLE));});
                                prgDialog.setLabel("Reading: " + tag.getFirst(FieldKey.TITLE));
                                //prgDialog.setProgress((float)-1);
                            }
                        }

                        else {
                            unknowns.add(f);
                        }


                    } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException ex) {
                        //Logger.getLogger(SQLiteCreate.class.getName()).log(Level.SEVERE, null, ex);
                    }


                } else if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), ext, db);
                }

            }

    }
    private void addItemsToDB(NNMPDB db){

        System.out.println("HERE!!!");
        prgDialog.setProgress((float)0);

        for (int i = 0; i < tags.size(); ++i) {

            Tag tag = tags.get(i);

            prgDialog.setLabel("Adding: " + tag.getFirst(FieldKey.TITLE));
            prgDialog.setProgress((float)prgDialog.getProgress() + (float)1/tags.size());
            System.out.println((float)prgDialog.getProgress() + (float)1/tags.size());

            int artist = db.addArtist(tags.get(i).getFirst(FieldKey.ARTIST));
            int album = db.addAlbum(tags.get(i).getFirst(FieldKey.ALBUM), artist);
            if (tags.get(i).getFirst(FieldKey.TRACK).equals("")) {
            } else {
                int song = db.addSong(tags.get(i).getFirst(FieldKey.TRACK), tags.get(i).getFirst(FieldKey.TITLE), tags.get(i).getFirst((FieldKey.YEAR)), lengths.get(i), paths.get(i), album);
            }

        }

        prgDialog.setProgress((float)0);

        for (int i = 0; i < unknowns.size(); i++) {
            AudioFile file = unknowns.get(i);

            prgDialog.setLabel("Adding: " + file.getFile().getName());
            prgDialog.setProgress((float)prgDialog.getProgress() + (float)1/unknowns.size());

            int unknownEntry = db.addUnknown(file.getFile().getName(), file.getFile().getAbsolutePath(), file.getAudioHeader().getTrackLength());

        }

        threads.shutdownNow();

        tags.clear();
        lengths.clear();
        paths.clear();

        artistList = db.getAllArtists();
        unknownList = db.getAllUnknown();

        System.out.println("DONE!");
        processingCompleted.set(true);


    }
    /**
     *
     * @param path
     * @param db
     * @return
     */
    public void populateMusicMetadata(String path, NNMPDB db) {
        prgDialog.showDialog();
        db.create();

        /*Thread t = new Thread (() -> {
            for (int i = 0; i < EXTENTIONS.length; ++i) {
                getFiles(path, EXTENTIONS[i], db);
            }
        });*/

        /*try {

            /*t.start();

            t.sleep(1000);

            while(t.isAlive()) {

            }*/

            new Thread(() -> {
                prgDialog.setProgress(-1);

                for (int i = 0; i < EXTENTIONS.length; ++i) {
                    getFiles(path, EXTENTIONS[i], db);
                }

                new Thread(() -> {
                    prgDialog.setProgress(0);
                }).start();

                //prgDialog.setProgress((float)0);
                addItemsToDB(db);
                //artistList = db.getAllArtists();
                prgDialog.closeDialog();
            }).start();


       /* } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

    }

    public boolean isComplete() {
        return processingCompleted.get();
    }

    public List<Integer> getArtistList() {
        return artistList;
    }
    public List<Integer> getUnknownList() {
        return unknownList;
    }
}