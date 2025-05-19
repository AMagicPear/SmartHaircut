package com.perry.smartposter.model;

public class DataElement {
    public long id;
    public String mImagePath;
    public String mText;
    public DataElement(long id, String imagePath, String text) {
        this.id = id;
        this.mImagePath = imagePath;
        this.mText = text;
    }
}
