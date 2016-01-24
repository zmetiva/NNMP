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
public class Artist extends TreeItem<String> {
    private int id;
    
    Artist (int id) {
        super();
        this.id = id;
        
        this.setValue(getTitle());
        
    }
    
    public int getId() {
        return id;
    }
    
    public String getTitle() {
        NNMPDB db = new NNMPDB();
        
        return db.getArtistName(this.id);
    }
}
