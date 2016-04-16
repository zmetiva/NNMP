/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

/**
 *
 * @author zmmetiva
 */
public abstract class MediaFile {
    protected String location = "";
    protected int duration = -1;

    public MediaFile() {
        
    }
    
    public MediaFile(String location, int duration) {
        this.location = location;
        this.duration = duration;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public String getLocation() {
        return location;
    }
    
    public int getDuration() {
        return duration;
    }
    
    @Override
    public String toString() {
        return "Media file";
    }
}
