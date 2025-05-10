package com.perry.smartposter;

public class DataElement {
    public long id;
    public int mImageResource;
    public String mText;
    DataElement(long id,int imageResource, String text){
        this.id = id;
        this.mImageResource = imageResource;
        mText = text;
    }
}
