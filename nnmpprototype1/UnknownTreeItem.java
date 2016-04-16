package nnmpprototype1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.scene.control.TreeItem;



/**
 *
 * @author zmmetiva
 */
public class UnknownTreeItem extends TreeItem<String> {
    private int id;

    UnknownTreeItem (String title) {
        super();


        this.setValue(title);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        NNMPDB db = NNMPDB.getInstance();

        return db.getUnknownTitle(this.id);
    }
}
