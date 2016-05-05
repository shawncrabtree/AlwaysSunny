package com.sun.alwayssunny.Activity;

/**
 * Created by daniel on 5/4/16.
 * Needed to deal with dumb listview stuff in relative view formatting
 */
public class Loc {
    private String locName;
    private int position;

    public Loc (String locName){
        super();
        this.locName = locName;
    }

    public String getLocName(){
        return locName;
    }
}
