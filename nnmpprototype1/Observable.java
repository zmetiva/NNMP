package nnmpprototype1;

/**
 * The Observable interface is used in the implementation of the Observer Pattern.
 * The FXMLDocumentController class acts as an observer of the observable MediaPlayback Class.
 */
public interface Observable {
    public void addObserver(Observer o);
    public void removeObserver(Observer o);
    public void notifyObserver();
}
