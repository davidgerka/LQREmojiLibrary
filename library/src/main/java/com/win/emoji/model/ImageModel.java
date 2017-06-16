package com.win.emoji.model;

import android.graphics.drawable.Drawable;

/**
 * Created by lqn on 17/6/8.
 */

public class ImageModel {

    public String flag=null;//说明文本
    public Drawable icon=null;//图标
    public boolean isSelected=false;//是否被选中
    public int state;   //状态，使用场景：如果是键盘按钮，0：显示喇叭图片；1：显示键盘图片

    public static final int STATE_ZERO = 0;
    public static final int STATE_ONE = 1;

    public ImageModel(){

    }

    public ImageModel(String flag, Drawable icon, boolean isSelected, int state){
        this.flag = flag;
        this.icon = icon;
        this.isSelected = isSelected;
        this.state = state;
    }

}

