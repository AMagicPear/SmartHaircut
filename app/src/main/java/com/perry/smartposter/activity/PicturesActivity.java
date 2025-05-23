package com.perry.smartposter.activity;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.perry.smartposter.adapter.CustomAdapter;
import com.perry.smartposter.model.DataElementManager;
import com.perry.smartposter.R;

public class PicturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pictures);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pictures_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取 MaterialToolbar 实例
        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        // 为返回按钮设置点击事件
        topAppBar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        // 使用DataElementManager获取数据列表
        CustomAdapter customAdapter = new CustomAdapter(DataElementManager.getInstance(getApplicationContext()).getDataList());
        recyclerView.setAdapter(customAdapter);
    }
}