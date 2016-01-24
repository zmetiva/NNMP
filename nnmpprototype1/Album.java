/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import javafx.scene.control.TreeItem;

/**
 *
 * @author zmmetiva
 */
public class Album extends TreeItem<String> {
    private int id;
    
    Album (int id) {
        super();
        this.id = id;
        
        this.setValue(this.getTitle());
    }
    
    public int getId() {
        return id;
    }
    
    public String getTitle() {
        NNMPDB db = new NNMPDB();
        
        return db.getAlbumTitle(this.id);
    }
}
