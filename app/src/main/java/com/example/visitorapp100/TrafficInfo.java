package com.example.visitorapp100;

import android.text.PrecomputedText;

public class TrafficInfo {

    String lineName;
    String section;
    String status;
    String cause;

    public TrafficInfo(String lineName,String section,String status,String cause){
        this.lineName = lineName;
        this.section = section;
        this.status = status;
        this.cause = cause;
    }

    public String getLineName(){
        return lineName;
    }

    public String getSection(){
        return  section;
    }

    public String getStatus(){
        return status;
    }

    public String getCause(){
        return cause;
    }

    public String toString(){
        return section+":"+cause;
    }
}
