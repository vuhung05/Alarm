package com.example.vuhung.video10minutes.Model;

import java.util.ArrayList;

public class Route {
    private int Id;
    private String name;
    private ArrayList<Child> listChildren;
    private int icon;
    private  long time;
    private long timeCurrent;

    public long getTimeCurrent() {
        return timeCurrent;
    }

    public void setTimeCurrent(long timeCurrent) {
        this.timeCurrent = timeCurrent;
    }

    public Route() {
    }
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Route(int id, String name, ArrayList<Child> listChildren, int icon,long timeCurrent, long time) {
        Id = id;
        this.name = name;
        this.listChildren = listChildren;
        this.icon = icon;
        this.time = time;
        this.timeCurrent = timeCurrent;
    }

    public Route(String name, ArrayList<Child> listChildren, int icon, long timeCurrent, long time) {
        this.name = name;
        this.listChildren = listChildren;
        this.icon = icon;
        this.time = time;
        this.timeCurrent = timeCurrent;
    }

    public  String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Child> getListChildren() {
        return listChildren;
    }

    public void setListChildren(ArrayList<Child> listChildren) {
        this.listChildren = listChildren;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
