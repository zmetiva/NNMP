package nnmpprototype1;

/**
 * Media File - The base class of an audio file. Contains the basic information for a media file.
 */
public abstract class MediaFile {

    // The location of the media file
    protected String location = "";

    // The duration of the media file
    protected int duration = -1;

    /**
     * Default Constructor
     */
    public MediaFile() {
        
    }

    /**
     * Parameterized Constructor
     *
     * @param location the location of the media file.
     * @param duration the duration of the media file.
     */
    public MediaFile(String location, int duration) {
        this.location = location;
        this.duration = duration;
    }

    /**
     * Sets the location of the media file.
     *
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the duration of the media file.
     *
     * @param duration duration of the media file.
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Returns the location of the media file.
     *
     * @return the location of the media file.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the duration of the media file.
     *
     * @return the duration of the media file.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Overriden toString that displays dummy text.
     *
     * @return media file text.
     */
    @Override
    public String toString() {
        return "Media file";
    }
}
