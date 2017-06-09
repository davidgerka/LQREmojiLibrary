package com.win.emoji.emotionkeyboardview;

import android.view.MotionEvent;
import android.view.View;

/**
 * 聊天界面相关的接口
 * Created by 1 on 2016/11/10.
 */
public interface EmotionKeyboardListener {
    void onShowInputMethod();   //显示软键盘
    void onHideInputMethod();
    void onShowEmotionKeyboard();
    void onShowRecordLayout();  //显示录音布局
    void onSendTextMsg(String content);
    void onListViewSmooth();    //滚动listview
    void onTouchRecordEvent(View v, MotionEvent event, View recordLayout);

    }
