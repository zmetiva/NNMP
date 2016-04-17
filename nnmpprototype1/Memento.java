package nnmpprototype1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The Memento Class is used to store a state of the active PlaybackList object. The state stored consists
 * of AudioFiles that are present in the PlaybackList at the time the operation occurs. The state is stored as a
 * FXML ObservableList object.
 */
public class Memento {
    /** ObservableList used to store state information */
    private ObservableList<nnmpprototype1.AudioFile> state;

    /**
     * Argument Constructor
     *
     * @param state - ObservableList reference
     */
    public Memento(ObservableList<nnmpprototype1.AudioFile> state) {
        this.state = FXCollections.observableArrayList(state);
    }

    /**
     * Accessor method used to return the saved state.
     *
     * @return ObservableList - most recent saved state
     */
    public ObservableList<nnmpprototype1.AudioFile> getState() {
        return state;
    }
}
