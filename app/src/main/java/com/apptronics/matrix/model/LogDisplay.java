package com.apptronics.matrix.model;

/**
 * Created by Maha Perriyava on 5/7/2018.
 */

public class LogDisplay {

    public String timeStamp, timeWorked, workDescription, userName;

    public LogDisplay(String timeStamp,String timeWorked,String workDescription,String userName){
        this.timeStamp=timeStamp;
        this.timeWorked=timeWorked;
        this.workDescription=workDescription;
        this.userName=userName;
    }
}
