package com.bitsescrow.app.bitsescrow.models;

/**
 * Created by Lekan Adigun on 3/26/2018.
 */

public class Slide {

    private int icon = 0;
    private String title = "";
    private String subTitle = "";

    public Slide() {}

    public Slide(int icon, String title, String subTitle) {
        this.icon = icon;
        this.title = title;
        this.subTitle = subTitle;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
