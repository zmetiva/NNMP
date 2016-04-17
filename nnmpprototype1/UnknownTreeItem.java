package nnmpprototype1;

import javafx.scene.control.TreeItem;

/**
 * Unknown Tree Item - Contains the artist information for the tree items in the main GUI. Allows for each album sub-tree
 * to be populated.
 */
public class UnknownTreeItem extends TreeItem<String> {

    // The id of the unknown entries
    private int id;

    /**
     * Parameterized Constructor
     *
     * @param title the title of the
     */
    UnknownTreeItem (String title) {
        super();
        this.setValue(title);
    }

    /**
     * Returns the id of the unknown item
     *
     * @return the id of the unknown item
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the title of the unknown item
     *
     * @return the title of the unknown item
     */
    public String getTitle() {
        NNMPDB db = NNMPDB.getInstance();

        return db.getUnknownTitle(this.id);
    }
}
