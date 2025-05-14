package com.perry.smartposter;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.File;
import java.util.ArrayList;

public class Pictures extends AppCompatActivity {

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

        // 设置返回按钮的点击事件监听器
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); // 直接关闭当前活动，返回到上一个活动（MainActivity）
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        CustomAdapter customAdapter = new CustomAdapter(getMyData());
        recyclerView.setAdapter(customAdapter);
    }

    private ArrayList<ImageElement> getMyData() {
        ArrayList<ImageElement> dataList = new ArrayList<>();
        File picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (picturesDir != null && picturesDir.exists()) {
            File[] files = picturesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".jpg")) {
                        dataList.add(new ImageElement(0, file.getAbsolutePath(), file.getName()));
                    }
                }
            }
        } else {
            Toast.makeText(this, "No pictures directory found", Toast.LENGTH_SHORT).show();
        }
        return dataList;
    }
}