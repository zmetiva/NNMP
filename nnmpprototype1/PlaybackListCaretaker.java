package nnmpprototype1;

/**
 * Created by Tyler on 4/16/2016.
 */
public class PlaybackListCaretaker {

    private Object obj;

    public PlaybackListCaretaker() {

    }

    public void save(PlaybackList list) {
        this.obj = list.save();
    }

    public void undo(PlaybackList list) {
        list.undoLastSave(obj);
    }
}
