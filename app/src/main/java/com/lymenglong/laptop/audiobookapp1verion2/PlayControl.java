package com.lymenglong.laptop.audiobookapp1verion2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lymenglong.laptop.audiobookapp1verion2.customize.CustomActionBar;
import com.lymenglong.laptop.audiobookapp1verion2.model.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PlayControl extends AppCompatActivity {


    private Button btnPlay, btnStop, btnPause, btnForward, btnBackward, btnNext, btnPrev, btnFavorite;
    private MediaPlayer mediaPlayer;
    private CustomActionBar actionBar;
    private Activity activity = PlayControl.this;
    private Thread seekBarThread;
    private SeekBar songProgressBar;
    private int lastPlayDuration = 0;


    private RequestQueue requestQueueHistory, requestQueueFavorite;
    private static final String historyURL = "http://20121969.tk/audiobook/mobile_registration/history.php";
    private static final String favoriteURL = "http://20121969.tk/audiobook/mobile_registration/favorite.php";
    private StringRequest requestHistory, requestFavorite;
    private Session session;


    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private int intCurrentPosition = 0;
    private int targetPossition;
    private int intSoundMax;
    private int seekForwardTime = 5000; //5 seconds
    private int seekBackwardTime = 5000; // 5 seconds
    private TextView songTotalDurationLabel;
    private TextView songCurrentDurationLabel;
    private String getFileUrlChapter,  getContentChapter, getTitleChapter;
    private int getIdChapter, getPauseTime;
    private ProgressDialog progressDialog;

    private boolean initialStage = true;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_control);
        initDataFromIntent();
        setTitle(getTitleChapter);
        initCheckBookUrl(); //finish activity when getFileUrlChapter is empty
        initToolbar();
        initView();
        initObject();
        initPrepareMedia();
        intListener();

    }



class Player extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... strings) {
        Boolean prepared = false;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(strings[0]);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    initialStage = true;
//                    mediaPlayer.stop();
//                    mediaPlayer.reset();
                }
            });
            mediaPlayer.prepare();
//            songProgressBar.setMax(intSoundMax);
            prepared = true;

        } catch (Exception e) {
            Log.e("MyAudioStreamingApp", e.getMessage());
            prepared = false;
        }

        return prepared;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (progressDialog.isShowing()) {
            progressDialog.cancel();
        }

        mediaPlayer.start();
        initialStage = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Buffering...");
        progressDialog.show();
    }
}

    private void postHistoryDataToServer() {

        requestHistory = new StringRequest(Request.Method.POST, historyURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getApplicationContext(),"Thành công, "+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Lỗi, " +jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Thêm vào lịch sử thất bại", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("IdBook", String.valueOf(getIdChapter));
                hashMap.put("IdUser", session.getUserIdLoggedIn());
                hashMap.put("InsertTime",String.valueOf(lastPlayDuration));
                hashMap.put("PauseTime ","3210");
                return hashMap;
            }
        };

        requestQueueHistory.add(requestHistory);
    }
    private void postFavoriteDataToServer() {

        requestFavorite = new StringRequest(Request.Method.POST, favoriteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getApplicationContext(),"Thành công, "+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Lỗi, " +jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("IdBook", String.valueOf(getIdChapter));
                hashMap.put("IdUser", session.getUserIdLoggedIn());
//                hashMap.put("InsertTime","1234");
//                hashMap.put("Status","3210");
                return hashMap;
            }
        };

        requestQueueFavorite.add(requestFavorite);
    }

    private void initCheckBookUrl() {
        if (getFileUrlChapter.isEmpty()) {
            Toast.makeText(activity, "Lỗi, Dữ liệu chưa được cấp nhật", Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

    private void initDataFromIntent() {
        getFileUrlChapter = getIntent().getStringExtra("fileUrl");
        getIdChapter = getIntent().getIntExtra("idChapter",-1);
        getTitleChapter = getIntent().getStringExtra("titleChapter");
        getContentChapter = getIntent().getStringExtra("content");
        getPauseTime = getIntent().getIntExtra("PauseTime", 0);
    }

    private void initPrepareMedia() {
        if (getFileUrlChapter != null) {
            /*mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(getFileUrlChapter);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        intSoundMax = mp.getDuration();
                    }
                });
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            new Player().execute(getFileUrlChapter);
        }
    }


    private void initObject() {
        requestQueueHistory = Volley.newRequestQueue(activity);
        requestQueueFavorite = Volley.newRequestQueue(activity);
        mediaPlayer= new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        session = new Session(activity);
        progressDialog = new ProgressDialog(activity);
    }

    //bat su kien khi kich button
    View.OnClickListener onClickListener  = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //region Switch Button
            switch (view.getId()){
                case R.id.btn_add_favorite:
                    postFavoriteDataToServer();
                    break;
                case R.id.btn_play :
                    playMedia();
                    break;
                case R.id.btn_pause:
                    pauseMedia();
                    break;
                case R.id.btn_stop:
                    stopMedia();
                    break;
                case R.id.btn_next:
                    nextMedia();
                    break;
                case R.id.btn_previous:
                    previousMedia();
                    break;
                case R.id.btn_ffw:
                    forwardMedia();
                    break;
                case R.id.btn_backward:
                    backwardMedia();
                    break;
            }
            //endregion
        }
    };

    private void stopMedia() { // nghe lại từ đầu
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
        mediaPlayer.start();

    }

    private void backwardMedia() {
        intCurrentPosition = mediaPlayer.getCurrentPosition();
        // check if seekBackward time is greater than 0 sec
        if(intCurrentPosition - seekBackwardTime >= 0){
            // forward song
            mediaPlayer.seekTo(intCurrentPosition - seekBackwardTime);
        }else{
            // backward to starting position
            mediaPlayer.seekTo(0);
        }
    }

    private void forwardMedia() {
        intCurrentPosition = mediaPlayer.getCurrentPosition();
        targetPossition = intCurrentPosition + seekForwardTime;
        if(targetPossition < intSoundMax){
            // forward song
            mediaPlayer.seekTo(targetPossition);
            Log.d("MyTagView", "forwardMedia: "+ targetPossition);
        }else{
            // forward to end position
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }

    private void previousMedia() {

    }

    private void intListener() {
        btnFavorite.setOnClickListener(onClickListener);
        btnPlay.setOnClickListener(onClickListener);
        btnPause.setOnClickListener(onClickListener);
        btnForward.setOnClickListener(onClickListener);
        btnBackward.setOnClickListener(onClickListener);
        btnNext.setOnClickListener(onClickListener);
        btnPrev.setOnClickListener(onClickListener);
        btnStop.setOnClickListener(onClickListener);
        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    mediaPlayer.seekTo(i);
                    seekBar.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        });
    }

    private void nextMedia() {

    }

    private void pauseMedia() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
        }
    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            if (mediaPlayer!=null) {
                mediaPlayer.start();
                mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 1000);
                if(mediaPlayer.getCurrentPosition()< getPauseTime){
                    mediaPlayer.seekTo(getPauseTime);
                }
//                Toast.makeText(activity, "Playback Started From Server",
                if(mediaPlayer.isPlaying()){
                    Toast.makeText(activity, "Đang chạy, vui lòng chờ!",
                            Toast.LENGTH_SHORT).show();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            Toast.makeText(activity, "Đã chạy xong", Toast.LENGTH_SHORT).show();
//                            lastPlayDuration = 0;
//                            postHistoryDataToServer();
                        }
                    });
                }
            }
        }
    }

    private Handler mSeekbarUpdateHandler = new Handler();
    private Runnable mUpdateSeekbar = new Runnable() {
        @Override
        public void run() {
            songProgressBar.setProgress(mediaPlayer.getCurrentPosition());
            mSeekbarUpdateHandler.postDelayed(this, 50);
        }
    };


    public void initProgressSeekBar(){
        seekBarThread = new Thread(new Runnable() {
            @Override
            public void run() {
                songProgressBar.setMax(intSoundMax);
//                Log.d("test","s"+SONG_NUMBER+"tt"+SOUND_TOTAL);
                while (mediaPlayer != null && intCurrentPosition < intSoundMax){
                    try {
                        Thread.sleep(1000);
                        intCurrentPosition = mediaPlayer.getCurrentPosition();
                    }
                    catch (InterruptedException e) {
                        return;

                    }
                    catch (Exception otherException){
                        return;
                    }
                    songProgressBar.setProgress(intCurrentPosition);
                }
            }
        });
        seekBarThread.start();
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */

    private void initToolbar() {
        actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, getTitleChapter, false);
    }

    private void initView() {
        btnFavorite = findViewById(R.id.btn_add_favorite);
        btnPlay = findViewById(R.id.btn_play);
        btnPause = findViewById(R.id.btn_pause);
        btnForward = findViewById(R.id.btn_ffw);
        btnBackward = findViewById(R.id.btn_backward);
        btnNext = findViewById(R.id.btn_next);
        btnPrev = findViewById(R.id.btn_previous);
        btnStop = findViewById(R.id.btn_stop);
        songProgressBar = findViewById(R.id.seekBar);
        songTotalDurationLabel = (TextView) findViewById(R.id.text_total_duration_label);
        songCurrentDurationLabel = (TextView) findViewById(R.id.text_current_duration_label);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer.getCurrentPosition()==mediaPlayer.getDuration()){
            lastPlayDuration = 0;
        }else {
            lastPlayDuration = mediaPlayer.getCurrentPosition();
        }
        if (!getFileUrlChapter.isEmpty()) {
            postHistoryDataToServer();
        }
        mediaPlayer.release();
    }
}
