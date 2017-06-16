package com.bet007.library.chatsceneinputlibrary.listener;

import android.view.MotionEvent;
import android.view.View;


/**
 * 聊天界面相关的接口
 */
public interface IEmotionKeyboardListener extends IEmotionSelectedListener {
    void onShowInputMethod();   //显示软键盘

    void onHideInputMethod();

    void onShowEmotionKeyboard();

    void onShowRecordLayout();  //显示录音布局

    void onSendTextMsg(String content);

    void onListViewSmooth();    //滚动listview

    void onTouchRecordEvent(View v, MotionEvent event, View recordLayout);



}
