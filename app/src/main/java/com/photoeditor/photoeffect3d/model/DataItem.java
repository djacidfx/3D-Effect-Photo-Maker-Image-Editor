package com.photoeditor.photoeffect3d.model;

import android.graphics.Bitmap;

public class DataItem {
    private Bitmap bitmap;
    private String genre;
    private String title;
    Warna warna1;
    Warna warna2;
    private String year;

    public Warna getWarna1() {
        return this.warna1;
    }

    public void setWarna1(Warna warna1) {
        this.warna1 = warna1;
    }

    public Warna getWarna2() {
        return this.warna2;
    }

    public void setWarna2(Warna warna2) {
        this.warna2 = warna2;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public DataItem(){

    }
    public DataItem(String title, String genre, String year) {
        this.title = title;
        this.genre = genre;
        this.year = year;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return this.genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
