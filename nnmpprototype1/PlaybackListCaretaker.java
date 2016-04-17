package nnmpprototype1;

/**
 * The PlaybackListCaretaker class is used to save and restore the contents of the PlaybackList
 * object it is passed. It is part of the Memento Design Pattern implemented in this project.
 */
public class PlaybackListCaretaker {

    /** Object reference*/
    private Object obj;

    /**
     * Default Constructor
     */
    public PlaybackListCaretaker() {

    }

    /**
     * Method used to save the contents of the PlaybackList that it is passed.
     *
     * @param list - PlaybackList reference
     */
    public void save(PlaybackList list) {
        this.obj = list.save();
    }

    /**
     * Method used to restore the PlaybackList to a previous, saved state.
     *
     * @param list - PlaybackList reference
     */
    public void undo(PlaybackList list) {
        list.undoLastSave(obj);
    }
}
