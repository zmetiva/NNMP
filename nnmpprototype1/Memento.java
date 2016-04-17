package nnmpprototype1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;

/**
 * Created by Tyler on 4/16/2016.
 */
public class Memento {
    private ObservableList<nnmpprototype1.AudioFile> state;

    public Memento(ObservableList<nnmpprototype1.AudioFile> state) {
        this.state = FXCollections.observableArrayList(state);
    }

    public ObservableList<nnmpprototype1.AudioFile> getState() {
        return state;
    }
}
