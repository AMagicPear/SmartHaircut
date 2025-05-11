package com.perry.smartposter;

public class DataElement {
    public long id;
    public String mImagePath;
    public String mText;
    DataElement(long id, String imagePath, String text) {
        this.id = id;
        this.mImagePath = imagePath;
        this.mText = text;
    }
}
