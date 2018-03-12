package com.lymenglong.laptop.audiobookapp1verion2.customize;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.lymenglong.laptop.audiobookapp1verion2.R;


public class CustomActionBar implements View.OnClickListener{
    private View imBack, imSearch;
    private TextView tvToolbar;
    private Activity activity;

    /**
     * Custom actionbar cho các activity với title và right btn
     * @param activity activity cần dùng bar
     * @param text title header
     * @param hasSearch true nếu muốn có thêm btn search
     */
    public void eventToolbar(Activity activity, String text, boolean hasSearch) {
        this.activity = activity;
        imBack = activity.findViewById(R.id.imBack);
        imSearch = activity.findViewById(R.id.imSearch);
        tvToolbar = (TextView) activity.findViewById(R.id.tvToolbar);

        tvToolbar.setText(text);
        if(hasSearch) {
            imSearch.setVisibility(View.VISIBLE);
        }else {
            imSearch.setVisibility(View.GONE);
        }
        imBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == imBack) {
            activity.onBackPressed();
        }
    }
}
