package com.bet007.mobile.score.adapter.qiuyou;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;

import com.bet007.mobile.score.interfaces.ImageLoader;
import com.bet007.mobile.score.model.qiuyou.IChatMessage;
import com.lqr.ChatSceneActivity;

import java.lang.ref.WeakReference;

/**
 * 聊天消息基类ViewHolle
 */

public abstract class BaseChatMessageViewHolder<MESSAGE extends IChatMessage> extends BaseViewHolder<MESSAGE> {

    protected Context mContext;
    protected float mDensity;
    protected int mPosition;
    protected boolean mIsSelected;
    protected ImageLoader mImageLoader;
    protected ChatMessageAdapter.OnMsgLongClickListener<MESSAGE> mMsgLongClickListener;
    protected ChatMessageAdapter.OnMsgClickListener<MESSAGE> mMsgClickListener;
    protected ChatMessageAdapter.OnAvatarClickListener<MESSAGE> mAvatarClickListener;
    protected ChatMessageAdapter.OnMsgResendListener<MESSAGE> mMsgResendListener;
    protected Handler mWeakHandler;
    public BaseChatMessageViewHolder(View itemView) {
        super(itemView);
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
