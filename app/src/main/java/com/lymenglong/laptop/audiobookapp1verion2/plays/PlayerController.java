package com.lymenglong.laptop.audiobookapp1verion2.plays;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import static com.lymenglong.laptop.audiobookapp1verion2.plays.Constants.*;

public final class PlayerController {
    private static int idCurrentPlayer = 0;
    private static Thread seekBarThread;

    //todo: prevController
/*    public static void prevController() {

        SONG_NUMBER--;

            if (CURRENT_PLAYER !=null && SONG_NUMBER > -1) {
                idCurrentPlayer= SONG_NUMBER;
                SONG_RES_NAME = String.valueOf(arrSongNameResource[idCurrentPlayer]);
                stopController();
                CURRENT_PLAYER = getMediaPlayer(SONG_RES_NAME);
                CURRENT_PLAYER.start();
                isPAUSED = false;
                TV_NOW_PLAYING.setText(""+ LIST_ITEM_NAME.get(idCurrentPlayer));
                updateButtonControls(BTN_PLAY, BTN_PAUSE,BTN_NEXT,BTN_PREV, SEEK_BAR);
                TTS(""+ listBookName.get(idCurrentPlayer));
            } else {
                SONG_NUMBER++;
                Toast.makeText(CONTEXT, "No Media Source", Toast.LENGTH_SHORT).show();
            }
    }*/


    //todo: nextController
/*    public static void nextController() {
        SONG_NUMBER++;
        if (CURRENT_PLAYER!=null && SONG_NUMBER < arrSongNameResource.length) {
            idCurrentPlayer= SONG_NUMBER;
            SONG_RES_NAME = String.valueOf(arrSongNameResource[idCurrentPlayer]);
            stopController();
            CURRENT_PLAYER = getMediaPlayer(SONG_RES_NAME);
            CURRENT_PLAYER.start();
            isPAUSED = false;
            TV_NOW_PLAYING.setText(""+ listBookName.get(idCurrentPlayer));
            updateButtonControls(BTN_PLAY, BTN_PAUSE,BTN_NEXT,BTN_PREV, SEEK_BAR);
            TTS(""+ listBookName.get(idCurrentPlayer));
        } else {
            SONG_NUMBER--;
            Toast.makeText(CONTEXT, "No Media Source", Toast.LENGTH_SHORT).show();
        }

    }*/

    //todo: pauseController DONE for test
    public static void pauseController() {
        isPAUSED = true;
        updateButtonControls(BTN_PLAY, BTN_PAUSE,BTN_NEXT,BTN_PREV, SEEK_BAR);
        CURRENT_PLAYER.pause();
    }

    //todo: playController DONE for test
    public static void playController() {
        try {
            if (!idPlayerChanged()&& CURRENT_PLAYER !=null){
                SOUND_TOTAL = CURRENT_PLAYER.getDuration();
                CURRENT_PLAYER.start();
//                setupThreadSeekBar();
            } else{
                idCurrentPlayer = SONG_NUMBER;
                CURRENT_PLAYER = getMediaPlayer(SONG_RES_NAME);
                SOUND_TOTAL = CURRENT_PLAYER.getDuration();
                CURRENT_PLAYER.start();
                TV_NOW_PLAYING.setText(""+ LIST_ITEM_NAME.get(SONG_NUMBER));
//                setupThreadSeekBar();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        isPAUSED = false;
        updateButtonControls(BTN_PLAY, BTN_PAUSE,BTN_NEXT,BTN_PREV, SEEK_BAR);
    }

    //todo: rewController
    public static void rewController() {
        int currentSongPosition = CURRENT_PLAYER.getCurrentPosition();
        int targetSongPosition = currentSongPosition - STEP_VALUE;
        if(CURRENT_PLAYER !=null && targetSongPosition < CURRENT_PLAYER.getDuration()){
            CURRENT_PLAYER.seekTo(targetSongPosition);
        } else {
            Toast.makeText(CONTEXT, "No Media Source", Toast.LENGTH_SHORT).show();
        }

    }

    //todo: ffwController
    public static void ffwController() {
        int currentSongPosition = CURRENT_PLAYER.getCurrentPosition();
        int targetSongPosition = currentSongPosition + STEP_VALUE;
        if(CURRENT_PLAYER !=null && targetSongPosition > 0 ){
            CURRENT_PLAYER.seekTo(targetSongPosition);
        } else {
            Toast.makeText(CONTEXT, "No Media Source", Toast.LENGTH_SHORT).show();
        }

    }

    public static void stopController(){
        try {
            if (CURRENT_PLAYER.isPlaying()){
                CURRENT_PLAYER.stop();
                CURRENT_PLAYER.release();
                CURRENT_PLAYER = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void updateButtonControls(ImageButton btnPlay, ImageButton btnPause,
                                            ImageButton btnNext, ImageButton btnPrev, SeekBar seekBar) {
        if (isPAUSED){
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.GONE);
        }else{
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        }
        if (CURRENT_PLAYER != null) {
            seekBar.setEnabled(true);
            btnPlay.setEnabled(true);
            btnNext.setEnabled(true);
            btnPrev.setEnabled(true);
        } else {
            seekBar.setEnabled(false);
            btnPlay.setEnabled(false);
            btnNext.setEnabled(false);
            btnPrev.setEnabled(false);
        }
    }


    public static String getConvertedDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if (hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }

    public static void setupThreadSeekBar(){
        seekBarThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SEEK_BAR.setMax(SOUND_TOTAL);
                TV_DURATION_MAX.setText(""+getConvertedDuration(SOUND_TOTAL));
                Log.d("test","s"+SONG_NUMBER+"tt"+SOUND_TOTAL);
                while (CURRENT_PLAYER != null && CURRENT_POSITION_PERIOD < SOUND_TOTAL){
                    try {
                        Thread.sleep(300);
                        CURRENT_POSITION_PERIOD = CURRENT_PLAYER.getCurrentPosition();
                        TV_DURATION_BUFFERING.setText(""+getConvertedDuration(CURRENT_POSITION_PERIOD));
                    }
                    catch (InterruptedException e) {
                        return;

                    }
                    catch (Exception otherException){
                        return;
                    }
                    SEEK_BAR.setProgress(CURRENT_POSITION_PERIOD);
                }
            }
        });
        seekBarThread.start();
    }


    private static Boolean idPlayerChanged(){
        if (idCurrentPlayer == SONG_NUMBER){
            return false;
        } else return true;
    }


    //todo: songResName resource DONE
    private static MediaPlayer getMediaPlayer(String songResName){
        int soundId = CONTEXT.getResources().getIdentifier(songResName,"raw",CONTEXT.getPackageName());
        MediaPlayer mediaPlayer = MediaPlayer.create(CONTEXT,soundId);
        return mediaPlayer;
    }

    private static MediaPlayer getMediaFromServer (){
        MediaPlayer player = new MediaPlayer();
        try{

//        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDataSource("http://xty/MRESC/images/test/xy.mp3");
        player.prepare();
        player.start();
        } catch (Exception e) {
        // TODO: handle exception
        }
        return player;
    }
}

