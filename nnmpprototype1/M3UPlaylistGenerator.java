package nnmpprototype1;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * M3U Playlist Generator - Generates and reads an M3U playlist file.
 */
public class M3UPlaylistGenerator {

    /** List of items in the playlist **/
    private List<nnmpprototype1.AudioFile> items = new ArrayList<>();

    /** Tag for extended M3U **/
    private boolean isExtendedM3U = false;

    /**
     * Default Constructor
     */
    public M3UPlaylistGenerator() {

    }

    /**
     * Parameterized Constructor - Sets all of the items to the generator.
     *
     * @param items the items to be added
     */
    public M3UPlaylistGenerator(List<nnmpprototype1.AudioFile> items) {
        for (int i = 0; i < items.size(); i++) {
            this.items.add(items.get(i));
        }
    }

    /**
     * Method that appends an item to the list.
     *
     * @param item appended item
     */
    public void add(nnmpprototype1.AudioFile item) {
        this.items.add(item);
    }

    /**
     * Method that adds an item to a certain index in the list.
     *
     * @param index index of the placed file
     * @param item the item that needs to be added
     */
    public void add(int index, nnmpprototype1.AudioFile item) {
        this.items.add(index, item);
    }

    /**
     * Method that returns an item from a certain index in the list.
     *
     * @param index the index of the desired item
     * @return the item desired
     */
    public nnmpprototype1.AudioFile get(int index) { return items.get(index); }

    /**
     * Method that returns the size of the list.
     *
     * @return the size of the playlist
     */
    public int size() { return items.size(); }

    /**
     * Method that generates a M3U playlist from the list of items.
     *
     * @param absolutePath the path of the playlist
     */
    public void generate(String absolutePath) {
        try {

            // Create a PrintWriter to create the playlist
            PrintWriter writer = new PrintWriter(absolutePath, "UTF-8");
            StringBuilder line = new StringBuilder();

            // Start the playlist generation
            writer.print("#EXTM3U\n");

            // Add all items to the playlist file
            for (int i = 0; i < items.size(); i++) {
                line.append("#EXTINF:" + String.valueOf(items.get(i).getDuration()) + "," + items.get(i).getArtist() + " - " + items.get(i).getTitle() + "\n" +
                        items.get(i).getLocation() + "\n");
                writer.print(line.toString());
                line.delete(0, line.length());
            }

            // Close the writer
            writer.close();

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that reads and processes a M3U playlist file.
     *
     * @param absolutePath the path of the playlist file
     * @param append bool to determine if current list of items should be appended
     */
    public void read(String absolutePath, boolean append) {

            // If the append if false, clear the list
            if(!append) {
                items.clear();
            }

            try {
                // Create a file and a scanner to read
                File file = new File(absolutePath);
                Scanner scanner = new Scanner(file, "UTF-8");

                // Create a string builder for each line in file
                StringBuilder line = new StringBuilder();

                // Read in all of the lines in the playlist file
                while(scanner.hasNext()) {

                    // Get the line
                    line.append(scanner.nextLine());

                    // Set extended tag
                    if (line.toString().contains("#EXTM3U")) {
                        isExtendedM3U = true;
                    }

                    // If tag is a song
                    if (line.toString().contains("#EXTINF")) {

                        // Read the song
                        line.delete(0, line.length());
                        line.append(scanner.nextLine());

                        try {

                            // Create a tagger audio file from the location of the song
                            AudioFile audioFile = AudioFileIO.read(new File(line.toString()));
                            Tag tag = audioFile.getTag();

                            // Add the item to the list
                            items.add(new nnmpprototype1.AudioFile(line.toString(), audioFile.getAudioHeader().getTrackLength(),tag.getFirst(FieldKey.ARTIST), tag.getFirst(FieldKey.ALBUM), tag.getFirst(FieldKey.TITLE), tag.getFirst(FieldKey.TRACK), tag.getFirst(FieldKey.YEAR), -1));

                        } catch (CannotReadException | IOException | ReadOnlyFileException | TagException | InvalidAudioFrameException e) {
                            e.printStackTrace();
                        }

                    }

                    // Clear the line
                    line.delete(0, line.length());
                }

                // Close the scanner
                scanner.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
}
