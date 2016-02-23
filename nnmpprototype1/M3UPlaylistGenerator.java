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
 * Created by Zach on 1/29/2016.
 */
public class M3UPlaylistGenerator {
    private List<nnmpprototype1.AudioFile> items = new ArrayList<>();
    private boolean isExtendedM3U = false;

    M3UPlaylistGenerator() {

    }

    M3UPlaylistGenerator(List<nnmpprototype1.AudioFile> items) {
        for (int i = 0; i < items.size(); i++) {
            this.items.add(items.get(i));
        }
    }

    public void add(nnmpprototype1.AudioFile item) {
        this.items.add(item);
    }

    public void add(int index, nnmpprototype1.AudioFile item) {
        this.items.add(index, item);
    }

    public nnmpprototype1.AudioFile get(int index) { return items.get(index); }

    public int size() { return items.size(); }

    public void generate(String absolutePath) {
        try {
            PrintWriter writer = new PrintWriter(absolutePath, "UTF-8");
            StringBuilder line = new StringBuilder();
            writer.print("#EXTM3U\n");
            for (int i = 0; i < items.size(); i++) {
                line.append("#EXTINF:" + String.valueOf(items.get(i).getDuration()) + "," + items.get(i).getArtist() + " - " + items.get(i).getTitle() + "\n" +
                        items.get(i).getLocation() + "\n");
                writer.print(line.toString());
                line.delete(0, line.length());
            }

            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void read(String absolutePath, boolean append) {
            if(!append) {
                items.clear();
            }

            try {
                File file = new File(absolutePath);
                Scanner scanner = new Scanner(file, "UTF-8");

                StringBuilder line = new StringBuilder();

                while(scanner.hasNext()) {
                    line.append(scanner.nextLine());
                    //System.out.println(line.toString());
                    if (line.toString().contains("#EXTM3U")) {
                        isExtendedM3U = true;
                    }
                    if (line.toString().contains("#EXTINF")) {
                        line.delete(0, line.length());

                        line.append(scanner.nextLine());
                        //System.out.println(line.toString());
                        try {
                            AudioFile audioFile = AudioFileIO.read(new File(line.toString()));
                            Tag tag = audioFile.getTag();

                            items.add(new nnmpprototype1.AudioFile(line.toString(), audioFile.getAudioHeader().getTrackLength(),tag.getFirst(FieldKey.ARTIST), tag.getFirst(FieldKey.ALBUM), tag.getFirst(FieldKey.TITLE), tag.getFirst(FieldKey.TRACK), tag.getFirst(FieldKey.YEAR), -1));

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

                    }
                    line.delete(0, line.length());
                }
                scanner.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //System.out.print("FFD");
            }

        }
}
