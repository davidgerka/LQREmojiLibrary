package com.bet007.mobile.score.interfaces;

import android.view.View;

/**
 * Created by Allen
 * Created on 2017/4/11.
 */

public interface ViewClick {

    public static final String TAG_ITEM = "tagItem";
    public static final String TAG_BTN_AGREE = "tagBtnAgree";       //同意
    public static final String TAG_BTN_DELETE = "tagBtnDelete";     //删除
    public static final String TAG_BTN_INVITE = "tagBtnInvite";     //邀请
    public static final String TAG_BTN_SET_TOP = "tagBtnSetTop";     //置顶
    public static final String TAG_BTN_CANCEL_TOP = "tagBtnCancelTop";     //取消置顶
    public static final String TAG_BTN_ADJUST = "tagBtnAdjust";     //调整
    public static final String TAG_BTN_REMOVE = "tagBtnRemove";     //移出
    public static final String TAG_BTN_CANCEL = "tagBtnCancel";     //取消
    public static final String TAG_BTN_GOTOPERSONALHOME = "tagBtnGotoPersonalhome";     //个人主页

    void ItemClick(Object item, String clickTag, String paras, View view);
}
