package com.harmony.livecolor;

import java.util.ArrayList;

public class MyPalette {
    private String id, name;
    private ArrayList<MyColor> colors;

    public MyPalette(String id, String name, ArrayList<MyColor> colors) {
        this.id = id;
        this.name = name;
        this.colors = colors;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<MyColor> getColors() {
        return colors;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColors(ArrayList<MyColor> colors) {
        this.colors = colors;
    }

    public boolean deleteColor() {
        return false;
    }
}
