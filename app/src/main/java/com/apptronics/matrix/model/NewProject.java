package com.apptronics.matrix.model;

import java.util.ArrayList;

/**
 * Created by Maha Perriyava on 5/6/2018.
 */

public class NewProject {

    public String type;
    public ArrayList<String> UIDs;

    public NewProject(String type, ArrayList<String> uids){
        this.type=type;
        this.UIDs=uids;
    }
}
