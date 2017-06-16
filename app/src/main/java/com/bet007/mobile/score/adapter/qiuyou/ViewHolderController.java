package com.bet007.mobile.score.adapter.qiuyou;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.LinkedHashMap;


public class ViewHolderController {

    private static ViewHolderController mInstance;
    private HashMap<Integer, ImageView> mData = new LinkedHashMap<>();
    private int mLastPlayPosition = -1;

    private ViewHolderController() {

    }

    public static ViewHolderController getInstance() {
        if(mInstance == null){
            synchronized (ViewHolderController.class){
                if(mInstance == null){
                    mInstance = new ViewHolderController();
                }
            }
        }
        return mInstance;
    }

    public void addView(int position, ImageView view) {
        mData.put(position, view);
    }

    public int getLastPlayPosition() {
        return mLastPlayPosition;
    }

    public void setLastPlayPosition(int position) {
        mLastPlayPosition = position;
    }

    public void notifyAnimStop(int resId) {
        ImageView imageView = mData.get(mLastPlayPosition);
        try {
            if (imageView != null) {
                AnimationDrawable anim = (AnimationDrawable) imageView.getDrawable();
                anim.stop();
                imageView.setImageResource(resId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void remove(int position) {
        if (mData.size() > 0) {
            mData.remove(position);
        }
    }

    public void release() {
        mData.clear();
        mData = null;
    }

}
