/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zmmetiva
 */
public class NNMPDB {
    private  static NNMPDB dbInstance = new NNMPDB();
    private Connection c = null;
    private Statement st = null;

    private NNMPDB() {

    }

    public static NNMPDB getInstance() {
        return dbInstance;
    }

    public void create() {
        try {
            Statement stmt = null;
            Class.forName("org.sqlite.JDBC");

            c = DriverManager.getConnection("jdbc:sqlite:nnmpdb.db");
            System.out.println("Opened database successfully");
            stmt = c.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS album (" +
                    "  album_id integer primary key NOT NULL," +
                    "  album_name text NOT NULL," +
                    "  artist_id integer NOT NULL," +
                    "  FOREIGN KEY (artist_id) REFERENCES artist (artist_id)" +
                    ");" +
                    "CREATE TABLE IF NOT EXISTS artist (" +
                    "  artist_id integer primary key NOT NULL," +
                    "  artist_name text NOT NULL" +
                    ");" +
                    "CREATE TABLE IF NOT EXISTS song (" +
                    "  song_id integer primary key NOT NULL," +
                    "  song_title text NOT NULL," +
                    "  track integer NOT NULL," +
                    "  year text, " +
                    "  duration integer NOT NULL," +
                    "  location text NOT NULL," +
                    "  album_id integer NOT NULL," +
                    "  FOREIGN KEY (album_id) REFERENCES album (album_id)" +
                    ");" +
                    "CREATE TABLE IF NOT EXISTS unknown(" +
                    "  unknown_id integer primary key NOT NULL," +
                    "  unknown_title text NOT NULL," +
                    "  duration integer NOT NULL," +
                    "  location text NOT NULL" +
                    ");";

            stmt.executeUpdate(sql);
            stmt.close();

            c.close();

        } catch ( Exception e ) {
            System.err.println( ": " + e.getClass().getName() + e.getMessage() );
            System.exit(0);
        }
    }

    public int addSong(String track, String title, String year, int duration, String location, int albumId) {

        boolean exists = this.songExists(title, albumId);
        int index = -1;

        if (!exists) {
            String sql = "INSERT INTO song (song_id, song_title, track, year, duration, location, album_id) VALUES (NULL, '" + title.replaceAll("'", "''") + "', " + Integer.parseInt(track, 10) + ", '" + year.replaceAll("'", "''") + "', " + duration + ", '" + location.replaceAll("'", "''") + "', " + albumId + ");";
            runQuery(sql);

            sql = "SELECT MAX(song_id) FROM song;";

            ResultSet rs = this.runResultQuery(sql);

            try {
                index = rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
            }

            clean();


        } else {
            String sql = "SELECT song_id FROM song WHERE song_title = '" + title.replaceAll("'", "''") + "' AND album_id = " + albumId + ";";

            ResultSet rs = this.runResultQuery(sql);

            try {
                index = rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
            }

            clean();

        }


        return index;
    }

    public int addAlbum(String albumName, int artistId) {

        boolean exists = this.albumExists(albumName, artistId);
        int index = -1;

        if (!exists) {
            String sql = "INSERT INTO album (album_id, album_name, artist_id) VALUES (NULL, '" + albumName.replaceAll("'", "''") + "', " + artistId + ");";
            runQuery(sql);

            sql = "SELECT MAX(album_id) FROM album;";

            ResultSet rs = this.runResultQuery(sql);

            try {
                index = rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
            }

            clean();

        } else {
            String sql = "SELECT album_id FROM album WHERE album_name = '" + albumName.replaceAll("'", "''") + "' AND artist_id = " + artistId + ";";

            ResultSet rs = this.runResultQuery(sql);

            try {
                index = rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
            }

            clean();

        }


        return index;
    }

    public int addArtist(String name) {

        int index = this.artistExists(name);

        if ( index == 0) {

            String sql = "INSERT INTO artist (artist_id, artist_name) VALUES (NULL, '" + name.replaceAll("'", "''") + "');";
            runQuery(sql);

            sql = "SELECT MAX(artist_id) FROM artist;";

            ResultSet rs = this.runResultQuery(sql);

            try {
                index = rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
            }

            clean();

        } else {
            String sql = "SELECT artist_id FROM artist WHERE artist.artist_name = '" + name.replaceAll("'", "''") + "';";
            ResultSet rs = this.runResultQuery(sql);

            try {
                index = rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
            }

            clean();
        }


        return index;
    }

    public int addUnknown(String title, String location, int duration) {

        boolean exists = this.unknownExists(location);
        int index = 0;

        if (!exists) {

            String sql = "INSERT INTO unknown (unknown_id, unknown_title, location, duration) VALUES (NULL, '" + title.replaceAll("'", "''") + "', '" + location.replaceAll("'", "''") + "', " + duration + ");";
            runQuery(sql);

            sql = "SELECT MAX(unknown_id) FROM unknown;";

            ResultSet rs = this.runResultQuery(sql);

            try {
                index = rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
            }

            clean();

        }
        return index;
    }

    public boolean deleteSong(int id) {
        String sql = "DELETE FROM song WHERE song_id = " + id;

        runQuery(sql);

        return true;

    }

    public int artistExists(String name) {

        String sql = "SELECT EXISTS(SELECT artist_name FROM artist WHERE artist_name = '" + name.replaceAll("'", "''") + "')";

        int exists = -1;

        ResultSet rs = this.runResultQuery(sql);

        try {
            exists = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return exists;
    }

    public boolean albumExists(String name, int artistId) {
        String sql = "SELECT EXISTS(SELECT album_name FROM album WHERE album_name = '" + name.replaceAll("'", "''") + "' AND artist_id = '" + artistId + "')";

        int exists = 0;

        ResultSet rs = this.runResultQuery(sql);

        try {
            exists = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return exists == 1;
    }

    public boolean songExists(String name, int albumId) {
        String sql = "SELECT EXISTS(SELECT song_title FROM song WHERE song_title = '" + name.replaceAll("'", "''") + "' AND album_id = " + albumId + ")";

        int exists = 0;

        ResultSet rs = this.runResultQuery(sql);

        try {
            exists = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return exists == 1;
    }

    public boolean unknownExists(String location) {
        String sql = "SELECT EXISTS(SELECT location FROM unknown WHERE location = '" + location.replaceAll("'", "''") + "')";

        int exists = 0;

        ResultSet rs = this.runResultQuery(sql);

        try {
            exists = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return exists == 1;
    }

    public List<Integer> getAllSongs() {
        String sql = "SELECT song_id FROM song";
        ArrayList<Integer> songs = new ArrayList<>();
        ResultSet rs = this.runResultQuery(sql);

        try {
            while (rs.next()) {
                songs.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return songs;

    }

    public String getSongTitle(int id) {
        String sql = "SELECT song_title FROM song WHERE song_id = " + id;
        String loc = null;

        ResultSet rs = this.runResultQuery(sql);

        try {
            loc = rs.getString(1);
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return loc;

    }

    public String getAlbumTitle(int id) {
        String sql = "SELECT album_name FROM album WHERE album_id = " + id;
        String loc = null;

        ResultSet rs = this.runResultQuery(sql);

        try {
            loc = rs.getString(1);
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return loc;
    }

    public String getArtistName(int id) {
        String sql = "SELECT artist_name FROM artist WHERE artist_id = " + id;
        String loc = null;

        ResultSet rs = this.runResultQuery(sql);

        try {
            loc = rs.getString(1);
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return loc;

    }

    public String getSongLocation(int id) {
        String sql = "SELECT location FROM song WHERE song_id = " + id;
        String loc = null;

        ResultSet rs = this.runResultQuery(sql);

        try {
            loc = rs.getString(1);
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return loc;
    }

    public String getUnknownTitle(int id) {
        String sql = "SELECT unknown_title FROM unknown WHERE unknown_id = " + id;
        String loc = null;

        ResultSet rs = this.runResultQuery(sql);

        try {
            loc = rs.getString(1);
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return loc;
    }

    public int getNumberOfEntries() {
        ResultSet rs = null;
        int count = -1;

        try {

            String sql = "SELECT COUNT(song_id) FROM song";
            rs = runResultQuery(sql);

            count = rs.getInt(1);

            clean();

        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }

    private void runQuery(String sql) {
        Statement stmt = null;

        try {
            c = DriverManager.getConnection("jdbc:sqlite:nnmpdb.db");

            stmt = c.createStatement();
            stmt.executeUpdate(sql);
            c.close();

        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ResultSet runResultQuery(String sql) {
        ResultSet rs = null;

        try {
            c = DriverManager.getConnection("jdbc:sqlite:nnmpdb.db");
            st = c.createStatement();
            rs = st.executeQuery(sql);

        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rs;
    }

    private void clean() {
        try {
            st.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Integer> getAllAlbums() {
        String sql = "SELECT album_id FROM album";
        ArrayList<Integer> ids = new ArrayList<>();
        ResultSet rs = this.runResultQuery(sql);

        try {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return ids;

    }

    public List<Integer> getAllArtists() {
        String sql = "SELECT artist_id FROM artist ORDER BY artist_name ASC";
        List<Integer> ids = new ArrayList<>();
        ResultSet rs = this.runResultQuery(sql);

        try {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return ids;
    }

    public List<Integer> getAllUnknown() {
        String sql = "SELECT unknown_id FROM unknown ORDER BY unknown_title ASC";
        List<Integer> ids = new ArrayList<>();
        ResultSet rs = this.runResultQuery(sql);

        try {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        return ids;
    }

    public ArrayList<Integer> getSongsByAlbum(int albumId) {
        ArrayList<Integer> ids = new ArrayList<>();

        String sql = "SELECT song_id FROM song WHERE album_id = " + albumId + " ORDER BY track ASC";

        ResultSet rs = this.runResultQuery(sql);

        try {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();


        return ids;

    }

    public ArrayList<Integer> getSongsByArtist(int artistId) {
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<Integer> artists = new ArrayList<>();

        String sql = "SELECT album_id FROM album WHERE artist_id = " + artistId;

        ResultSet rs = this.runResultQuery(sql);

        try {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();

        for (int i  = 0; i < ids.size(); ++i) {
            artists.addAll(this.getSongsByAlbum(ids.get(i)));
        }

        return artists;
    }

    public ArrayList<Integer> getAlbumsByArtist(int artistId) {
        ArrayList<Integer> ids = new ArrayList<>();

        String sql = "SELECT album_id FROM album WHERE artist_id = " + artistId;

        ResultSet rs = this.runResultQuery(sql);

        try {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();


        return ids;
    }

    public List<String> getSongData(int songId) {

        ArrayList<String> info = new ArrayList<>();
        int count = 1;

        String sql = "SELECT s.location, s.duration, art.artist_name, alb.album_name, s.song_title, s.track, s.year FROM song s, album alb, artist art WHERE s.song_id = " + songId + " AND s.album_id = alb.album_id AND art.artist_id = alb.artist_id";

        ResultSet rs = this.runResultQuery(sql);

        try {
            for (int i = 1; i < 8; ++i) {
                info.add(rs.getString(i));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();


        return info;

    }

    public List<String> getUnknownData(int unknownId) {
        ArrayList<String> info = new ArrayList<>();

        String sql = "SELECT unknown_title, location, duration FROM unknown WHERE unknown_id = " + unknownId;

        ResultSet rs = this.runResultQuery(sql);

        try {
            for (int i = 1; i < 4; ++i) {
                info.add(rs.getString(i));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NNMPDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        clean();


        return info;
    }


    public void updateSong(int songId, String track, String title, String album, String artist, String year) {

        int artId = addArtist(artist);
        int albId = addAlbum(album, artId);

        String sql = "UPDATE song SET song_title = '" + title.replaceAll("'", "''") + "',  track = " + track.replaceAll("'", "''") + ", year = '" + year.replaceAll("'", "''") + "', album_id = " + albId + " WHERE song_id = " + songId;

        System.out.print(artId + "\n" + albId);

        this.runQuery(sql);

    }


}