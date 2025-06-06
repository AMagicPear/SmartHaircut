package com.perry.smartposter.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.perry.smartposter.activity.PictureDetailActivity;
import com.perry.smartposter.model.DataElement;
import com.perry.smartposter.model.DataElementManager;
import com.perry.smartposter.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    /// 数据源，注意这是一个引用数据！
    private final ArrayList<DataElement> mDataList;

    public CustomAdapter(ArrayList<DataElement> myData) {
        mDataList = myData;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mImageView;
        private final TextView mTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.recyclerview_item_image);
            mTextView = itemView.findViewById(R.id.recyclerview_item_text);
            mImageView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), PictureDetailActivity.class);
                intent.putExtra("id", mDataList.get(getAdapterPosition()).id);
                v.getContext().startActivity(intent);
                Log.d("PERRY", "onClick: " + mDataList.get(getAdapterPosition()).id);
            });
        }
    }

    /// 创建新的ViewHolder实例
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载RecyclerView子项的布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new MyViewHolder(view);
    }

    /// 绑定数据到ViewHolder
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mImageView.setImageURI(Uri.parse(mDataList.get(position).mImagePath));
        holder.mTextView.setText(mDataList.get(position).mText);
        holder.mTextView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("删除图片")
                    .setMessage("确定删除吗？")
                    .setPositiveButton("对！", (dialog, which) -> {
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            DataElementManager.getInstance(v.getContext()).RemoveDataElementAt(pos);
                            notifyItemRemoved(pos);
                        }
                    }).setNegativeButton("手滑了", null);
            builder.create().show();
            return true;
        });
    }


    /// @return 返回数据项的数量
    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
