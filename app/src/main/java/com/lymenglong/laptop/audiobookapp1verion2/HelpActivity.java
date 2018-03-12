package com.lymenglong.laptop.audiobookapp1verion2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.lymenglong.laptop.audiobookapp1verion2.adapter.HomeAdapter;
import com.lymenglong.laptop.audiobookapp1verion2.customize.CustomActionBar;
import com.lymenglong.laptop.audiobookapp1verion2.databases.DatabaseHelper;
import com.lymenglong.laptop.audiobookapp1verion2.model.Chapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class HelpActivity extends AppCompatActivity {

    private TextView tvReadFile;
    private RecyclerView listChapter;
    private ArrayList<Chapter> chapters;
    private HomeAdapter adapter;
    private CustomActionBar actionBar;
    private DatabaseHelper databaseHelper;
    private String titleHome;
    private int idHome;
    private TextView tvStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initView();
        getDataFromIntent();
        initObject();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        titleHome = getIntent().getStringExtra("titleHome");
        idHome = getIntent().getIntExtra("idHome", -1);
    }


    private void initObject() {
        //region Read File Text
        String text = "";
        try {
            InputStream inputStream = getAssets().open("help.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            text = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tvReadFile.setText(text);
        //endregion

    }


    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        actionBar = new CustomActionBar();
//        actionBar.eventToolbar(this, titleHome, true);
        actionBar.eventToolbar(this, "Hướng Dẫn", false );
        listChapter = (RecyclerView) findViewById(R.id.list_chapter);
        tvReadFile = (TextView) findViewById(R.id.tv_read_file);
    }
}
