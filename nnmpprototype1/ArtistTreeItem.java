package nnmpprototype1;

import javafx.scene.control.TreeItem;

/**
 * Artist Tree Item - Contains the artist information for the tree items in the main GUI. Allows for each album sub-tree
 * to be populated.
 */
public class ArtistTreeItem extends TreeItem<String> {

    // The id of the artist item
    private int id;

    /**
     * Parameterized Constructor
     *
     * @param id the id of the artist
     */
    ArtistTreeItem (int id) {
        super();
        this.id = id;
        
        this.setValue(getTitle());
        
    }

    /**
     * Returns the artist tree item id.
     *
     * @return the artist tree item title
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the artist tree item title.
     *
     * @return the artist tree item title
     */
    public String getTitle() {
        NNMPDB db = NNMPDB.getInstance();
        
        return db.getArtistName(this.id);
    }
}
