package com.bet007.mobile.score.adapter.qiuyou;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.ImageView;

import com.bet007.mobile.score.common.ToastUtil;
import com.lqr.R;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 语音ViewHolder控制器
 */
public class RecordHolderController {
    public static final long DEFAULT_PLAYING_MSG_ID = -1;//默认正在播放的消息ID
    private Context mContext;
    private static RecordHolderController mInstance;

    private static MediaPlayer mMediaPlayer;
    public int mSendDrawable;
    public int mReceiveDrawable;
    public int mPlaySendAnim;
    public int mPlayReceiveAnim;
    public long mPlayMsgId = RecordHolderController.DEFAULT_PLAYING_MSG_ID;
    public boolean mLastIsSend = true;    //false:上次播放的是接收消息；true：上次播放的是发送消息
    public ImageView mHornView;
    public AnimationDrawable mHornAnimation;

    private boolean mIsEarPhoneOn;  //标识听筒模式，还没实现
    public AudioManager am;
    public AudioManager.OnAudioFocusChangeListener afChangeListener;
    private FileInputStream mFIS;
    private FileDescriptor mFD;

    public static synchronized RecordHolderController getInstance(Context context) {
        if(mInstance == null){
            synchronized (RecordHolderController.class){
                if(mInstance == null){
                    mInstance = new RecordHolderController(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 构造方法
     * @param context
     */
    private RecordHolderController(Context context) {
        mContext = context.getApplicationContext();

        mSendDrawable = R.drawable.chatscene_horn_to_2;
        mReceiveDrawable = R.drawable.chatscene_horn_from_2;
        mPlaySendAnim = R.drawable.chatscene_horn_to_sel;
        mPlayReceiveAnim = R.drawable.chatscene_horn_from_sel;

        am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        //audio焦点监听，如果失去焦点（比如电话来了、进行录音）就要停止播放
        afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    stopAndResetHolder();
                    abandonAudioFoucs();
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    stopAndResetHolder();
                    abandonAudioFoucs();
                }
            }
        };
    }

    //放弃监听焦点
    public void abandonAudioFoucs() {
        if (am != null && afChangeListener != null) {
            am.abandonAudioFocus(afChangeListener);
        }
    }

    /**
     * 播放声音
     * @param voicePath     声音文件本地路径
     * @param messageId     对应的消息ID
     * @param handler       Handler
     * @param position      消息所在列表的位置
     */
    public void playVoice(String voicePath, final long messageId, final Handler handler, final int position){
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.reset();
                    return false;
                }
            });
            mFIS = new FileInputStream(voicePath);
            mFD = mFIS.getFD();
            mMediaPlayer.setDataSource(mFD);
            if (mIsEarPhoneOn) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mPlayMsgId = messageId;
                    handler.obtainMessage(ChatMessageAdapter.NOTIFY_LIST_HORN, position, 0).sendToTarget();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mPlayMsgId = RecordHolderController.DEFAULT_PLAYING_MSG_ID;
                    resetHornView();
                }
            });

            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            ToastUtil.showMessage(mContext, mContext.getString(R.string.play_record_fail));
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

    /**
     * 停止播放声音，复位喇叭view状态，复位playMsgId
     */
    public void stopAndResetHolder(){
        mPlayMsgId = RecordHolderController.DEFAULT_PLAYING_MSG_ID;
        stopVoice();
        resetHornView();
    }

    /**
     * 停止播放声音
     */
    public void stopVoice(){
        try {
            if(mMediaPlayer != null){
                mMediaPlayer.release(); //直接release
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        mMediaPlayer = null;
    }

    /**
     * 复位喇叭view状态，复位playMsgId
     */
    public void resetHornView(){
        if(mHornAnimation != null){
            mHornAnimation.stop();
            mHornAnimation = null;
        }
        if(mLastIsSend){
            if(mHornView != null){
                mHornView.setImageResource(mSendDrawable);
            }
        }else {
            if(mHornView != null){
                mHornView.setImageResource(mReceiveDrawable);
            }
        }
        mHornView = null;
    }

    /**
     * 释放资源，销毁自己
     */
    public void destroy(){
        stopVoice();
        mHornView = null;
        mHornAnimation = null;
        abandonAudioFoucs();    //这里一点要调用，不然下次进来有可能出现首次播放声音失败
        mInstance = null;
    }

}
