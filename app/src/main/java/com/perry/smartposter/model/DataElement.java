package com.perry.smartposter.model;

public class DataElement {
    public long id;
    public String mImagePath;
    public String mText;
    public int hairCutId;
    public DataElement(long id, String imagePath, String text, int hairCutId) {
        this.id = id;
        this.mImagePath = imagePath;
        this.mText = text;
        this.hairCutId = hairCutId;
    }
}
