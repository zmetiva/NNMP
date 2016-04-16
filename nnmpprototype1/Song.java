package nnmpprototype1;

import javafx.scene.control.TreeItem;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zmmetiva
 */
public class Song extends TreeItem<String> {
    private int id;
    
    Song (int id) {
        super();
        this.id = id;
        
        this.setValue(getTitle());
    }
    
    public int getId() {
        return id;
    }
    
    public String getTitle() {
        NNMPDB db = NNMPDB.getInstance();
        
        return db.getSongTitle(this.id);
    }
    
    public String getLocation() {
        NNMPDB db = NNMPDB.getInstance();
        
        return db.getSongLocation(this.id);
    }
}
