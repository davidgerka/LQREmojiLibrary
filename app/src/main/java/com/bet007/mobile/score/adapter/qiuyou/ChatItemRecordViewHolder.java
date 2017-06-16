package com.bet007.mobile.score.adapter.qiuyou;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bet007.mobile.score.model.qiuyou.IChatMessage;
import com.lqr.R;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 聊天列表中录音消息ViewHolder
 *
 * @param <MESSAGE>
 */
public class ChatItemRecordViewHolder<MESSAGE extends IChatMessage>
        extends BaseChatItemViewHolder<MESSAGE> {


    protected ImageView ivHorn;
    protected View layoutContent;
    protected TextView tvDuration;

    private boolean mSetData = false;
    private AnimationDrawable mVoiceAnimation;
    private FileInputStream mFIS;
    private FileDescriptor mFD;
    private boolean mIsEarPhoneOn;
    private int mSendDrawable;
    private int mReceiveDrawable;
    private int mPlaySendAnim;
    private int mPlayReceiveAnim;
    private ViewHolderController mController;

    public ChatItemRecordViewHolder(View itemView, boolean isSender) {
        super(itemView, isSender);
        ivHorn = getView(R.id.iv_horn);
        layoutContent = getView(R.id.content_layout_record);
        tvDuration = getView(R.id.tv_record_time);

        getView(R.id.tv_action).setVisibility(View.GONE);
        getView(R.id.content_layout_text).setVisibility(View.GONE);
        getView(R.id.content_layout_pic).setVisibility(View.GONE);

        initRes();
        mController = ViewHolderController.getInstance();
    }

    private void initRes() {
        mSendDrawable = R.drawable.chatscene_horn_to_2;
        mReceiveDrawable = R.drawable.chatscene_horn_from_2;
        mPlaySendAnim = R.drawable.chatscene_horn_to_sel;
        mPlayReceiveAnim = R.drawable.chatscene_horn_from_sel;
    }

    @Override
    public void onBind(final MESSAGE message) {
        super.onBind(message);

        if(message.isRead()){
            ivUnread.setVisibility(View.GONE);
        }else {
            ivUnread.setVisibility(View.VISIBLE);
        }

        long duration = message.getDuration();
        tvDuration.setText(message.getDuration() + "''");
        int width = (int) (-0.04 * duration * duration + 4.526 * duration + 75.214);
        layoutContent.getLayoutParams().width = (int) (width * mDensity);

        tvDuration.setVisibility(View.VISIBLE);
        if (mIsSender) {
            switch (message.getStateCode()) {
                case IChatMessage.STATE_CREATED:
                case IChatMessage.STATE_FAILED:
                case IChatMessage.STATE_GOING:
                    tvDuration.setVisibility(View.INVISIBLE);
                    break;
            }
        }

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

        layoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMsgClickListener != null) {
                    mMsgClickListener.onMessageClick(message);
                }

                // stop animation whatever this time is play or pause audio
//                if (mVoiceAnimation != null) {
//                    mVoiceAnimation.stop();
//                    mVoiceAnimation = null;
//                }
                if (mIsSender) {
                    mController.notifyAnimStop(mSendDrawable);
                    ivHorn.setImageResource(mPlaySendAnim);
                } else {
                    mController.notifyAnimStop(mReceiveDrawable);
                    ivHorn.setImageResource(mPlayReceiveAnim);
                }
                mVoiceAnimation = (AnimationDrawable) ivHorn.getDrawable();
                mController.addView(getAdapterPosition(), ivHorn);
                // If audio is playing, pause
                Log.e("VoiceViewHolder", "MediaPlayer playing " + mMediaPlayer.isPlaying() + "now position " + getAdapterPosition());
                if (mController.getLastPlayPosition() == getAdapterPosition()) {
                    if (mMediaPlayer.isPlaying()) {
                        pauseVoice();
                        mVoiceAnimation.stop();
                        if (mIsSender) {
                            ivHorn.setImageResource(mSendDrawable);
                        } else {
                            ivHorn.setImageResource(mReceiveDrawable);
                        }
                    } else if (mSetData) {
                        mMediaPlayer.start();
                        mVoiceAnimation.start();
                    } else {
                        playVoice(getAdapterPosition(), message);
                    }
                    // Start playing record
                } else {
                    playVoice(getAdapterPosition(), message);
                }
            }
        });

        layoutContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mMsgLongClickListener != null) {
                    mMsgLongClickListener.onMessageLongClick(message);
                } else {

                }
                return true;
            }
        });
    }

    private void playVoice(int position, MESSAGE message) {
        mController.setLastPlayPosition(position);
        try {
            mMediaPlayer.reset();
            mFIS = new FileInputStream(message.getContentText());
            mFD = mFIS.getFD();
            mMediaPlayer.setDataSource(mFD);
            if (mIsEarPhoneOn) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mVoiceAnimation.start();
                    mp.start();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mVoiceAnimation.stop();
                    mp.reset();
                    mSetData = false;
                    if (mIsSender) {
                        ivHorn.setImageResource(mSendDrawable);
                    } else {
                        ivHorn.setImageResource(mReceiveDrawable);
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(mContext, mContext.getString(R.string.play_record_fail),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (mFIS != null) {
                    mFIS.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void pauseVoice() {
        mMediaPlayer.pause();
        mSetData = true;
    }

    public void setAudioPlayByEarPhone(int state) {
        AudioManager audioManager = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        if (state == 0) {
            mIsEarPhoneOn = false;
            audioManager.setSpeakerphoneOn(true);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL);
        } else {
            mIsEarPhoneOn = true;
            audioManager.setSpeakerphoneOn(false);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                    AudioManager.STREAM_VOICE_CALL);
        }
    }

}