package com.perry.smartposter.model;

import android.content.Context;
import android.util.Log;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DataElementManager {
    private static final String RECORD_FILE_NAME = "data_elements.toml";
    private final File recordFile;
    private final ArrayList<DataElement> dataList;

    /// 单例实例
    private static DataElementManager instance;

    private DataElementManager(Context context) {
        this.recordFile = new File(context.getExternalFilesDir(null), RECORD_FILE_NAME);
        dataList = loadDataElements();
        Log.d("Perry", "数据文件位于：" + recordFile.getAbsolutePath());
        for(var data: dataList){
            Log.d("Perry", "当前数据：" + data.id + "\n" + data.mImagePath + "\n" + data.mText);
        }
    }

    /**
     * 获取单例实例
     */
    public static synchronized DataElementManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataElementManager(context.getApplicationContext());
        }
        return instance;
    }

    public void AddDataElement(DataElement dataElement) {
        for (var item : dataList) {
            if(item.id == dataElement.id){
                Log.d("Perry", "已存在相同ID的数据，已忽略");
                return;
            }
        }
        dataList.add(dataElement);
        saveDataElements();
        Log.d("Perry", "已添加数据并保存" + dataElement.id);
    }

    public void RemoveDataElementAt(int index) {
        dataList.remove(index);
        saveDataElements();
    }

    public void RemoveDataElementOfId(long id) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).id == id) {
                dataList.remove(i);
                Log.d("Perry", "已删除数据" + id);
                break;
            }
        }
        saveDataElements();
    }

    /**
     * 将 List<DataElement> 写入 TOML 文件
     */
    private void saveDataElements() {
        TomlWriter writer = new TomlWriter();
        try (FileWriter fileWriter = new FileWriter(recordFile)) {
            fileWriter.write(writer.write(CollectionsWrapper.wrap(dataList)));
            Log.d("Perry", "数据已保存到" + recordFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从 TOML 文件中读取 List<DataElement>
     */
    private ArrayList<DataElement> loadDataElements() {
        if (!recordFile.exists()) return new ArrayList<>();

        try (FileReader reader = new FileReader(recordFile)) {
            Toml toml = new Toml().read(reader);
            CollectionsWrapper wrapper = toml.to(CollectionsWrapper.class);
            return wrapper.mData != null ? wrapper.mData : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<DataElement> getDataList() {
        return dataList;
    }

    /**
     * 用于包裹 List<DataElement>，以便正确映射到 [[mData]] 结构
     */
    private static class CollectionsWrapper {
        public ArrayList<DataElement> mData;

        public static CollectionsWrapper wrap(ArrayList<DataElement> list) {
            CollectionsWrapper wrapper = new CollectionsWrapper();
            wrapper.mData = list;
            return wrapper;
        }
    }
}
