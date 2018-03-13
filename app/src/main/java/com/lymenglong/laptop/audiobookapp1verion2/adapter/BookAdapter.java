package com.lymenglong.laptop.audiobookapp1verion2.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lymenglong.laptop.audiobookapp1verion2.PlayControl;
import com.lymenglong.laptop.audiobookapp1verion2.R;
import com.lymenglong.laptop.audiobookapp1verion2.ViewReading;
import com.lymenglong.laptop.audiobookapp1verion2.model.Book;

import java.util.ArrayList;


public class BookAdapter extends RecyclerView.Adapter {
    private ArrayList<Book> books;
    private Activity activity;
    private View view;
    private int getIdChapter;
    private String getTitleChapter, getContentChapter, getfileUrlChapter;

    public BookAdapter(Activity activity, ArrayList<Book> books) {
        this.books = books;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ChapterHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChapterHolder) {
            ChapterHolder chapterHolder = (ChapterHolder) holder;

            chapterHolder.name.setText(books.get(position).getTitle());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private ImageView imgNext;

        public ChapterHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.nameStory);
            imgNext = (ImageView) itemView.findViewById(R.id.imgNext);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView) {
                getIdChapter = books.get(getAdapterPosition()).getId();
                getTitleChapter = books.get(getAdapterPosition()).getTitle();
                getContentChapter = books.get(getAdapterPosition()).getContent();
                getfileUrlChapter = books.get(getAdapterPosition()).getFileUrl();
                showAlertDialog();
            }
        }
    }


    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle("Chọn Dạng Sách");
        builder.setMessage("Bạn muốn chọn dạng nào?");
        builder.setCancelable(false);
        builder.setPositiveButton("Văn bản", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(activity, "Dạng văn bản", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, ViewReading.class);
                intent.putExtra("idChapter", getIdChapter);
                intent.putExtra("titleChapter", getTitleChapter);
                intent.putExtra("content", getContentChapter);
                intent.putExtra("fileUrl", getfileUrlChapter);
                dialogInterface.dismiss();
                activity.startActivity(intent);

            }
        });
        builder.setNegativeButton("Ghi âm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(activity, "Dạng ghi âm", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, PlayControl.class);
                intent.putExtra("idChapter", getIdChapter);
                intent.putExtra("titleChapter", getTitleChapter);
                intent.putExtra("content", getContentChapter);
                intent.putExtra("fileUrl", getfileUrlChapter);
                dialogInterface.dismiss();
                activity.startActivity(intent);
            }
        });
        builder.setNeutralButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}
