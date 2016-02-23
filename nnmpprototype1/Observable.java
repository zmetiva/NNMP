package nnmpprototype1;

/**
 * Created by Tyler on 2/11/2016.
 */
public interface Observable {
    public void addObserver(Observer o);
    public void removeObserver(Observer o);
    public void notifyObserver();
}
