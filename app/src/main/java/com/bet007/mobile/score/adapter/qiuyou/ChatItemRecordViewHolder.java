package com.bet007.mobile.score.adapter.qiuyou;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bet007.mobile.score.common.SoundRecorder;
import com.bet007.mobile.score.common.ToastUtil;
import com.bet007.mobile.score.model.qiuyou.IChatMessage;
import com.lqr.R;

import java.io.File;
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
    private static final String TAG = ChatItemRecordViewHolder.class.getSimpleName();

    protected ImageView ivHorn;
    protected View layoutContent;
    protected TextView tvDuration;

    private boolean mIsEarPhoneOn;
    private RecordHolderController mRecordHolderController;


    public ChatItemRecordViewHolder(View itemView, boolean isSender) {
        super(itemView, isSender);
        ivHorn = getView(R.id.iv_horn);
        layoutContent = getView(R.id.content_layout_record);
        tvDuration = getView(R.id.tv_record_time);

        getView(R.id.tv_action).setVisibility(View.GONE);
        getView(R.id.content_layout_text).setVisibility(View.GONE);
        getView(R.id.content_layout_pic).setVisibility(View.GONE);

        mRecordHolderController = RecordHolderController.getInstance(itemView.getContext());

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
        layoutContent.requestLayout();//重新调整宽度

        tvDuration.setVisibility(View.VISIBLE);
        if (mIsSender) {
            switch (message.getStateCode()) {
                case IChatMessage.STATE_CREATED:

                    break;
                case IChatMessage.STATE_FAILED:

                    break;
                case IChatMessage.STATE_GOING:
//                    tvDuration.setVisibility(View.INVISIBLE);
                    break;
            }
        }

        setHornView(message);

        layoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMsgClickListener != null) {
                    mMsgClickListener.onMessageClick(message);
                }

                if(mRecordHolderController.mPlayMsgId == message.getMessageId()){   //点击了正在播放的item，直接停止播放
                    mRecordHolderController.stopAndResetHolder();
                    return;
                }
                if(mRecordHolderController.mPlayMsgId != RecordHolderController.DEFAULT_PLAYING_MSG_ID){//点击了其他item，并且有item正在播放
                    mRecordHolderController.stopAndResetHolder();
                }
                downloadVoiceAndPlay(message, message.isRead(), true);

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

    public void setHornView(MESSAGE message){
        if(mRecordHolderController.mPlayMsgId != RecordHolderController.DEFAULT_PLAYING_MSG_ID
                && mRecordHolderController.mPlayMsgId == message.getMessageId()){
            if(mIsSender){
                ivHorn.setImageResource(mRecordHolderController.mPlaySendAnim);
            }else {
                ivHorn.setImageResource(mRecordHolderController.mPlayReceiveAnim);
            }
            mRecordHolderController.mLastIsSend = mIsSender;
            AnimationDrawable animation = (AnimationDrawable) ivHorn.getDrawable();
            animation.start();
            mRecordHolderController.mHornView = ivHorn;
            mRecordHolderController.mHornAnimation = animation;
        }else {
            if(mIsSender){
                ivHorn.setImageResource(mRecordHolderController.mSendDrawable);
            }else {
                ivHorn.setImageResource(mRecordHolderController.mReceiveDrawable);
            }
        }
    }

    private void downloadVoiceAndPlay(MESSAGE model, boolean isRead, boolean play) {
        String voicePath = model.getContentText();
        String voiceUrl = model.getContentText();
        if (TextUtils.isEmpty(voicePath)) { // 没有声音文件的本地路径
            if (TextUtils.isEmpty(voiceUrl)) { // 没有声音文件的url
                Log.e(TAG, "----------voiceUrl is empty");
                if (play) {
                    ToastUtil.showMessage(mContext, mContext.getString(R.string.play_audio_fail));
                }
            } else {
                downLoadVoice(model, voiceUrl, voicePath, play, isRead);
            }
        } else {
            File file = new File(voicePath);
            if (file != null && file.exists()) {
                if (play) {
                    playVoice(model, voicePath, isRead);
                }
            } else {
                if (TextUtils.isEmpty(voiceUrl)) { // 没有声音url
                    Log.e(TAG, "----------voiceUrl is empty");
                    if (play) {
                        ToastUtil.showMessage(mContext, mContext.getString(R.string.play_audio_fail));
                    }
                } else {
                    downLoadVoice(model, voiceUrl, voicePath, play, isRead);
                }
            }
        }
    }

    /**
     * 下载声音文件，更新数据库，更新列表
     *
     * @param model     消息model
     * @param voiceUrl  声音文件的url
     * @param voicePath 声音文件的本地路径
     * @param isPlay    下载成功后是否要播放
     * @param isRead    是否已读，只有isPlay为true时才会把数据库更新为true
     */
    private void downLoadVoice(final MESSAGE model, String voiceUrl, String voicePath, final boolean isPlay,
                               final boolean isRead) {
        boolean flag = true;
        if (TextUtils.isEmpty(voicePath)) {
            flag = false;
            voicePath = SoundRecorder.createVoicePathWithUrl(voiceUrl);
        }
        final String path = voicePath;
        final boolean hasPath = flag;
        final long time = System.currentTimeMillis();
        if(isPlay){
            playVoice(model, path, isRead);
        }
        //TODO
        //下载文件
        Log.i(TAG, "----------time = "+time+"---voicePath = " + voicePath + "---url = " + voiceUrl);
    }


    /**
     * 播放声音文件，刷新列表，把未读标志为已读
     *
     * @param model     消息model
     * @param voicePath 声音文件本地路径
     * @param isRead    是否已读，如果未读，要更新数据库为已读
     * @return 如果更新了数据库，返回true；否则返回false
     */
    private boolean playVoice(MESSAGE model, String voicePath, boolean isRead) {
        try {
            // 请求audio焦点
            int result = mRecordHolderController.am.requestAudioFocus(mRecordHolderController.afChangeListener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // 应该在这里播放音乐文件的
            } else {
//				MyToast.showShortToast(mContext, R.string.play_audio_fail, null);
//				return false;
            }
            Log.i(TAG, "-----------path = "+voicePath+"-----result = "+result);
            mRecordHolderController.playVoice(voicePath, model.getMessageId(), mWeakHandler, getAdapterPosition());
            // 更新数据库
            if (!mIsSender && !isRead) {
                //TODO
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showMessage(mContext, mContext.getString(R.string.play_audio_fail));
            return false;
        }
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