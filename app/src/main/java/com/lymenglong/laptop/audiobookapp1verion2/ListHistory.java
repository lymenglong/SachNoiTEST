package com.lymenglong.laptop.audiobookapp1verion2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import com.lymenglong.laptop.audiobookapp1verion2.adapter.HistoryAdapter;
import com.lymenglong.laptop.audiobookapp1verion2.customize.CustomActionBar;
import com.lymenglong.laptop.audiobookapp1verion2.model.Chapter;


public class ListHistory extends AppCompatActivity{
    private Activity activity = ListHistory.this;
    private RecyclerView listChapter;
    private HistoryAdapter adapter;
    private Chapter chapterModel;
    private CustomActionBar actionBar;
    private String titleChapter;
    private int idChapter;
    private static final String URL = "http://20121969.tk/audiobook/books/getAllBooks.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_favorite);
        getDataFromIntent();
        init();
    }

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        titleChapter = getIntent().getStringExtra("titleChapter");
        idChapter = getIntent().getIntExtra("idChapter", -1);
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void init() {
        actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, titleChapter, false);
        listChapter = findViewById(R.id.list_small_chapter);
    }

}
