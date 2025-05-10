package com.perry.smartposter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private final ArrayList<DataElement> mDataList;
    public CustomAdapter(ArrayList<DataElement> myData) {
        mDataList = myData;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private final ImageView mImageView;
        private final TextView mTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.recyclerview_item_image);
            mTextView = itemView.findViewById(R.id.recyclerview_item_text);
        }
    }

    /// 创建新的ViewHolder实例
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    /// 绑定数据到ViewHolder
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    ///@return 返回数据项的数量
    @Override
    public int getItemCount() {
        return 0;
    }
}
