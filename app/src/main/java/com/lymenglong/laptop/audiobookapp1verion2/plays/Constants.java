package com.lymenglong.laptop.audiobookapp1verion2.plays;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by LapTop on 8/4/2017.
 */

public class Constants {
    public static int SONG_NUMBER = 0; // set the first song to default play
    public static String SONG_RES_NAME; // set default song name
    public static Boolean isPAUSED = true; //set default state of Button Pause Or Play
    public static int STEP_VALUE = 5000; // 5 sec
    public static List<String> LIST_ITEM_NAME;
    public static String [] arrRES_NAME;
    public static Context CONTEXT;
    public static ImageButton BTN_PLAY, BTN_PAUSE , BTN_NEXT, BTN_PREV ;
    public static MediaPlayer CURRENT_PLAYER;
    public static int CURRENT_POSITION_PERIOD = 0;
    public static SeekBar SEEK_BAR;
    public static TextView TV_DURATION_MAX;
    public static TextView TV_DURATION_BUFFERING;
    public static TextView TV_NOW_PLAYING;
    public static int SOUND_TOTAL;
    public static int indexMENU, indexCATEGORY, indexHome ,counterFRAGMENT = 0;
    public static TextView tvTOOLBAR;
    public static ImageView ivTOOLBAR_BACK;
}
