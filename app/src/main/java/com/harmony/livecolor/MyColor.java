package com.harmony.livecolor;

public class MyColor {

    private String id, name, hex, rgb, hsv;

    public MyColor(String id, String name, String hex, String rgb, String hsv){
        this.id = id;
        this.name = name;
        this.hex = hex;
        this.rgb = rgb;
        this.hsv = hsv;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHex() {
        return hex;
    }

    public String getHsv() {
        return hsv;
    }

    public String getRgb() {
        return rgb;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

    public void setHsv(String hsv) {
        this.hsv = hsv;
    }
}
