
package nnmpprototype1;

import javafx.scene.control.TreeItem;

/**
 * Album Tree Item - Contains the album information for the tree items in the main GUI. Allows for each song from the
 * album to be populated in the table.
 */
public class AlbumTreeItem extends TreeItem<String> {

    // The album tree item id
    private int id;

    /**
     * Parameterized Constructor
     *
     * @param id the id of the album
     */
    AlbumTreeItem (int id) {
        super();
        this.id = id;
        
        this.setValue(this.getTitle());
    }

    /**
     * Returns the album tree item id.
     *
     * @return the album tree item id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the album tree item title.
     *
     * @return the album tree item title
     */
    public String getTitle() {
        NNMPDB db = NNMPDB.getInstance();
        
        return db.getAlbumTitle(this.id);
    }
}
