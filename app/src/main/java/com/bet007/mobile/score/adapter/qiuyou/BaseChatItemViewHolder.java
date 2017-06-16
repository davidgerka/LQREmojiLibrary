package com.bet007.mobile.score.adapter.qiuyou;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bet007.mobile.score.model.qiuyou.IChatMessage;
import com.github.jdsjlzx.progressindicator.AVLoadingIndicatorView;
import com.lqr.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 聊天列表中Item的公共VIew的ViewHolder
 *
 * @param <MESSAGE>
 */
public class BaseChatItemViewHolder<MESSAGE extends IChatMessage>
        extends BaseChatMessageViewHolder<MESSAGE> {

    protected TextView tvTime;
    protected CircleImageView civAvatar;
    protected TextView tvMaster;
    protected TextView tvName;
    protected ImageView ivUnread;
    protected ImageView ivFail;
    protected AVLoadingIndicatorView progressBar;

    protected boolean mIsSender;

    public BaseChatItemViewHolder(View itemView, boolean isSender) {
        super(itemView);
        this.mIsSender = isSender;
        tvTime = getView(R.id.tv_time);
        civAvatar = getView(R.id.civ_avatar);
        tvMaster = getView(R.id.tv_master);
        tvName = getView(R.id.tv_name);
        ivUnread = getView(R.id.iv_unread);
        ivFail = getView(R.id.iv_fail);
        progressBar = getView(R.id.progressbar);

    }

    @Override
    public void onBind(final MESSAGE message) {
        if (mImageLoader != null) {
            mImageLoader.loadAvatarImage(civAvatar, message.getGroupAvatar());
        } else if (mImageLoader == null) {
//                    civAvatar.setVisibility(View.GONE);
        }
        tvTime.setText("" + message.getCreateTime());
        tvMaster.setVisibility(View.VISIBLE);
        tvName.setText(message.getSenderName());

        ivUnread.setVisibility(View.GONE);
        ivFail.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);


        if (mIsSender) {
            switch (message.getStateCode()) {
                case IChatMessage.STATE_CREATED:
                case IChatMessage.STATE_SUCCEED:
                    break;
                case IChatMessage.STATE_FAILED:
                    ivFail.setVisibility(View.VISIBLE);
                    ivFail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mMsgResendListener != null) {
                                mMsgResendListener.onMessageResend(message);
                            }
                        }
                    });
                    break;
                case IChatMessage.STATE_GOING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            ivUnread.setVisibility(View.VISIBLE);
        }

        civAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAvatarClickListener != null) {
                    mAvatarClickListener.onAvatarClick(message);
                }
            }
        });

//
//        mMsgTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mMsgClickListener != null) {
//                    mMsgClickListener.onMessageClick(message);
//                }
//            }
//        });
//
//        mMsgTv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if (mMsgLongClickListener != null) {
//                    mMsgLongClickListener.onMessageLongClick(message);
//                } else {
//                    if (BuildConfig.DEBUG) {
//                        Log.w("MsgListAdapter", "Didn't set long click listener! Drop event.");
//                    }
//                }
//                return true;
//            }
//        });
//

    }


    public CircleImageView getAvatar() {
        return civAvatar;
    }

}